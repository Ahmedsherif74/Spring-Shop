package com.example.exceptions.order;

public class OrderNotFoundException extends RuntimeException {
    private String message = "Order not found";

    public OrderNotFoundException() {}

    public OrderNotFoundException(String msg) {
      super(msg);
      this.message = msg;
    }
}

