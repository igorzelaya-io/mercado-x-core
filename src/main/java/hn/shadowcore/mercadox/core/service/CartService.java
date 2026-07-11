package hn.shadowcore.mercadox.core.service;

import hn.shadowcore.mercadox.core.mapper.ItemMapper;
import hn.shadowcore.mercadox.library.entity.ports.incoming.CartUseCase;
import hn.shadowcore.mercadox.library.entity.response.dto.CartDto;
import hn.shadowcore.mercadox.library.entity.response.dto.ItemDto;
import hn.shadowcore.mercadox.library.jpa.repository.ItemRepository;
import hn.shadowcore.mercadox.library.redis.repository.CartRedisRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService implements CartUseCase {

    private final CartRedisRepository cartRepository;

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    @Override
    public CartDto addToCart(String userId, String itemId) {
        ItemDto itemDto = itemRepository.findById(UUID.fromString(itemId))
                .map(itemMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Item with ID: '%s' was not found", itemId)));

        CartDto cart = getCartItems(userId);

        cart.cartItems()
                .stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .ifPresentOrElse(
                        i -> i.setUnitQuantity(i.getUnitQuantity() + 1),
                        () -> cart.cartItems().add(itemDto)
                );

        cartRepository.saveCart(cart);
        return cart;
    }

    @Override
    public CartDto removeFromCart(String cartId, String itemId) {

        List<ItemDto> cartItems = cartRepository.getCart(cartId)
                .cartItems()
                .stream()
                .filter(i -> !i.getId().equals(itemId))
                .toList();

        CartDto dto = new CartDto(cartId, cartItems);
        cartRepository.saveCart(dto);

        return dto;
    }

    @Override
    public CartDto getCartItems(String cartId) {
        return cartRepository.getCart(cartId);
    }

    @Override
    public void clearCart(String cartId) {
        cartRepository.clearCart(cartId);
    }
}
