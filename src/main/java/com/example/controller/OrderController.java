package com.example.controller;

import com.example.exceptions.order.OrderNotFoundException;
import com.example.model.Order;
import com.example.service.MainService;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(MainService<Order> orderService) {
        this.orderService = (OrderService) orderService;
    }

    @PostMapping("/")
    public void addOrder(@RequestBody Order order){
        orderService.addOrder((order));
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable UUID orderId){
      try{
        return orderService.getOrderById((orderId));
      }catch (OrderNotFoundException e){
        return null;
      }

    }

    @GetMapping("/")
    public ArrayList<Order> getOrders(){
        return orderService.getOrders();
    }

    @DeleteMapping("/delete/{orderId}")
    public String deleteOrderById(@PathVariable UUID orderId){
        try {
            orderService.deleteOrderById(orderId);
        } catch(OrderNotFoundException e){
            return e.getMessage();
        }
        return "Order deleted successfully";
    }
}
