package com.example.service;

import com.example.exceptions.order.OrderNotFoundException;
import com.example.model.Order;
import com.example.repository.MainRepository;
import com.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawTypes")
public class OrderService extends MainService<Order> {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(MainRepository<Order> orderRepository) {
        this.orderRepository = (OrderRepository) orderRepository;
    }

    public void addOrder(Order order){
      orderRepository.addOrder(order);
    }

    public ArrayList<Order> getOrders(){
      return orderRepository.getOrders();
    }

    public Order getOrderById(UUID orderId) throws OrderNotFoundException {
      return orderRepository.getOrderById(orderId);
    }

    public void deleteOrderById(UUID orderId) throws OrderNotFoundException{
      orderRepository.deleteOrderById(orderId);
    }

}
