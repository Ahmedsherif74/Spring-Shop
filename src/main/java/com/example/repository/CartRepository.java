package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class CartRepository extends MainRepository<Cart> {

    @Value("${spring.application.cartDataPath}")
    private String defaultCartDataPath;

    public CartRepository(){}

    @Override
    protected String getDataPath() {
        String envPath = System.getenv("CART_DATA_PATH");
        if (envPath != null && !envPath.isEmpty()) {
            return envPath;
        } else {
            return defaultCartDataPath;
        }
    }

    @Override
    protected Class<Cart[]> getArrayType() {
        return Cart[].class;
    }

    public void addCart(Cart cart) {
        if (cart == null || cart.getUserId() == null) {
            throw new IllegalArgumentException("Cart cannot be null and must have a userId");
        }
        this.save(cart);
    }

    public ArrayList<Cart> getCarts() {
        try {
            ArrayList<Cart> carts = this.findAll();
            return (carts != null) ? carts : new ArrayList<>(); // Ensure no null value is returned
        } catch (Exception e) {
            return new ArrayList<>(); // If `findAll()` fails, return an empty list instead of throwing an error
        }
    }

    public Cart getCartById(UUID cartId) {
        List<Cart> carts = this.findAll();
        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                return cart; // found
            }
        }
        return null; // not found
    }

    public Cart getCartByUserId(UUID userId) {
        List<Cart> carts = this.findAll();
        for (Cart cart : carts) {
            if (cart.getUserId().equals(userId)) {
                return cart; // user cart mwgooda
            }
        }
        return null;
    }

    public void addProductToCart(UUID cartId, Product product) {
        List<Cart> carts = this.findAll(); // Get all carts

        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                cart.getProducts().add(product);
                this.saveAll((ArrayList<Cart>) carts);
                return;
            }
        }
    }

    public void deleteProductFromCart(UUID cartId, Product product) {
        List<Cart> carts = this.findAll(); // Get all carts

        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                cart.getProducts().removeIf(p -> p.getId().equals(product.getId()));
                this.saveAll((ArrayList<Cart>) carts);
                return;
            }
        }
        throw new RuntimeException(" Cart not found with ID");
    }


    public void deleteCartById(UUID cartId) {
        List<Cart> carts = this.findAll();
        List<Cart> updatedCarts = new ArrayList<>();

        for (Cart cart : carts) {
            if (!cart.getId().equals(cartId)) {
                updatedCarts.add(cart);
            }
        }

        this.saveAll((ArrayList<Cart>) updatedCarts);
    }

}

