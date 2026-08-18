package hn.shadowcore.mercadox.core.controller;

import hn.shadowcore.mercadox.core.mapper.OrderMapper;
import hn.shadowcore.mercadox.library.entity.avro.OrderPayload;
import hn.shadowcore.mercadox.library.entity.model.enums.OrderStatus;
import hn.shadowcore.mercadox.library.entity.ports.incoming.OrderQueryUseCase;
import hn.shadowcore.mercadox.library.entity.ports.incoming.OrderUseCase;
import hn.shadowcore.mercadox.library.entity.request.DispatchOrderRequest;
import hn.shadowcore.mercadox.library.entity.request.PlaceOrderRequest;
import hn.shadowcore.mercadox.library.entity.response.BaseResponseDto;
import hn.shadowcore.mercadox.library.entity.response.Response;
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

    @GetMapping("/{orderId}")
    public ResponseEntity<? extends Response<OrderPayload>> getOrderById(@PathVariable("orderId") String orderId) {
        BaseResponseDto<OrderPayload> response = new BaseResponseDto<>();
        OrderPayload orderDto = orderMapper.toDto(orderQueryUseCase.findOrderById(orderId));
        return response.buildResponseEntity(HttpStatus.OK, "Order returned successfully", orderDto);
    }

    @PostMapping("/place")
    @PreAuthorize("hasAnyRole('USER', 'ORG_ADMIN', 'ADMIN')")
    public ResponseEntity<? extends Response<OrderStatus>> placeOrder(@RequestBody PlaceOrderRequest orderRequest) {
        BaseResponseDto<OrderStatus> response = new BaseResponseDto<>();
        OrderStatus status = orderUseCase.place(orderRequest);
        return response.buildResponseEntity(HttpStatus.CREATED, "Order was placed successfully.", status);
    }

    @PostMapping("/dispatch")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<? extends Response<OrderStatus>> dispatchOrder(@RequestBody DispatchOrderRequest dispatchRequest) {
        BaseResponseDto<OrderStatus> dto = new BaseResponseDto<>();
        OrderStatus status = orderUseCase.dispatch(dispatchRequest);
        return dto.buildResponseEntity(HttpStatus.OK, "Order was dispatched.", status);
    }

    @PostMapping("/close")
    @PreAuthorize(("hasAnyRole('ORG_ADMIN', 'ADMIN')"))
    public ResponseEntity<? extends Response<OrderStatus>> closeOrder(@RequestParam(required = true) String orderId) {
        BaseResponseDto<OrderStatus> responseDto = new BaseResponseDto<>();
        OrderStatus status = orderUseCase.close(orderId);
        return responseDto.buildResponseEntity(HttpStatus.OK, "Order was closed successfully.", status);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<? extends Response<OrderStatus>> cancelOrder(@PathVariable String orderId) {
        BaseResponseDto<OrderStatus> responseDto = new BaseResponseDto<>();
        OrderStatus status = orderUseCase.cancel(orderId);
        return responseDto.buildResponseEntity(HttpStatus.OK, "Order deleted successfully.", status);
    }


}
