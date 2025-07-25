package hn.shadowcore.mercadoxcore.mapper;


import hn.shadowcore.mercadoxlibrary.entity.model.core.Order;
import hn.shadowcore.mercadoxlibrary.entity.model.core.OrderItem;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.OrderDto;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .createdAt(order.getCreatedAt().toLocalDateTime().toString())
                .customerName(order.getUser().getFullName())
                .customerEmail(order.getUser().getEmail())
                .shippingAddress(order.getShipment().getLocation().getAddress())
                .locationReference(order.getShipment().getLocation().getLocationReference())
                .organization(order.getOrganization().getName())
                .itemNames(order.getItems().stream()
                        .map(oi -> oi.getItem().getName())
                        .toList())
                .quantity(order.getItems().stream()
                        .mapToInt(OrderItem::getQuantity)
                        .sum())
                .build();
    }


}
