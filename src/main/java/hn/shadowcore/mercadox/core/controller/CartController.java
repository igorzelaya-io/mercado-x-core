package hn.shadowcore.mercadox.core.controller;

import hn.shadowcore.mercadoxlibrary.entity.ports.incoming.CartUseCase;
import hn.shadowcore.mercadoxlibrary.entity.response.BaseResponseDto;
import hn.shadowcore.mercadoxlibrary.entity.response.Response;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.CartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
@PreAuthorize("permitAll()")
public class CartController {

    private final CartUseCase cartUseCase;

    @GetMapping("/{cartId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<? extends Response<CartDto>> getCartItems(@PathVariable String cartId) {
        BaseResponseDto<CartDto> response = new BaseResponseDto<>();
        CartDto dto = cartUseCase.getCartItems(cartId);
        return response.buildResponseEntity(HttpStatus.OK, "Item was saved successfully", dto);
    }

    @PostMapping("/{cartId}/item/{itemId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<? extends Response<CartDto>> addItemToCart(@PathVariable String cartId,
                                                                               @PathVariable String itemId) {
        BaseResponseDto<CartDto> response = new BaseResponseDto<>();
        CartDto updatedCart = cartUseCase.addToCart(cartId, itemId);
        return response.buildResponseEntity(HttpStatus.OK, "Cart was updated successfully", updatedCart);
    }

    @DeleteMapping("/{cartId}/item/{itemId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<? extends Response<CartDto>> removeItemFromCart(@PathVariable String cartId,
                                                                                    @PathVariable String itemId) {
        BaseResponseDto<CartDto> response = new BaseResponseDto<>();
        CartDto updatedCart = cartUseCase.removeFromCart(cartId, itemId);
        return response.buildResponseEntity(HttpStatus.OK, "Item removed from cart.", updatedCart);
    }

    @DeleteMapping("/{cartId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<? extends Response<String>> clearCart(@PathVariable String cartId) {
        BaseResponseDto<String> baseResponse = new BaseResponseDto<>();
        cartUseCase.clearCart(cartId);
        return baseResponse.buildResponseEntity
                (HttpStatus.OK, "Cart was cleared successfully.", "El carrito fue vaciado con éxito.");
    }

}
