package hn.shadowcore.mercadoxcore.service;

import hn.shadowcore.mercadoxcontext.utils.KafkaProducerRecordFactory;
import hn.shadowcore.mercadoxcontext.utils.OrgIdContextHolder;
import hn.shadowcore.mercadoxcore.mapper.OrderMapper;
import hn.shadowcore.mercadoxcore.util.EmailDispatchUtils;
import hn.shadowcore.mercadoxcore.util.OrderUtils;
import hn.shadowcore.mercadoxlibrary.entity.model.auth.Organization;
import hn.shadowcore.mercadoxlibrary.entity.model.auth.User;
import hn.shadowcore.mercadoxlibrary.entity.model.core.Item;
import hn.shadowcore.mercadoxlibrary.entity.model.core.Location;
import hn.shadowcore.mercadoxlibrary.entity.model.core.Order;
import hn.shadowcore.mercadoxlibrary.entity.model.core.OrderItem;
import hn.shadowcore.mercadoxlibrary.entity.model.core.Shipment;
import hn.shadowcore.mercadoxlibrary.entity.model.enums.KafkaTopic;
import hn.shadowcore.mercadoxlibrary.entity.model.enums.OrderStatus;
import hn.shadowcore.mercadoxlibrary.entity.ports.incoming.OrderUseCase;
import hn.shadowcore.mercadoxlibrary.entity.request.DispatchOrderRequest;
import hn.shadowcore.mercadoxlibrary.entity.request.PlaceOrderRequest;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.CartDto;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.CartItemDto;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.EmailEventDto;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.EmailRecipientDto;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.OrderDto;
import hn.shadowcore.mercadoxlibrary.jpa.repository.OrderItemRepository;
import hn.shadowcore.mercadoxlibrary.jpa.repository.OrderRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public OrderStatus place(PlaceOrderRequest request) {

        if(isValidRequest(request.getCartDto())) {

            User user = userService.findActiveUserById(request.getCartDto().userId());
            Organization organization = organizationService.findActiveOrgById(UUID.fromString(OrgIdContextHolder.getTenantId()));
            Location location = locationService.findById(request.getLocationId());

            Shipment shipment = Shipment.builder()
                    .id(UUID.randomUUID())
                    .placedAt(Timestamp.valueOf(LocalDateTime.now()))
                    .location(location)
                    .customer(user)
                    .shipmentStatus(OrderStatus.UNDER_REVIEW)
                    .build();

            Order order = Order.builder()
                    .id(generateId())
                    .orderStatus(OrderStatus.UNDER_REVIEW)
                    .user(user)
                    .shipment(shipment)
                    .organization(organization)
                    .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                    .build();

            List<OrderItem> orderItems = orderUtils.buildOrderItems(request.getCartDto().cartItems(), order);

            orderRepository.save(order);
            orderItemRepository.saveAll(orderItems);

            List<EmailRecipientDto> recipientDtos = emailDispatchUtils.mapOrgAdminsToEmailRecipient();
            OrderDto orderDto = orderMapper.toDto(order);
            orderDto.setFriendlyUrl("http://");
            orderDto.setSearchUrl("http://");

            EmailEventDto<OrderDto> event = orderUtils.buildOrderEventDto(orderDto, "Order requested.", recipientDtos);

            ProducerRecord<String, Object> producerRecord = KafkaProducerRecordFactory
                    .buildWithOrgIdHeader(KafkaTopic.ORDER_PLACING, orderDto.getId(), event);

            emailDispatchUtils.sendEmail(producerRecord);
            return OrderStatus.UNDER_REVIEW;
        }
        log.error("Order was unable to be processed due to shorting in product stock.");
        return OrderStatus.CANCELLED;
    }

    @Override
    public OrderStatus dispatch(DispatchOrderRequest request) {

        Order order = orderRepository.findById(UUID.fromString(request.getOrderId()))
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("Order was not found for ID: '%s'", request.getOrderId())));

        for(OrderItem item : order.getItems()) {
            if(!isInStock(item.getItem().getId().toString(), item.getQuantity())) {
                throw new ResourceNotFoundException(String
                        .format("Item: '%s' was not found for quantity: '%s'", item.getItem().getName(), item.getQuantity()));
            }
            Item itemEntity = itemService.getItemDetails(String.valueOf(item.getItem().getId()));
            itemEntity.setUnitQuantity(itemEntity.getUnitQuantity() - item.getQuantity());
            validateAvailability(itemEntity);
            itemService.updateItem(itemEntity);
        }

        User deliveryEmployee = userService.findActiveUserById(request.getDeliveryEmployee());
        User customer = userService.findActiveUserById(request.getCustomer());
        order.setDispatchedBy(deliveryEmployee.getFullName());
        order.setDeliveryId(deliveryEmployee.getId().toString());
        order.setOrderStatus(OrderStatus.IN_PROGRESS);
        orderRepository.save(order);

        OrderDto orderDto = orderMapper.toDto(order);

        EmailRecipientDto delivery = new EmailRecipientDto(deliveryEmployee.getFirstName(), deliveryEmployee.getEmail());
        EmailRecipientDto customerEmail = new EmailRecipientDto(customer.getFirstName(), customer.getEmail());

        List<EmailRecipientDto> recipients = emailDispatchUtils.mapOrgAdminsToEmailRecipient();
        recipients.add(delivery);
        recipients.add(customerEmail);

        deliveryEmployee.setDriveAvailable(false);
        userService.saveUser(deliveryEmployee);

        EmailEventDto<OrderDto> emailDto = orderUtils.buildOrderEventDto(orderDto, "Order Confirmed.", recipients);

        ProducerRecord<String, Object> producerRecord = KafkaProducerRecordFactory
                .buildWithOrgIdHeader(KafkaTopic.ORDER_CONFIRMED, orderDto.getId(), emailDto);

        emailDispatchUtils.sendEmail(producerRecord);
        return OrderStatus.IN_PROGRESS;
    }

    @Override
    public OrderStatus close(String orderId) {
        Order order = fetchOrder(orderId);
        order.setOrderStatus(OrderStatus.CLOSED);

        User delivery = userService.findActiveUserById(order.getDeliveryId());
        delivery.setDriveAvailable(true);

        userService.saveUser(delivery);
        orderRepository.save(order);

        // TODO: Send invoice to User.
        return OrderStatus.CLOSED;
    }

    @Override
    public OrderStatus cancel(String orderId) {
        Order order = fetchOrder(orderId);
        List<EmailRecipientDto> recipients = emailDispatchUtils.mapOrgAdminsToEmailRecipient();

        if(OrderStatus.IN_PROGRESS.equals(order.getOrderStatus())) {
            User deliveryEmployee = userService.findActiveUserById(orderId);
            deliveryEmployee.setDriveAvailable(true);

            for(OrderItem orderItem: order.getItems()) {
                Item inventoryItem = itemService
                        .getItemDetails(orderItem.getItem().getId().toString());
                inventoryItem.setUnitQuantity(inventoryItem.getUnitQuantity() + orderItem.getQuantity());
                validateAvailability(inventoryItem);
                itemService.updateItem(inventoryItem);
            }

            EmailRecipientDto customerRecipient = emailDispatchUtils.mapSingleRecipient(deliveryEmployee);
            recipients.add(customerRecipient);
        }
        order.setOrderStatus(OrderStatus.CLOSED);
        orderRepository.save(order);

        User customer = userService.findActiveUserById(order.getUser().getId().toString());

        EmailRecipientDto customerRecipient = emailDispatchUtils.mapSingleRecipient(customer);
        recipients.add(customerRecipient);

        EmailEventDto<OrderDto> eventDto = orderUtils.buildOrderEventDto
                (orderMapper.toDto(order), "Order Cancelled.", recipients);

        ProducerRecord<String, Object> producerRecord = KafkaProducerRecordFactory
                .buildWithOrgIdHeader(KafkaTopic.ORDER_CANCELLED, order.getId(), eventDto);

        emailDispatchUtils.sendEmail(producerRecord);
        return OrderStatus.CANCELLED;
    }

    private Order fetchOrder(String orderId) {
        return orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("Order was not found for: '%s'", orderId)));
    }

    private boolean isValidRequest(CartDto cartDto) {
        for(CartItemDto item : cartDto.cartItems()) {
            if(isInStock(item.getCartItem().getId(), item.getQuantity())) {
                throw new IllegalArgumentException(String
                        .format("There isn't enough units for: '%s'", item.getCartItem().getName()));
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

    private String generateId() {
        String timestamp = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String suffix = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return new StringBuilder("ORD-").append(timestamp).append("-").append(suffix).toString();
    }

}
