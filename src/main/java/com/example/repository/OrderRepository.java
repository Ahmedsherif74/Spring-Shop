package com.example.repository;

import com.example.exceptions.order.OrderNotFoundException;
import com.example.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class OrderRepository extends MainRepository<Order>{

  @Value("${spring.application.orderDataPath}")
  private String defaultOrderDataPath;

  public OrderRepository() {}

  @Override
  protected String getDataPath() {
    String envPath = System.getenv("ORDER_DATA_PATH");
    if (envPath != null && !envPath.isEmpty()) {
      return envPath;
    } else {
      return defaultOrderDataPath;
    }
  }

  @Override
  protected Class<Order[]> getArrayType() {
    return Order[].class;
  }

  public void addOrder(Order order){
    this.save(order);
  }

  public ArrayList<Order> getOrders(){
    return this.findAll();
  }

  public Order getOrderById(UUID orderId) throws OrderNotFoundException {
    return this.findAll().stream()
      .filter(o -> o.getId().compareTo(orderId) == 0)
      .findFirst()
      .orElseThrow(()->new OrderNotFoundException("Order not found"));
  }

  public void deleteOrderById(UUID orderId) throws OrderNotFoundException {
    ArrayList<Order> orders = this.findAll();

    boolean isDeleted = orders.removeIf(o -> o.getId().equals(orderId));  // Remove order by ID

    if(!isDeleted)
      throw new OrderNotFoundException("Order not found");

    this.saveAll(orders);
  }

}
