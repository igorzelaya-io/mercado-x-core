package hn.shadowcore.mercadoxcore.util;


import hn.shadowcore.mercadoxcore.service.ItemService;
import hn.shadowcore.mercadoxlibrary.entity.model.core.Item;
import hn.shadowcore.mercadoxlibrary.entity.model.core.Order;
import hn.shadowcore.mercadoxlibrary.entity.model.core.OrderItem;
import hn.shadowcore.mercadoxlibrary.entity.model.core.OrderItemsKey;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.CartItemDto;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.EmailEventDto;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.EmailRecipientDto;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.OrderDto;
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

    public EmailEventDto<OrderDto> buildOrderEventDto(OrderDto order, String eventSubject,
                                                       List<EmailRecipientDto> recipients) {
        return new EmailEventDto<>(UUID.randomUUID(), eventSubject, recipients, order, Instant.now());
    }

    public List<OrderItem> buildOrderItems(List<CartItemDto> cartItems, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItemDto cartItem : cartItems) {
            final String itemId = cartItem.getCartItem().getId();
            OrderItemsKey key = new OrderItemsKey(UUID.fromString(itemId), order.getId());
            Item itemEntity = itemService.getItemDetails(itemId);
            OrderItem orderItem = new OrderItem(key, itemEntity, order, cartItem.getQuantity());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

}
