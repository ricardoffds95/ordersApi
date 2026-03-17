package com.example.orders;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderDTO findById(long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return new OrderDTO(order.get().getId(), order.get().getProduct(), order.get().getQuantity());
        }
        return null;
    }

    public Long createOrder(OrderDTO orderDTO){
        if(!validateOrder(orderDTO)) {
            throw new RuntimeException("Invalid order data");
        }
        Order order = new Order();
        order.setProduct(orderDTO.getProduct());
        order.setQuantity(orderDTO.getQuantity());
        return orderRepository.save(order).getId();
    }

    public void deleteOrder(long l) {
            if (orderRepository.existsById(l)) {
                orderRepository.deleteById(l);
            } else {
                throw new RuntimeException("Order not found");
            }
    }

    public Boolean validateOrder(OrderDTO orderDTO) {
        if(orderDTO.getProduct() == null || orderDTO.getProduct().isEmpty()) {
            return false;
        }
        return !(orderDTO.getQuantity() == null || orderDTO.getQuantity() <= 0);
    }

}
