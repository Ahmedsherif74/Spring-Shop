package com.example.service;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.MainRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@SuppressWarnings("rawtypes")
public class UserService extends MainService<User> {

    private final UserRepository userRepository;
    private final OrderService orderService;
    private final CartService cartService;

    @Autowired
    public UserService(MainRepository<User> userRepository, MainService<Order> orderService, MainService<Cart> cartService) {
        this.userRepository = (UserRepository) userRepository;
        this.orderService = (OrderService) orderService;
        this.cartService = (CartService) cartService;
    }

    public User addUser(User user) {
        return userRepository.addUser(user);
    }

    public ArrayList<User> getUsers() {
        return userRepository.getUsers();
    }

    public User getUserById(UUID userId) {
        return userRepository.getUserById(userId);
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        return userRepository.getOrdersByUserId(userId);
    }

    public void addOrderToUser(UUID userId) {
        Cart cart = cartService.getCartByUserId(userId);

        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }

        List<Product> products = cart.getProducts();

        double totalPrice = 0;
        for (Product product : products) {
            totalPrice += product.getPrice();
        }

        Order order = new Order(userId, totalPrice, products);
        orderService.addOrder(order);
        userRepository.addOrderToUser(userId, order);

        emptyCart(userId);
    }

    public void emptyCart(UUID userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }
        for (Product product : cart.getProducts()) {
            cartService.deleteProductFromCart(cart.getId(), product);
        }
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
        userRepository.removeOrderFromUser(userId, orderId);
    }

    public void deleteUserById(UUID userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteUserById(userId);
    }
}
