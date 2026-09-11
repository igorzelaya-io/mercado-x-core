package hn.alturaforge.mercadox.core.util;

import hn.alturaforge.mercadox.core.service.ItemService;
import hn.alturaforge.mercadox.library.entity.avro.EmailRecipient;
import hn.alturaforge.mercadox.library.entity.avro.OrderEmailEvent;
import hn.alturaforge.mercadox.library.entity.avro.OrderPayload;
import hn.alturaforge.mercadox.library.entity.model.core.Item;
import hn.alturaforge.mercadox.library.entity.model.core.Order;
import hn.alturaforge.mercadox.library.entity.model.core.OrderItem;
import hn.alturaforge.mercadox.library.entity.model.core.OrderItemsKey;
import hn.alturaforge.mercadox.library.entity.response.dto.ItemDto;
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

    public OrderEmailEvent buildOrderEventDto(OrderPayload order, String eventSubject,
                                              String emailTemplate,
                                              List<EmailRecipient> recipients) {
        return OrderEmailEvent.newBuilder()
                .setEventId(order.getId())
                .setEventSubject(eventSubject)
                .setEmailTemplate(hn.alturaforge.mercadox.library.entity.avro.NotificationTemplateName
                        .valueOf(emailTemplate))
                .setRecipients(recipients)
                .setPayload(order)
                .setTimestamp(Instant.now())
                .build();
    }

    public List<OrderItem> buildOrderItems(List<ItemDto> cartItems, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (ItemDto cartItem : cartItems) {
            final String itemId = cartItem.getId();
            OrderItemsKey key = new OrderItemsKey(UUID.fromString(itemId), order.getId());
            Item itemEntity = itemService.getItemDetails(itemId);
            OrderItem orderItem = new OrderItem(key, itemEntity, order, cartItem.getUnitQuantity());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

}
