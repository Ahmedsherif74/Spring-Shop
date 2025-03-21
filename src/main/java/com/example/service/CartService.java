package com.example.service;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import com.example.repository.MainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class CartService extends MainService<Cart> {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(MainRepository<Cart> cartRepository) {
        this.cartRepository = (CartRepository) cartRepository;
    }

    public Cart addCart(Cart cart) {
        cartRepository.addCart(cart);
        return cart;
    }

    public ArrayList<Cart> getCarts() {
        return cartRepository.getCarts();
    }

    public Cart getCartById(UUID cartId) {
        return cartRepository.getCartById(cartId);
    }

    public Cart getCartByUserId(UUID userId) {
        return cartRepository.getCartByUserId(userId);
    }

    public void addProductToCart(UUID cartId, Product product) {
        cartRepository.addProductToCart(cartId, product);
    }

    public void deleteProductFromCart(UUID cartId, Product product) {
        cartRepository.deleteProductFromCart(cartId, product);
    }

    public void deleteCartById(UUID cartId) {
        cartRepository.deleteCartById(cartId);
    }

}
