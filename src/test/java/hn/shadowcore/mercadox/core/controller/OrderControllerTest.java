package hn.shadowcore.mercadox.core.controller;

//@MercadoXControllerTest
//class OrderControllerTest extends AbstractControllerTest {
//
//    @MockBean
//    private OrderUseCase orderUseCase;
//
//    @MockBean
//    private OrderQueryUseCase orderQueryUseCase;
//
//    @MockBean
//    private OrderMapper orderMapper;
//
//    @Test
//    void testInit() {
//        assertThat(orderQueryUseCase).isNotNull();
//        assertThat(orderQueryUseCase).isNotNull();
//        assertThat(orderMapper).isNotNull();
//    }
//
//    @Test
//    void shouldReturnOrderById() throws Exception {
//        OrderDto dto = OrderDto.builder()
//                .id("order-123")
//                .build();
//
//        when(orderQueryUseCase.findOrderById("order-123")).thenReturn(null);
//        when(orderMapper.toDto(any())).thenReturn(dto);
//
//        mockMvc.perform(get("/api/v1/orders/{orderId}", "order-123"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    void shouldPlaceOrder() throws Exception {
//        when(orderUseCase.place(any()))
//                .thenReturn(OrderStatus.IN_PROGRESS);
//
//        mockMvc.perform(post("/api/v1/orders/place")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(new PlaceOrderRequest())))
//                .andExpect(status().isCreated());
//    }
//
//}
