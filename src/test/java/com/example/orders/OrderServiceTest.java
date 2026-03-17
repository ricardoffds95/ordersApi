package com.example.orders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void findById_shouldReturnDTO_whenOrderExists() {
        Optional<Order> order = Optional.of(new Order());
        order.get().setId(1L);
        order.get().setProduct("Laptop");
        order.get().setQuantity(2);

        when(orderRepository.findById(1L)).thenReturn(order);

        OrderDTO result = orderService.findById(1L);
        assertEquals(order.get().getId(), result.getId());
        assertEquals(order.get().getProduct(), result.getProduct());
        assertEquals(order.get().getQuantity(), result.getQuantity());
    }

    @Test
    void insertOrder_shouldCreateAndReturnOrderId() {
        Order order = new Order();
        order.setId(1L);
        order.setProduct("Laptop");
        order.setQuantity(2);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Long result = orderService.createOrder(new OrderDTO(order.getId(), order.getProduct(), order.getQuantity()));
        assertEquals(order.getId(), result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void DeleteOrder_shouldDeleteIfOrderExists() {

        when(orderRepository.existsById(1L)).thenReturn(true);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void DeleteOrder_shouldReturnExceptionIfOrderDoesNotExist() {

        when(orderRepository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> orderService.deleteOrder(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found");
    }

    @Test
    void getOrderById_RestService() {
        Optional<Order> order = Optional.of(new Order());
        order.get().setId(1L);
        order.get().setProduct("Laptop");
        order.get().setQuantity(2);

        when(orderRepository.findById(1L)).thenReturn(order);
        OrderController controller = new OrderController(orderService);

        ResponseEntity<Object> response = controller.getOrderById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(((OrderDTO) response.getBody()).getId()).isEqualTo(1L);
    }

    @Test
    void getOrderById_RestService_shouldReturnError() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        OrderController controller = new OrderController(orderService);
        ResponseEntity<Object> response = controller.getOrderById(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void saveOrder_RestService() {
        OrderDTO orderDTO = new OrderDTO(null, "Laptop", 2);

        when(orderRepository.save(any(Order.class))).thenReturn(new Order());
        OrderController controller = new OrderController(orderService);

        ResponseEntity<Void> response = controller.saveOrder(orderDTO);
        verify(orderRepository, times(1)).save(any(Order.class));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void saveOrder_RestService_shouldReturnErrorForInvalidData() {
        OrderDTO orderDTO = new OrderDTO(null, "", 0);

        OrderController controller = new OrderController(orderService);

        ResponseEntity<Void> response = controller.saveOrder(orderDTO);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteOrder_RestService() {

        when(orderRepository.existsById(1L)).thenReturn(true);

        OrderController controller = new OrderController(orderService);

        ResponseEntity<Void> response = controller.deleteOrder(1L);
        verify(orderRepository, times(1)).deleteById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteOrder_RestService_shouldReturnError() {

        when(orderRepository.existsById(1L)).thenReturn(false);

        OrderController controller = new OrderController(orderService);

        ResponseEntity<Void> response = controller.deleteOrder(1L);
        verify(orderRepository, times(1)).existsById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
