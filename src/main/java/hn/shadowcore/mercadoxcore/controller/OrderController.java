package hn.shadowcore.mercadoxcore.controller;

import hn.shadowcore.mercadoxcore.mapper.OrderMapper;
import hn.shadowcore.mercadoxlibrary.entity.model.enums.OrderStatus;
import hn.shadowcore.mercadoxlibrary.entity.ports.incoming.OrderQueryUseCase;
import hn.shadowcore.mercadoxlibrary.entity.ports.incoming.OrderUseCase;
import hn.shadowcore.mercadoxlibrary.entity.request.DispatchOrderRequest;
import hn.shadowcore.mercadoxlibrary.entity.request.PlaceOrderRequest;
import hn.shadowcore.mercadoxlibrary.entity.response.BaseResponseDto;
import hn.shadowcore.mercadoxlibrary.entity.response.Response;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("permitAll()")
@RequestMapping(value = "/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase orderUseCase;

    private final OrderQueryUseCase orderQueryUseCase;

    private final OrderMapper orderMapper;

    @GetMapping(value = "/{orderId}")
    private ResponseEntity<? extends Response<OrderDto>> getOrderById(@PathVariable String orderId) {
        BaseResponseDto<OrderDto> response = new BaseResponseDto<>();
        OrderDto orderDto = orderMapper.toDto(orderQueryUseCase.findOrderById(orderId));
        return response.buildResponseEntity(HttpStatus.OK, "Order returned successfully", orderDto);
    }

    @PostMapping("/place")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ORG_ADMIN ', 'ROLE_ADMIN')")
    public ResponseEntity<? extends Response<OrderStatus>> placeOrder(@RequestBody PlaceOrderRequest orderRequest) {
        BaseResponseDto<OrderStatus> response = new BaseResponseDto<>();
        OrderStatus status = orderUseCase.place(orderRequest);
        return response.buildResponseEntity(HttpStatus.CREATED, "Order was placed successfully.", status);
    }

    @PostMapping("/dispatch")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<? extends Response<OrderStatus>> dispatchOrder(@RequestBody DispatchOrderRequest dispatchRequest) {
        BaseResponseDto<OrderStatus> dto = new BaseResponseDto<>();
        OrderStatus status = orderUseCase.dispatch(dispatchRequest);
        return dto.buildResponseEntity(HttpStatus.OK, "Order was dispatched.", status);
    }

    @PostMapping("/close")
    @PreAuthorize(("hasAnyRole('ROLE_ORG_ADMIN', 'ROLE_ADMIN')"))
    public ResponseEntity<? extends Response<OrderStatus>> closeOrder(@RequestParam(required = true) String orderId) {
        BaseResponseDto<OrderStatus> responseDto = new BaseResponseDto<>();
        OrderStatus status = orderUseCase.close(orderId);
        return responseDto.buildResponseEntity(HttpStatus.OK, "Order was closed successfully.", status);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<? extends Response<OrderStatus>> cancelOrder(@PathVariable String orderId) {
        BaseResponseDto<OrderStatus> responseDto = new BaseResponseDto<>();
        OrderStatus status = orderUseCase.cancel(orderId);
        return responseDto.buildResponseEntity(HttpStatus.OK, "Order deleted successfully.", status);
    }


}
