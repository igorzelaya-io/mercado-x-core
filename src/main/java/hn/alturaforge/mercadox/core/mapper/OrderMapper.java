package hn.alturaforge.mercadox.core.mapper;

import hn.alturaforge.mercadox.library.entity.avro.OrderPayload;
import hn.alturaforge.mercadox.library.entity.model.core.Order;
import hn.alturaforge.mercadox.library.entity.model.core.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderPayload toDto(Order order) {
        return OrderPayload.newBuilder()
                .setId(order.getId())
                .setCreatedAt(order.getCreatedAt().toLocalDateTime().toString())
                .setCustomerName(order.getUser().getFullName())
                .setEmail(order.getUser().getEmail())
                .setShippingAddress(order.getShipment().getLocation().getAddress())
                .setLocationReference(order.getShipment().getLocation().getLocationReference())
                .setOrganization(order.getOrganization().getName())
                .setItemNames(order.getItems().stream()
                        .map(oi -> oi.getItem().getName())
                        .toList())
                .setQuantity(order.getItems().stream()
                        .mapToInt(OrderItem::getQuantity)
                        .sum())
                .build();
    }

}
