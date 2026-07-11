package hn.shadowcore.mercadox.core.util;

import hn.shadowcore.mercadox.core.service.ItemService;
import hn.shadowcore.mercadox.library.entity.model.core.Item;
import hn.shadowcore.mercadox.library.entity.model.core.Order;
import hn.shadowcore.mercadox.library.entity.model.core.OrderItem;
import hn.shadowcore.mercadox.library.entity.model.core.OrderItemsKey;
import hn.shadowcore.mercadox.library.entity.model.enums.NotificationTemplateName;
import hn.shadowcore.mercadox.library.entity.response.dto.EmailEventDto;
import hn.shadowcore.mercadox.library.entity.response.dto.EmailRecipientDto;
import hn.shadowcore.mercadox.library.entity.response.dto.ItemDto;
import hn.shadowcore.mercadox.library.entity.response.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderUtils {

    private final ItemService itemService;

    public EmailEventDto<OrderDto> buildOrderEventDto(OrderDto order, String eventSubject, NotificationTemplateName emailTemplate,
                                                       List<EmailRecipientDto> recipients) {
        EmailEventDto<OrderDto> event = new EmailEventDto<>(eventSubject, emailTemplate, recipients, order, Instant.now());
        event.setEventId(order.getId());
        return event;
    }

    public List<OrderItem> buildOrderItems(List<ItemDto> cartItems, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for(ItemDto cartItem : cartItems) {
            final String itemId = cartItem.getId();
            OrderItemsKey key = new OrderItemsKey(UUID.fromString(itemId), order.getId());
            Item itemEntity = itemService.getItemDetails(itemId);
            OrderItem orderItem = new OrderItem(key, itemEntity, order, cartItem.getUnitQuantity());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

}
