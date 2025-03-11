package com.example.repository;

import com.example.model.Order;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class UserRepository extends MainRepository<User> {

    @Value("${spring.application.userDataPath}")
    private String defaultUserDataPath;

    public UserRepository() {}

    @Override
    protected String getDataPath() {
        String envPath = System.getenv("USER_DATA_PATH");
        if (envPath != null && !envPath.isEmpty()) {
            return envPath;
        } else {
            return defaultUserDataPath;
        }
    }

    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }

    public ArrayList<User> getUsers() {
        return findAll();
    }

    public User getUserById(UUID userId) {
        return findAll().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public User addUser(User user) {
        ArrayList<User> users = findAll();

        // Ensure the user has a unique ID
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }

        users.add(user);
        saveAll(users);
        return user;
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        return findAll().stream()
                .filter(user -> user.getId().equals(userId))
                .map(User::getOrders)
                .findFirst()
                .orElse(new ArrayList<>());
    }

    public void addOrderToUser(UUID userId, Order order) {
        ArrayList<User> users = findAll();

        for (User user : users) {
            if (user.getId().equals(userId)) {
                user.getOrders().add(order);
                saveAll(users);
                return;
            }
        }
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
        ArrayList<User> users = findAll();

        for (User user : users) {
            if (user.getId().equals(userId)) {
                user.getOrders().removeIf(order -> order.getId().equals(orderId));
                saveAll(users);
                return;
            }
        }
    }

    public void deleteUserById(UUID userId) {
        ArrayList<User> users = findAll();
        users.removeIf(user -> user.getId().equals(userId));
        saveAll(users);
    }
}
