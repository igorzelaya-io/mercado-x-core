package hn.shadowcore.mercadoxcore.service;

import hn.shadowcore.mercadoxcore.mapper.ItemMapper;
import hn.shadowcore.mercadoxlibrary.entity.ports.incoming.CartUseCase;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.CartDto;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.CartItemDto;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.ItemDto;
import hn.shadowcore.mercadoxlibrary.jpa.repository.CartRedisRepository;
import hn.shadowcore.mercadoxlibrary.jpa.repository.ItemRepository;
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
                .filter(i -> i.getCartItem().getId().equals(itemId))
                .findFirst()
                .ifPresentOrElse(
                        i -> i.setQuantity(i.getQuantity() + 1),
                        () -> cart.cartItems().add(new CartItemDto(itemDto, 1))
                );

        cartRepository.saveCart(userId, cart);
        return cart;
    }

    @Override
    public CartDto removeFromCart(String cartId, String itemId) {

        List<CartItemDto> cartItems = cartRepository.getCart(cartId)
                .cartItems()
                .stream()
                .filter(i -> !i.getCartItem().getId().equals(itemId))
                .toList();

        CartDto dto = new CartDto(cartId, cartItems);
        cartRepository.saveCart(cartId, dto);

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
