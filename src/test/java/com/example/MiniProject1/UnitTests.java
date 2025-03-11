package com.example.MiniProject1;

import com.example.exceptions.order.OrderNotFoundException;
import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.service.CartService;
import com.example.service.OrderService;
import com.example.service.ProductService;
import com.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UnitTests {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Test
    void contextLoads() {}

    // UserService tests

    @Test
    void addUser_withValidInput_ShouldReturnSameName() {
        // Arrange
        User newUser = new User("Mahmoud", new ArrayList<>());

        // Act
        User addedUser = userService.addUser(newUser);

        // Assert
        assertEquals(addedUser.getName(), newUser.getName(), "Name is not the same");
    }

    @Test
    void addUser_withoutId_ShouldGenerateId() {
        // Arrange
        User newUser = new User("Mahmoud", new ArrayList<>());

        // Act
        User addedUser = userService.addUser(newUser);

        // Assert
        assertNotNull(addedUser.getId(), "User ID should be automatically generated.");
    }

    @Test
    void addUser_twoUsers_ShouldHaveDifferentIds() {
        // Arrange
        User newUser1 = new User("Mahmoud", new ArrayList<>());
        User newUser2 = new User("Ahmed", new ArrayList<>());

        // Act
        User addedUser1 = userService.addUser(newUser1);
        User addedUser2 = userService.addUser(newUser2);

        // Assert
        assertNotNull(addedUser1.getId(), "First user's ID should be generated.");
        assertNotNull(addedUser2.getId(), "Second user's ID should be generated.");
        assertNotEquals(addedUser1.getId(), addedUser2.getId(),
                "Each user should have a unique ID.");
    }

    @Test
    void getUsers_whenUsersExist_ShouldReturnAllUsers() {
        // Arrange
        userService.addUser(new User("Mahmoud", new ArrayList<>()));
        userService.addUser(new User("Ahmed", new ArrayList<>()));

        // Act
        ArrayList<User> users = userService.getUsers();

        // Assert
        assertNotNull(users);
        assertTrue(users.size() >= 2);
    }


    @Test
    void getUsers_afterAddingUsers_ShouldReflectChanges() {
        // Arrange
        userService.addUser(new User("Abo7meeeed", new ArrayList<>()));

        // Act
        ArrayList<User> users = userService.getUsers();

        // Assert
        assertTrue(users.stream().anyMatch(user -> user.getName().equals("Abo7meeeed")));
    }

    @Test
    void getUsers_afterAddingUser_ShouldIncreaseCount() {
        // Arrange
        int initialSize = userService.getUsers().size();
        userService.addUser(new User("Ahmed", new ArrayList<>()));

        // Act
        ArrayList<User> users = userService.getUsers();

        // Assert
        assertNotNull(users);
        assertEquals(initialSize + 1, users.size());
    }

    @Test
    void getUserById_existingUser_ShouldReturnUser() {
        // Arrange
        User newUser = userService.addUser(new User("Ahmed", new ArrayList<>()));

        // Act
        User foundUser = userService.getUserById(newUser.getId());

        // Assert
        assertNotNull(foundUser);
        assertEquals(newUser.getId(), foundUser.getId());
    }

    @Test
    void getUserById_nonExistingUser_ShouldReturnNull() {
        // Arrange
        UUID fakeUserId = UUID.randomUUID();

        // Act
        User foundUser = userService.getUserById(fakeUserId);

        // Assert
        assertNull(foundUser);
    }

    @Test
    void getUserById_afterDeletingUser_ShouldReturnNull() {
        // Arrange
        User newUser = userService.addUser(new User("Ahmed", new ArrayList<>()));
        userService.deleteUserById(newUser.getId());

        // Act & Assert
        assertNull(userService.getUserById(newUser.getId()));
    }

    @Test
    void getOrdersByUserId_shouldReturnEmptyListForNewUser() {
        // Arrange
        User newUser = userService.addUser(new User("Ahmed", new ArrayList<>()));

        // Act
        List<Order> orders = userService.getOrdersByUserId(newUser.getId());

        // Assert
        assertNotNull(orders);
        assertTrue(orders.isEmpty(), "New user should have no orders");
    }

    @Test
    void getOrdersByUserId_forNonExistingUser_ShouldReturnEmptyList() {
        // Arrange
        UUID fakeUserId = UUID.randomUUID();

        // Act
        List<Order> orders = userService.getOrdersByUserId(fakeUserId);

        // Assert
        assertNotNull(orders);
        assertTrue(orders.isEmpty(), "Non-existing user should have no orders");
    }

    @Test
    void getOrdersByUserId_afterAddingOrder_ShouldReturnCorrectOrders() {
        // Arrange
        User newUser = userService.addUser(new User("Mahmoud", new ArrayList<>()));
        List<Product> products = List.of(
                new Product("Laptop", 1200.0),
                new Product("Mouse", 50.0));
        cartService.addCart(new Cart(newUser.getId(), products));
        userService.addOrderToUser(newUser.getId());

        // Act
        List<Order> orders = userService.getOrdersByUserId(newUser.getId());

        // Assert
        assertNotNull(orders);
        assertFalse(orders.isEmpty(), "User should have at least one order");
    }

/*
    @Test
    void getOrdersByUserId_existingUserWithOrders_ShouldReturnOrders() {
        User newUser = userService.addUser(new User("Ahmed", new ArrayList<>()));
        userService.addOrderToUser(newUser.getId());
        List<Order> orders = userService.getOrdersByUserId(newUser.getId());
        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }

    @Test
    void getOrdersByUserId_existingUserWithoutOrders_ShouldReturnEmptyList() {
        User newUser = userService.addUser(new User("Ahmed", new ArrayList<>()));
        List<Order> orders = userService.getOrdersByUserId(newUser.getId());
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    void getOrdersByUserId_nonExistingUser_ShouldReturnEmptyList() {
        UUID fakeUserId = UUID.randomUUID();
        List<Order> orders = userService.getOrdersByUserId(fakeUserId);
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }
     */

    @Test
    void addOrderToUser_validCart_shouldCreateOrder() {
        // Arrange
        User newUser = userService.addUser(new User("Mahmoud", new ArrayList<>()));
        List<Product> products = List.of(new Product("Laptop", 1200.0), new Product("Mouse", 50.0));
        cartService.addCart(new Cart(newUser.getId(), products));

        // Act
        userService.addOrderToUser(newUser.getId());

        // Assert
        List<Order> userOrders = orderService.getOrders().stream()
                .filter(order -> order.getUserId().equals(newUser.getId()))
                .toList();
        assertFalse(userOrders.isEmpty(), "Order should be created");
        Order lastOrder = userOrders.get(userOrders.size() - 1);
        assertEquals(2, lastOrder.getProducts().size(),
                "Order should contain the correct number of products");
    }

    @Test
    void addOrderToUser_validCart_shouldEmptyCart() {
        // Arrange
        User newUser = userService.addUser(new User("Mahmoud", new ArrayList<>()));
        List<Product> products = List.of(
                new Product("TV", 1400.0),
                new Product("Monitor", 350.0));
        cartService.addCart(new Cart(newUser.getId(), products));

        // Act
        userService.addOrderToUser(newUser.getId());

        // Assert
        Cart updatedCart = cartService.getCartByUserId(newUser.getId());
        assertTrue(updatedCart.getProducts().isEmpty(),
                "Cart should be empty after order is placed");
    }

    @Test
    void addOrderToUser_validCart_shouldCalculateTotalPriceCorrectly() {
        // Arrange
        User newUser = userService.addUser(new User("Mahmoud", new ArrayList<>()));
        List<Product> products = List.of(
                new Product("Monitor", 300.0),
                new Product("Keyboard", 100.0),
                new Product("Headphones", 50.0)
        );
        cartService.addCart(new Cart(newUser.getId(), products));

        // Act
        userService.addOrderToUser(newUser.getId());

        // Assert
        List<Order> userOrders = orderService.getOrders().stream()
                .filter(order -> order.getUserId().equals(newUser.getId()))
                .toList();
        assertFalse(userOrders.isEmpty(), "Order should be created");
        Order lastOrder = userOrders.get(userOrders.size() - 1);
        assertEquals(450.0, lastOrder.getTotalPrice(),
                "Total price should match sum of product prices");
    }

    @Test
    void emptyCart_existingCart_shouldReturnEmptyCart() {
        // Arrange
        User newUser = userService.addUser(new User("Ahmed", new ArrayList<>()));
        List<Product> products = new ArrayList<>();
        products.add(new Product("Laptop", 1000.0));
        products.add(new Product("Mouse", 50.0));
        cartService.addCart(new Cart(newUser.getId(), products));

        // Act
        userService.emptyCart(newUser.getId());

        // Assert
        Cart updatedCart = cartService.getCartByUserId(newUser.getId());
        assertNotNull(updatedCart);
        assertTrue(updatedCart.getProducts().isEmpty(),
                "Cart should be empty after calling emptyCart");
    }

    @Test
    void emptyCart_alreadyEmptyCart_shouldRemainEmpty() {
        // Arrange
        User newUser = userService.addUser(new User("Mahmoud", new ArrayList<>()));
        cartService.addCart(new Cart(newUser.getId(), new ArrayList<>()));

        // Act
        userService.emptyCart(newUser.getId());

        // Assert
        Cart updatedCart = cartService.getCartByUserId(newUser.getId());
        assertNotNull(updatedCart);
        assertTrue(updatedCart.getProducts().isEmpty(),
                "Cart should remain empty after calling emptyCart on an empty cart");
    }

    @Test
    void emptyCart_oneUserCartShouldNotAffectAnotherUsersCart() {
        // Arrange
        User user1 = userService.addUser(new User("User1", new ArrayList<>()));
        User user2 = userService.addUser(new User("User2", new ArrayList<>()));

        List<Product> productsUser1 = List.of(new Product("Phone", 800.0));
        List<Product> productsUser2 = List.of(new Product("Tablet", 500.0));

        cartService.addCart(new Cart(user1.getId(), productsUser1));
        cartService.addCart(new Cart(user2.getId(), productsUser2));

        // Act
        userService.emptyCart(user1.getId());

        // Assert
        Cart updatedCart1 = cartService.getCartByUserId(user1.getId());
        Cart updatedCart2 = cartService.getCartByUserId(user2.getId());

        assertTrue(updatedCart1.getProducts().isEmpty(),
                "User1's cart should be empty");
        assertFalse(updatedCart2.getProducts().isEmpty(),
                "User2's cart should not be affected");
    }

    @Test
    void removeOrderFromUser_existingOrder_shouldRemoveOrder() {
        // Arrange
        User newUser = userService.addUser(new User("Mahmoud", new ArrayList<>()));
        List<Product> products = List.of(
                new Product("Laptop", 1400.0),
                new Product("Mouse", 100.0));
        cartService.addCart(new Cart(newUser.getId(), products));
        userService.addOrderToUser(newUser.getId());
        List<Order> userOrders = orderService.getOrders().stream()
                .filter(o -> o.getUserId().equals(newUser.getId()))
                .toList();
        Order lastOrder = userOrders.get(userOrders.size() - 1);

        // Act
        userService.removeOrderFromUser(newUser.getId(), lastOrder.getId());

        // Assert
        List<Order> updatedUserOrders = userService.getOrdersByUserId(newUser.getId());
        assertFalse(updatedUserOrders.stream().anyMatch(o -> o.getId().equals(lastOrder.getId())),
                "Order should have been removed from user orders");
    }

    @Test
    void removeOrderFromUser_nonExistentOrder_shouldNotRemoveOtherUsersOrder() {
        // Arrange
        User newUser = userService.addUser(new User("Mahmoud", new ArrayList<>()));
        List<Product> products = List.of(
                new Product("Laptop", 1400.0),
                new Product("Mouse", 100.0));
        cartService.addCart(new Cart(newUser.getId(), products));
        userService.addOrderToUser(newUser.getId());
        UUID nonExistentOrderId = UUID.randomUUID();

        // Act
        userService.removeOrderFromUser(newUser.getId(), nonExistentOrderId);

        // Assert
        List<Order> userOrders = userService.getOrdersByUserId(newUser.getId());
        assertFalse(userOrders.isEmpty(), "Existing orders should remain unchanged");
    }

    @Test
    void removeOrderFromUser_shouldNotAffectOtherOrders() {
        // Arrange
        User newUser = userService.addUser(new User("Mahmoud", new ArrayList<>()));
        List<Product> products1 = List.of(new Product("Laptop", 1400.0));
        List<Product> products2 = List.of(
                new Product("Mouse", 100.0),
                new Product("Keyboard", 200.0));
        Cart cart = new Cart(newUser.getId(), products1);
        cartService.addCart(cart);
        userService.addOrderToUser(newUser.getId());
        for (Product product : products2) {
            cartService.addProductToCart(cart.getId(), product);
        }
        userService.addOrderToUser(newUser.getId());

        List<Order> userOrders = orderService.getOrders().stream()
                .filter(o -> o.getUserId().equals(newUser.getId()))
                .toList();
        Order orderToRemove = userOrders.get(0);
        Order remainingOrder = userOrders.get(1);

        // Act
        userService.removeOrderFromUser(newUser.getId(), orderToRemove.getId());

        // Assert
        List<Order> updatedUserOrders = userService.getOrdersByUserId(newUser.getId());
        assertFalse(updatedUserOrders.stream()
                        .anyMatch(o -> o.getId().equals(orderToRemove.getId())),
                "Removed order should not exist anymore");
        assertTrue(updatedUserOrders.stream().anyMatch(o -> o.getId().equals(remainingOrder.getId())),
                "Other orders should remain unaffected");
    }

    @Test
    void deleteUserById_existingUser_shouldDeleteSuccessfully() {
        // Arrange
        User newUser = userService.addUser(new User("Mahmoud", new ArrayList<>()));

        // Act
        userService.deleteUserById(newUser.getId());

        // Assert
        assertNull(userService.getUserById(newUser.getId()),
                "User should be deleted and not found in the system.");
    }

    @Test
    void deleteUserById_nonExistingUser_shouldThrowException() {
        // Arrange
        UUID fakeUserId = UUID.randomUUID();

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.deleteUserById(fakeUserId),
                "Should throw an exception when deleting a non-existing user.");
    }

    @Test
    void deleteUserById_multipleUsers_shouldDeleteOnlySpecifiedUser() {
        // Arrange
        User user1 = userService.addUser(new User("Mahmoud", new ArrayList<>()));
        User user2 = userService.addUser(new User("Ahmed", new ArrayList<>()));

        // Act
        userService.deleteUserById(user1.getId());

        // Assert
        assertNull(userService.getUserById(user1.getId()),
                "Deleted user should not be found.");
        assertNotNull(userService.getUserById(user2.getId()),
                "Other users should not be affected.");
    }

    // CartService tests
    @Test
    void addCart_NewCart_AddedSuccessfully() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Cart cart = new Cart(cartId, userId, new ArrayList<>());

        // Act
        Cart addedCart = cartService.addCart(cart);

        //  Assert
        assertNotNull(addedCart, "The cart should not be null after being added.");
        assertEquals(cartId, addedCart.getId(), "The cart ID should match the expected ID.");
        assertEquals(userId, addedCart.getUserId(), "The cart should belong to the correct user.");
    }

    @Test
    void addCart_RetrievableById() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, UUID.randomUUID(), new ArrayList<>());

        cartService.addCart(cart);

        // Act
        Cart retrievedCart = cartService.getCartById(cartId);

        // Assert
        assertNotNull(retrievedCart, "The cart should be retrievable after being added.");
        assertEquals(cartId, retrievedCart.getId(), "The cart ID should match.");
    }

    @Test
    void addCart_MultipleCarts_StoredSeparately() {
        // Arrange
        UUID cartId1 = UUID.randomUUID();
        UUID cartId2 = UUID.randomUUID();

        Cart cart1 = new Cart(cartId1, UUID.randomUUID(), new ArrayList<>());
        Cart cart2 = new Cart(cartId2, UUID.randomUUID(), new ArrayList<>());

        cartService.addCart(cart1);
        cartService.addCart(cart2);

        // Act
        Cart retrievedCart1 = cartService.getCartById(cartId1);
        Cart retrievedCart2 = cartService.getCartById(cartId2);

        // Assert
        assertNotNull(retrievedCart1, "Cart1 should exist.");
        assertNotNull(retrievedCart2, "Cart2 should exist.");
        assertNotEquals(retrievedCart1.getId(), retrievedCart2.getId(), "Each cart should have a unique ID.");
    }

    @Test
    void getCarts_MultipleCartsExist_CorrectCount() {
        // Arrange
        for (Cart c : cartService.getCarts()) {
            cartService.deleteCartById(c.getId());
        }

        // Add two new carts
        Cart cart1 = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        Cart cart2 = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());

        cartService.addCart(cart1);
        cartService.addCart(cart2);

        // Act
        ArrayList<Cart> carts = cartService.getCarts();

        //  Assert
        assertEquals(2, carts.size(), "Cart list should contain exactly two carts.");
        assertTrue(carts.stream().anyMatch(c -> c.getId().equals(cart1.getId())), "Cart list should contain Cart1.");
        assertTrue(carts.stream().anyMatch(c -> c.getId().equals(cart2.getId())), "Cart list should contain Cart2.");
    }

    @Test
    void getCarts_NotDuplicateCarts() {
        // Arrange
        for (Cart c : cartService.getCarts()) {
            cartService.deleteCartById(c.getId());
        }

        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        cartService.addCart(cart);

        // Act
        ArrayList<Cart> firstCall = cartService.getCarts();
        ArrayList<Cart> secondCall = cartService.getCarts();
        ArrayList<Cart> thirdCall = cartService.getCarts();

        // Assert
        assertEquals(firstCall.size(), secondCall.size(), "Cart count should not change between calls.");
        assertEquals(secondCall.size(), thirdCall.size(), "Cart count should remain consistent.");
    }

    @Test
    void getCarts_CartsWithCorrectUserId() {
        // Arrange
        for (Cart c : cartService.getCarts()) {
            cartService.deleteCartById(c.getId());
        }

        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        Cart user1Cart1 = new Cart(UUID.randomUUID(), userId1, new ArrayList<>());
        Cart user1Cart2 = new Cart(UUID.randomUUID(), userId1, new ArrayList<>());
        Cart user2Cart = new Cart(UUID.randomUUID(), userId2, new ArrayList<>());

        cartService.addCart(user1Cart1);
        cartService.addCart(user1Cart2);
        cartService.addCart(user2Cart);

        // Act
        ArrayList<Cart> carts = cartService.getCarts();

        //  Assert
        assertTrue(carts.stream().anyMatch(c -> c.getUserId().equals(userId1)), "There should be carts for User 1.");
        assertTrue(carts.stream().anyMatch(c -> c.getUserId().equals(userId2)), "There should be a cart for User 2.");
    }

    @Test
    void getCartById_existingCart_ReturnCart() {
        // Arrange
        Cart newCart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        cartService.addCart(newCart);

        // Act
        Cart foundCart = cartService.getCartById(newCart.getId());

        // Assert
        assertNotNull(foundCart);
        assertEquals(newCart.getId(), foundCart.getId());
    }

    @Test
    void getCartById_NonExistingCart_ShouldReturnNull() {
        // Arrange
        UUID cartId = UUID.randomUUID();

        // Act
        Cart foundCart = cartService.getCartById(cartId);

        // Assert
        assertNull(foundCart);
    }

    @Test
    void getCartById_MultipleCarts_ReturnCorrectCart() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Cart firstCart = new Cart(UUID.randomUUID(), userId, new ArrayList<>());
        Cart secondCart = new Cart(UUID.randomUUID(), userId, new ArrayList<>());

        cartService.addCart(firstCart);
        cartService.addCart(secondCart);

        // Act
        Cart foundCart = cartService.getCartById(secondCart.getId());

        // Assert
        assertNotNull(foundCart);
        assertEquals(secondCart.getId(), foundCart.getId());
        assertEquals(userId, foundCart.getUserId()); // Ensure it's the right user's cart
    }

    @Test
    void getCartByUserId_ExistingUser_ReturnCart() {
        // Arrange: Create a cart for a user
        UUID userId = UUID.randomUUID();
        Cart userCart = new Cart(UUID.randomUUID(), userId, new ArrayList<>());

        cartService.addCart(userCart);

        // Act: Retrieve the cart by user ID
        Cart foundCart = cartService.getCartByUserId(userId);

        // Assert: The cart should exist and belong to the user
        assertNotNull(foundCart);
        assertEquals(userId, foundCart.getUserId());
    }

    @Test
    void getCartByUserId_MultipleUsers_ShouldReturnCorrectCart() {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        Cart user1Cart = new Cart(UUID.randomUUID(), userId1, new ArrayList<>());
        Cart user2Cart = new Cart(UUID.randomUUID(), userId2, new ArrayList<>());

        cartService.addCart(user1Cart);
        cartService.addCart(user2Cart);

        // Act
        Cart foundCart = cartService.getCartByUserId(userId2);

        // Assert
        assertNotNull(foundCart);
        assertEquals(userId2, foundCart.getUserId());
    }

    @Test
    void getCartByUserId_NoCartForUser_ReturnNull() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        Cart foundCart = cartService.getCartByUserId(userId);

        // Assert
        assertNull(foundCart);
    }

    @Test
    void addProductToCart_ExistingCart_ContainProduct() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, UUID.randomUUID(), new ArrayList<>());
        cartService.addCart(cart);

        // Arrange
        Product product = new Product(UUID.randomUUID(), "Laptop", 1200.00);

        // Act
        cartService.addProductToCart(cartId, product);


        Cart updatedCart = cartService.getCartById(cartId);

        // Assert
        assertNotNull(updatedCart);
        assertFalse(updatedCart.getProducts().isEmpty()); // Ensure it's not empty
        assertTrue(updatedCart.getProducts().stream()
                .anyMatch(p -> p.getId().equals(product.getId()))); // Product must exist in cart
    }

    @Test
    void addProductToCart_NonExistingCart_DoNothing() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        Product product = new Product(UUID.randomUUID(), "Smartphone", 799.99);

        // Act & Assert
        assertDoesNotThrow(() -> cartService.addProductToCart(cartId, product));


        Cart result = cartService.getCartById(cartId);
        assertNull(result);
    }


    @Test
    void addProductToCart_MultipleProducts_StoreAllProducts() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, UUID.randomUUID(), new ArrayList<>());
        cartService.addCart(cart);

        // Arrange
        Product product1 = new Product(UUID.randomUUID(), "Gaming Console", 500.00);
        Product product2 = new Product(UUID.randomUUID(), "Wireless Headphones", 250.00);

        // Act
        cartService.addProductToCart(cartId, product1);
        cartService.addProductToCart(cartId, product2);

        Cart updatedCart = cartService.getCartById(cartId);


        System.out.println("Updated Cart: " + updatedCart);
        System.out.println("Products in Cart: " + updatedCart.getProducts());

        // Assert
        assertNotNull(updatedCart, "The cart should exist after adding products.");


        assertEquals(2, updatedCart.getProducts().size(), "The cart should contain exactly 2 products.");

        // Verify product presence using stream matching (avoiding object reference mismatch)
        assertTrue(updatedCart.getProducts().stream().anyMatch(p -> p.getName().equals("Gaming Console")));
        assertTrue(updatedCart.getProducts().stream().anyMatch(p -> p.getName().equals("Wireless Headphones")));
    }

    @Test
    void deleteProductFromCart_ExistingProduct_RemoveProduct() {
        // Arrange: Create a cart and add it to the service
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, UUID.randomUUID(), new ArrayList<>());

        Product product = new Product(UUID.randomUUID(), "Laptop", 1500.00);
        cart.getProducts().add(product);
        cartService.addCart(cart);

        // Act: Remove the product from the cart
        cartService.deleteProductFromCart(cartId, product);

        // Retrieve updated cart
        Cart updatedCart = cartService.getCartById(cartId);

        // Assert: The cart should exist, but the product should be removed
        assertNotNull(updatedCart, "Cart should still exist after removing the product.");
        assertFalse(updatedCart.getProducts().contains(product), "The product should be removed from the cart.");
    }

    @Test
    void deleteProductFromCart_NonExistingCart_ThrowException() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        Product product = new Product(UUID.randomUUID(), "Monitor", 200.00);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartService.deleteProductFromCart(cartId, product));
    }


    @Test
    void deleteProductFromCart_OnlyProductInCart_LeaveCartEmpty() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, UUID.randomUUID(), new ArrayList<>());

        Product product = new Product(UUID.randomUUID(), "Smartphone", 1000.00);
        cart.getProducts().add(product);

        cartService.addCart(cart);

        // Act
        cartService.deleteProductFromCart(cartId, product);

        Cart updatedCart = cartService.getCartById(cartId);

        // Assert
        assertNotNull(updatedCart, "Cart should still exist after deleting the product.");
        assertEquals(0, updatedCart.getProducts().size(), "Cart should be empty after removing the only product.");
    }

    @Test
    void deleteCartById_ExistingCart_RemoveCart() {
        //Arrange
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, UUID.randomUUID(), new ArrayList<>());
        cartService.addCart(cart);

        // Act:
        cartService.deleteCartById(cartId);

        // Assert
        assertNull(cartService.getCartById(cartId), "Cart should be deleted and return null.");
    }

    @Test
    void deleteCartById_NonExistingCart_DoNothing() {
        // Arrange
        UUID cartId = UUID.randomUUID();

        // Act & Assert
        assertDoesNotThrow(() -> cartService.deleteCartById(cartId));
    }

    @Test
    void deleteCartById_OneCartDeleted_NotAffectOtherCarts() {
        // Arrange
        UUID cartId1 = UUID.randomUUID();
        UUID cartId2 = UUID.randomUUID();

        Cart cart1 = new Cart(cartId1, UUID.randomUUID(), new ArrayList<>());
        Cart cart2 = new Cart(cartId2, UUID.randomUUID(), new ArrayList<>());

        cartService.addCart(cart1);
        cartService.addCart(cart2);

        //Act
        cartService.deleteCartById(cartId1);

        // Assert
        assertNull(cartService.getCartById(cartId1), "Cart1 should be deleted.");
        assertNotNull(cartService.getCartById(cartId2), "Cart2 should still exist.");
    }

    // OrderService tests
    @Test
    public void addOrder_shouldAddOrderWithCorrectId() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ArrayList<Product> orderProducts = new ArrayList<>();
        double orderTotalPrice = 0;
        Order order = new Order(orderId,userId, orderTotalPrice,orderProducts);

        //Act
        orderService.addOrder(order);
        Order retrievedOrder = orderService.getOrders().getLast();

        //Assert
        assertEquals(orderId, retrievedOrder.getId(),"Order ID should match");
        assertEquals(orderTotalPrice, retrievedOrder.getTotalPrice(),"Order Total Price should match");

    }

    @Test
    public void addOrder_withoutId_ShouldGenerateId() {
      // Arrange
      UUID userId = UUID.randomUUID();
      ArrayList<Product> orderProducts = new ArrayList<>();
      double orderTotalPrice = 0;
      Order order = new Order(userId, orderTotalPrice,orderProducts);

      //Act
      orderService.addOrder(order);
      Order retrievedOrder = orderService.getOrders().getLast();

      //Assert
      assertNotNull(retrievedOrder.getId(),"Order ID should match");

    }

    @Test
    public void addOrder_ShouldAddOrderWithEmptyProductsArray_WhenProductsNotSpecified() {
      // Arrange
      UUID userId = UUID.randomUUID();
      Order order = new Order(userId);

      //Act
      orderService.addOrder(order);
      Order retrievedOrder = orderService.getOrders().getLast();

      //Assert
      assertNotNull(retrievedOrder.getProducts());
      assertEquals(retrievedOrder.getProducts().size(),0);
    }

    @Test
    public void getOrders_ordersShouldIncrease_AfterAddingOneOrder() {
      // Arrange
      UUID userId = UUID.randomUUID();
      ArrayList<Product> orderProducts = new ArrayList<>();
      double orderTotalPrice = 0;
      Order order = new Order(userId, orderTotalPrice,orderProducts);
      int ordersSizeBeforeAdding = orderService.getOrders().size();

      // Act
      orderService.addOrder(order);
      ArrayList<Order> allOrders = orderService.getOrders();

      // Assert
      assertEquals(allOrders.size(), ordersSizeBeforeAdding + 1);
    }

    @Test
    public void getOrders_ShouldReturnOneOrder_WhenOrdersFileHasOneOrder() {
        // Arrange - Clear previous orders
        List<Order> ordersBefore = orderService.getOrders();
        for (Order order : ordersBefore) {
            orderService.deleteOrderById(order.getId());
        }

        // Add a single order
        Order newOrder = new Order(UUID.randomUUID(), 100.0, new ArrayList<>());
        orderService.addOrder(newOrder);

        // Act
        List<Order> orders = orderService.getOrders();

        // Assert
        assertNotNull(orders, "Orders list should not be null");
        assertEquals(1, orders.size(), "Orders list should contain one order");

    }

    @Test
    public void getOrders_ShouldReturnEmptyArray_WhenOrdersFileHasNoOrders() {
        // Arrange
        List<Order> ordersBefore = orderService.getOrders();
        for (Order order : ordersBefore) {
            orderService.deleteOrderById(order.getId());
        }

        // Act
        List<Order> ordersAfter = orderService.getOrders();

        // Assert
        assertNotNull(ordersAfter, "Orders list should not be null");
        assertTrue(ordersAfter.isEmpty(), "Orders list should be empty");

    }

    @Test
    public void findOrderById_ShouldReturnOrder_WhenOrderExists() {
      UUID orderId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      ArrayList<Product> orderProducts = new ArrayList<>();
      double orderTotalPrice = 0;
      Order order = new Order(orderId, userId, orderTotalPrice,orderProducts);

      //Act
      orderService.addOrder(order);
      Order retrievedOrder = null;

      try{
        retrievedOrder = orderService.getOrderById(orderId);
      }catch (Exception e){}

      assertNotNull(retrievedOrder,"Order found successfully");
    }

    @Test
    public void findOrderById_ShouldReturnException_WhenOrderDoesNotExist() {
      UUID orderId = UUID.randomUUID();

      //Act
      OrderNotFoundException exception = assertThrows(
        OrderNotFoundException.class,  // Expected exception type
        () -> orderService.getOrderById(orderId)  // The code that should throw the exception
      );

      //Assert
      assertEquals("Order not found", exception.getMessage());
    }

    @Test
    public void findOrderById_ShouldReturnCorrectOrderDetails_WhenOrderExists() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ArrayList<Product> orderProducts = new ArrayList<>();
        double orderTotalPrice = 250.75;
        Order order = new Order(orderId, userId, orderTotalPrice, orderProducts);
        orderService.addOrder(order);

        // Act
        Order retrievedOrder = orderService.getOrderById(orderId);

        // Assert
        assertNotNull(retrievedOrder, "Order should be found");
        assertEquals(orderId, retrievedOrder.getId(), "Order ID should match");
        assertEquals(userId, retrievedOrder.getUserId(), "User ID should match");
        assertEquals(orderTotalPrice, retrievedOrder.getTotalPrice(), "Total price should match");
    }


    @Test
    public void deleteOrderById_ShouldDeleteOrder_WhenOrderExists() {
      UUID orderId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      ArrayList<Product> orderProducts = new ArrayList<>();
      double orderTotalPrice = 0;
      Order order = new Order(orderId, userId, orderTotalPrice,orderProducts);

      //Act
      orderService.addOrder(order);
      orderService.deleteOrderById(orderId);

      //Assert
      OrderNotFoundException exception = assertThrows(
        OrderNotFoundException.class,  // Expected exception type
        () -> orderService.getOrderById(orderId)  // The code that should throw the exception
      );
    }

    @Test
    public void deleteOrderById_ShouldReturnException_WhenOrderDoesNotExist() {
      UUID orderId = UUID.randomUUID();

      //Act
      OrderNotFoundException exception = assertThrows(
        OrderNotFoundException.class,  // Expected exception type
        () -> orderService.deleteOrderById(orderId)  // The code that should throw the exception
      );

      //Assert
      assertEquals("Order not found", exception.getMessage());
    }

    @Test
    public void deleteOrderById_ShouldReduceOrderCount_WhenMultipleOrdersExist() {
        // Arrange
        Order order1 = new Order(UUID.randomUUID(), UUID.randomUUID(), 150.0, new ArrayList<>());
        Order order2 = new Order(UUID.randomUUID(), UUID.randomUUID(), 250.0, new ArrayList<>());
        Order order3 = new Order(UUID.randomUUID(), UUID.randomUUID(), 350.0, new ArrayList<>());

        orderService.addOrder(order1);
        orderService.addOrder(order2);
        orderService.addOrder(order3);

        int initialSize = orderService.getOrders().size();

        // Act
        orderService.deleteOrderById(order2.getId());
        List<Order> remainingOrders = orderService.getOrders();

        // Assert
        assertNotNull(remainingOrders, "Orders list should not be null");
        assertEquals(initialSize - 1, remainingOrders.size(), "Order count should decrease by one");
        assertFalse(remainingOrders.contains(order2), "Deleted order should not exist in the list");
    }

    // ProductService tests
    @Test
    void addProduct_withValidInput_ShouldReturnSameProduct() {
        // Arrange
        Product newProduct = new Product("Laptop", 1500.0);

        // Act
        Product addedProduct = productService.addProduct(newProduct);

        // Assert
        assertEquals(newProduct.getName(), addedProduct.getName(), "Product name should match");
        assertEquals(newProduct.getPrice(), addedProduct.getPrice(), "Product price should match");
    }

    @Test
    void addProduct_withoutId_ShouldGenerateId() {
        // Arrange
        Product newProduct = new Product("Phone", 800.0);

        // Act
        Product addedProduct = productService.addProduct(newProduct);

        // Assert
        assertNotNull(addedProduct.getId(), "Product ID should be automatically generated");
    }

    @Test
    void addProduct_twoProducts_ShouldHaveDifferentIds() {
        // Arrange
        Product product1 = new Product("Tablet", 300.0);
        Product product2 = new Product("Monitor", 200.0);

        // Act
        Product addedProduct1 = productService.addProduct(product1);
        Product addedProduct2 = productService.addProduct(product2);

        // Assert
        assertNotNull(addedProduct1.getId(), "First product ID should be generated");
        assertNotNull(addedProduct2.getId(), "Second product ID should be generated");
        assertNotEquals(addedProduct1.getId(), addedProduct2.getId(),
                "Each product should have a unique ID");
    }

    @Test
    void getProducts_whenProductsExist_ShouldReturnAllProducts() {
        // Arrange
        productService.addProduct(new Product("Mouse", 50.0));
        productService.addProduct(new Product("Keyboard", 100.0));

        // Act
        ArrayList<Product> products = productService.getProducts();

        // Assert
        assertNotNull(products);
        assertTrue(products.size() >= 2);
    }

    @Test
    void getProducts_afterAddingProducts_ShouldReflectChanges() {
        // Arrange
        productService.addProduct(new Product("Headphones", 120.0));

        // Act
        ArrayList<Product> products = productService.getProducts();

        // Assert
        assertTrue(products.stream().anyMatch(product -> product.getName().equals("Headphones")));
    }

    @Test
    void getProducts_afterAddingProduct_ShouldIncreaseCount() {
        // Arrange
        int initialSize = productService.getProducts().size();
        productService.addProduct(new Product("Speaker", 250.0));

        // Act
        ArrayList<Product> products = productService.getProducts();

        // Assert
        assertNotNull(products);
        assertEquals(initialSize + 1, products.size());
    }

    @Test
    void getProductById_existingProduct_ShouldReturnProduct() {
        // Arrange
        Product newProduct = productService.addProduct(new Product("Router", 80.0));

        // Act
        Product foundProduct = productService.getProductById(newProduct.getId());

        // Assert
        assertNotNull(foundProduct);
        assertEquals(newProduct.getId(), foundProduct.getId());
    }

    @Test
    void getProductById_nonExistingProduct_ShouldReturnNull() {
        // Arrange
        UUID fakeProductId = UUID.randomUUID();

        // Act
        Product foundProduct = productService.getProductById(fakeProductId);

        // Assert
        assertNull(foundProduct);
    }

    @Test
    void getProductById_afterDeletingProduct_ShouldReturnNull() {
        // Arrange
        Product newProduct = productService.addProduct(new Product("Webcam", 90.0));
        productService.deleteProductById(newProduct.getId());

        // Act & Assert
        assertNull(productService.getProductById(newProduct.getId()));
    }

    @Test
    void updateProduct_existingProduct_ShouldUpdateSuccessfully() {
        // Arrange
        Product product = productService.addProduct(new Product("Smartwatch", 200.0));

        // Act
        Product updatedProduct = productService.updateProduct(
                product.getId(),
                "Fitness Watch",
                180.0);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals("Fitness Watch", updatedProduct.getName());
        assertEquals(180.0, updatedProduct.getPrice());
    }

    @Test
    void updateProduct_nonExistingProduct_ShouldReturnNull() {
        // Arrange
        UUID fakeProductId = UUID.randomUUID();

        // Act
        Product updatedProduct = productService.updateProduct(
                fakeProductId,
                "New Name",
                150.0);

        // Assert
        assertNull(updatedProduct);
    }

    @Test
    void updateProduct_onlyPriceChange_ShouldUpdatePrice() {
        // Arrange
        Product product = productService.addProduct(new Product("Wireless Mouse", 45.0));

        // Act
        Product updatedProduct = productService.updateProduct(
                product.getId(),
                product.getName(),
                30.0);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals("Wireless Mouse", updatedProduct.getName(),
                "Product name should remain the same.");
        assertEquals(30.0, updatedProduct.getPrice(),
                "Product price should be updated.");
    }

    @Test
    void applyDiscount_toExistingProducts_ShouldUpdatePrices() {
        // Arrange
        Product product1 = productService.addProduct(new Product("External HDD", 100.0));
        Product product2 = productService.addProduct(new Product("USB Drive", 50.0));
        List<UUID> productIds = List.of(product1.getId(), product2.getId());

        // Act
        productService.applyDiscount(20.0, new ArrayList<>(productIds));

        // Assert
        Product discountedProduct1 = productService.getProductById(product1.getId());
        Product discountedProduct2 = productService.getProductById(product2.getId());
        assertEquals(80.0, discountedProduct1.getPrice(),
                "20% discount should be applied");
        assertEquals(40.0, discountedProduct2.getPrice(),
                "20% discount should be applied");
    }

    @Test
    void applyDiscount_toNonExistingProduct_ShouldNotThrowError() {
        // Arrange
        UUID fakeProductId = UUID.randomUUID();
        List<UUID> productIds = List.of(fakeProductId);

        // Act & Assert
        assertDoesNotThrow(() -> productService.applyDiscount(20.0, new ArrayList<>(productIds)));
    }

    @Test
    void applyDiscount_fullDiscount_ShouldReducePriceToZero() {
        // Arrange
        Product product = productService.addProduct(new Product("Gaming Chair", 250.0));
        List<UUID> productIds = List.of(product.getId());

        // Act
        productService.applyDiscount(100.0, new ArrayList<>(productIds));

        // Assert
        Product discountedProduct = productService.getProductById(product.getId());
        assertEquals(0.0, discountedProduct.getPrice(),
                "Product price should be zero after 100% discount.");
    }

    @Test
    void deleteProductById_existingProduct_ShouldDeleteSuccessfully() {
        // Arrange
        Product product = productService.addProduct(new Product("Gaming Console", 500.0));

        // Act
        productService.deleteProductById(product.getId());

        // Assert
        assertNull(productService.getProductById(product.getId()),
                "Product should be deleted and not found in the system.");
    }

    @Test
    void deleteProductById_nonExistingProduct_ShouldNotThrowException() {
        // Arrange
        UUID fakeProductId = UUID.randomUUID();

        // Act & Assert
        assertDoesNotThrow(() -> productService.deleteProductById(fakeProductId),
                "Should not throw an error when deleting a non-existing product.");
    }

    @Test
    void deleteProductById_multipleProducts_ShouldDeleteOnlySpecifiedProduct() {
        // Arrange
        Product product1 = productService.addProduct(new Product("Smart TV", 900.0));
        Product product2 = productService.addProduct(new Product("Projector", 700.0));

        // Act
        productService.deleteProductById(product1.getId());

        // Assert
        assertNull(productService.getProductById(product1.getId()),
                "Deleted product should not be found.");
        assertNotNull(productService.getProductById(product2.getId()),
                "Other products should not be affected.");
    }
}
