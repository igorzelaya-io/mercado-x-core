package hn.shadowcore.mercadox.core.service;

import hn.shadowcore.mercadoxlibrary.entity.model.core.Order;
import hn.shadowcore.mercadoxlibrary.entity.model.enums.OrderStatus;
import hn.shadowcore.mercadoxlibrary.entity.ports.incoming.OrderQueryUseCase;
import hn.shadowcore.mercadoxlibrary.jpa.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryService implements OrderQueryUseCase {

    private final OrderRepository orderRepository;

    @Override
    @Cacheable(value = "reviewOrders")
    public List<Order> findAllUnderReview() {
        return orderRepository.findAllUnderReview();
    }

    @Override
    public Order findOrderById(String s) {
        return orderRepository.findById(String.valueOf(s))
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("Order was not found for ID: '%s'", s)));
    }

    @Override
    public List<Order> findAllForUserAndStatus(String s, OrderStatus orderStatus) {
        return orderRepository.findOrdersByUserAndStatus(s, orderStatus);
    }
}
