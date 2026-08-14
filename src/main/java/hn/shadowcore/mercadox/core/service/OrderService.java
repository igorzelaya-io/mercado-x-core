package hn.shadowcore.mercadox.core.service;

import hn.shadowcore.mercadox.context.utils.KafkaProducerRecordFactory;
import hn.shadowcore.mercadox.context.utils.OrgIdContextHolder;
import hn.shadowcore.mercadox.core.mapper.OrderMapper;
import hn.shadowcore.mercadox.core.util.EmailDispatchUtils;
import hn.shadowcore.mercadox.core.util.OrderUtils;
import hn.shadowcore.mercadox.library.entity.model.auth.Organization;
import hn.shadowcore.mercadox.library.entity.model.auth.User;
import hn.shadowcore.mercadox.library.entity.model.core.Item;
import hn.shadowcore.mercadox.library.entity.model.core.Location;
import hn.shadowcore.mercadox.library.entity.model.core.Order;
import hn.shadowcore.mercadox.library.entity.model.core.OrderItem;
import hn.shadowcore.mercadox.library.entity.model.core.Shipment;
import hn.shadowcore.mercadox.library.entity.model.enums.NotificationTemplateName;
import hn.shadowcore.mercadox.library.entity.model.enums.OrderStatus;
import hn.shadowcore.mercadox.library.entity.model.enums.kafka.KafkaTopic;
import hn.shadowcore.mercadox.library.entity.ports.incoming.OrderUseCase;
import hn.shadowcore.mercadox.library.entity.request.DispatchOrderRequest;
import hn.shadowcore.mercadox.library.entity.request.PlaceOrderRequest;
import hn.shadowcore.mercadox.library.entity.response.dto.CartDto;
import hn.shadowcore.mercadox.library.entity.response.dto.EmailEventDto;
import hn.shadowcore.mercadox.library.entity.response.dto.EmailRecipientDto;
import hn.shadowcore.mercadox.library.entity.response.dto.ItemDto;
import hn.shadowcore.mercadox.library.entity.response.dto.OrderDto;
import hn.shadowcore.mercadox.library.jpa.repository.OrderItemRepository;
import hn.shadowcore.mercadox.library.jpa.repository.OrderRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import hn.shadowcore.mercadox.context.utils.annotations.IdempotentOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService implements OrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final OrderMapper orderMapper;

    private final ItemService itemService;

    private final LocationService locationService;

    private final OrganizationService organizationService;

    private final UserService userService;

    private final EmailDispatchUtils emailDispatchUtils;

    private final OrderUtils orderUtils;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, OrderMapper orderMapper,
                        ItemService itemService, LocationService locationService, OrganizationService organizationService,
                        UserService userService, EmailDispatchUtils emailDispatchUtils, OrderUtils orderUtils) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderItemRepository = orderItemRepository;
        this.itemService = itemService;
        this.locationService = locationService;
        this.organizationService = organizationService;
        this.userService = userService;
        this.emailDispatchUtils = emailDispatchUtils;
        this.orderUtils = orderUtils;
    }

    @Override
    @IdempotentOperation(ttlMinutes = 15, keyPrefix = "order:place:")
    public OrderStatus place(PlaceOrderRequest request) {

        if(isValidRequest(request.getCartDto())) {

            User user = userService.findActiveUserById(request.getCartDto().userId());

            Organization organization = organizationService
                    .findActiveOrgById(UUID.fromString(OrgIdContextHolder.getTenantId()));

            Location location = locationService.findById(request.getLocationId());

            Shipment shipment = Shipment.builder()
                    .id(UUID.randomUUID()).placedAt(Timestamp.valueOf(LocalDateTime.now()))
                    .location(location).customer(user).shipmentStatus(OrderStatus.UNDER_REVIEW)
                    .build();

            Order order = Order.builder()
                    .id(Order.generateId()).orderStatus(OrderStatus.UNDER_REVIEW)
                    .user(user).shipment(shipment).organization(organization)
                    .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                    .build(); // UNDER_REVIEW is the initial state — no transition needed

            List<OrderItem> orderItems = orderUtils.buildOrderItems(request.getCartDto().cartItems(), order);

            orderRepository.save(order);
            orderItemRepository.saveAll(orderItems);

            List<EmailRecipientDto> recipientDtos = emailDispatchUtils.mapOrgAdminsToEmailRecipient();
            OrderDto orderDto = orderMapper.toDto(order);
            orderDto.setFriendlyUrl("http://");
            orderDto.setSearchUrl("http://");

            EmailEventDto<OrderDto> event = orderUtils.buildOrderEventDto(orderDto, "Order requested.",
                    NotificationTemplateName.ORDER_REQUEST_TEMPLATE,recipientDtos);

            ProducerRecord<String, Object> producerRecord = KafkaProducerRecordFactory
                    .buildWithOrgIdHeader(KafkaTopic.ORDER_PLACING, orderDto.getId(), event);

            emailDispatchUtils.sendEmail(producerRecord);
            return OrderStatus.UNDER_REVIEW;
        }
        log.error("Order was unable to be processed due to shorting in product stock.");
        return OrderStatus.CANCELLED;
    }

    @Override
    @Transactional
    public OrderStatus dispatch(DispatchOrderRequest request) {

        Order order = orderRepository.findById(String.valueOf(request.getOrderId()))
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("Order was not found for ID: '%s'", request.getOrderId())));

        order.getItems().stream()
                .sorted(Comparator.comparing(oi -> oi.getItem().getId()))
                .forEach(oi -> {
                    Item itemEntity = itemService.getItemDetailsForUpdate(String.valueOf(oi.getItem().getId()));
                    if(itemEntity.getUnitQuantity() < oi.getQuantity()) {
                        throw new ResourceNotFoundException(String
                                .format("Item: '%s' has insufficient stock for quantity: '%s'",
                                        oi.getItem().getName(), oi.getQuantity()));
                    }
                    itemEntity.setUnitQuantity(itemEntity.getUnitQuantity() - oi.getQuantity());
                    validateAvailability(itemEntity);
                    itemService.updateItem(itemEntity);
                });

        User deliveryEmployee = userService.findActiveUserById(request.getDeliveryEmployee());
        User customer = userService.findActiveUserById(request.getCustomer());
        order.setDispatchedBy(deliveryEmployee.getFullName());
        order.setDeliveryId(deliveryEmployee.getId().toString());
        order.setOrderStatus(order.getOrderStatus().transitionTo(OrderStatus.IN_PROGRESS));
        orderRepository.save(order);

        OrderDto orderDto = orderMapper.toDto(order);

        List<EmailRecipientDto> recipients = emailDispatchUtils.mapOrgAdminsToEmailRecipient();
        emailDispatchUtils.mapSingleRecipient(deliveryEmployee).ifPresent(recipients::add);
        emailDispatchUtils.mapSingleRecipient(customer).ifPresent(recipients::add);

        deliveryEmployee.setDriveAvailable(false);
        userService.saveUser(deliveryEmployee);

        EmailEventDto<OrderDto> emailDto = orderUtils.buildOrderEventDto(orderDto, "Order Confirmed.",
                NotificationTemplateName.ORDER_CONFIRMATION_TEMPLATE, recipients);

        ProducerRecord<String, Object> producerRecord = KafkaProducerRecordFactory
                .buildWithOrgIdHeader(KafkaTopic.ORDER_CONFIRMED, orderDto.getId(), emailDto);

        emailDispatchUtils.sendEmail(producerRecord);
        return OrderStatus.IN_PROGRESS;
    }

    @Override
    public OrderStatus close(String orderId) {
        Order order = fetchOrder(orderId);
        order.setOrderStatus(order.getOrderStatus().transitionTo(OrderStatus.CLOSED));

        User delivery = userService.findActiveUserById(order.getDeliveryId());
        delivery.setDriveAvailable(true);

        userService.saveUser(delivery);
        orderRepository.save(order);

        // TODO: Send invoice to User.
        return OrderStatus.CLOSED;
    }

    @Override
    @Transactional
    public OrderStatus cancel(String orderId) {
        Order order = fetchOrder(orderId);
        List<EmailRecipientDto> recipients = emailDispatchUtils.mapOrgAdminsToEmailRecipient();

        if(OrderStatus.IN_PROGRESS.equals(order.getOrderStatus())) {
            User deliveryEmployee = userService.findActiveUserById(orderId);
            deliveryEmployee.setDriveAvailable(true);

            order.getItems().stream()
                    .sorted(Comparator.comparing(oi -> oi.getItem().getId()))
                    .forEach(oi -> {
                        Item inventoryItem = itemService
                                .getItemDetailsForUpdate(oi.getItem().getId().toString());
                        inventoryItem.setUnitQuantity(inventoryItem.getUnitQuantity() + oi.getQuantity());
                        validateAvailability(inventoryItem);
                        itemService.updateItem(inventoryItem);
                    });

            emailDispatchUtils.mapSingleRecipient(deliveryEmployee).ifPresent(recipients::add);
        }
        order.setOrderStatus(order.getOrderStatus().transitionTo(OrderStatus.CANCELLED));
        orderRepository.save(order);

        User customer = userService.findActiveUserById(order.getUser().getId().toString());

        emailDispatchUtils.mapSingleRecipient(customer).ifPresent(recipients::add);

        EmailEventDto<OrderDto> eventDto = orderUtils.buildOrderEventDto
                (orderMapper.toDto(order), "Order Cancelled.",
                        NotificationTemplateName.ORDER_CANCELLATION_TEMPLATE, recipients);

        ProducerRecord<String, Object> producerRecord = KafkaProducerRecordFactory
                .buildWithOrgIdHeader(KafkaTopic.ORDER_CANCELLED, order.getId(), eventDto);

        emailDispatchUtils.sendEmail(producerRecord);
        return OrderStatus.CANCELLED;
    }

    private Order fetchOrder(String orderId) {
        return orderRepository.findById(String.valueOf(orderId))
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("Order was not found for: '%s'", orderId)));
    }

    private boolean isValidRequest(CartDto cartDto) {
        for(ItemDto item : cartDto.cartItems()) {
            if(!isInStock(item.getId(), item.getUnitQuantity())) {
                throw new IllegalArgumentException(String
                        .format("There isn't enough units for: '%s'", item.getName()));
            }
        }
        return true;
    }

    private boolean isInStock(String itemId, int quantity) {
        return itemService.isInStock(itemId, quantity);
    }

    private void validateAvailability(Item item) {
        if(item.getUnitQuantity() <= 0) {
            item.setInStock(false);
        }
        else if(item.getUnitQuantity() > 0) {
            item.setInStock(true);
        }
    }

}
