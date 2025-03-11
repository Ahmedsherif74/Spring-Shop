package com.example.service;

import com.example.repository.MainRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.example.model.Product;
import com.example.repository.ProductRepository;

@Service
@SuppressWarnings("rawtypes")
public class ProductService extends MainService<Product> {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(MainRepository<Product> productRepository) {
        this.productRepository = (ProductRepository) productRepository;
    }

    public Product addProduct(Product product) {
        return productRepository.addProduct(product);
    }

    public ArrayList<Product> getProducts() {
        return productRepository.getProducts();
    }

    public Product getProductById(UUID productId) {
        return productRepository.getProductById(productId);
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        return productRepository.updateProduct(productId, newName, newPrice);
    }

    public void applyDiscount(double discount, List<UUID> productIds) {
        ArrayList<Product> allProducts = productRepository.getProducts();

        for (Product product : allProducts) {
            if (productIds.contains(product.getId())) {
                double newPrice = product.getPrice() * (1 - discount/100.0);
                newPrice = Math.round(newPrice * 100.0) / 100.0;
                product.setPrice(newPrice);
            }
        }

        productRepository.saveAll(allProducts);
    }

    public void deleteProductById(UUID productId) {
        productRepository.deleteProductById(productId);
    }

    public Object find(String typeName, Object reference) {
        if (typeName == null || reference == null) {
            return null;
        }

        // For Products
        if (typeName.equals("Product") && reference instanceof Product) {
            Product productRef = (Product) reference;
            UUID productId = productRef.getId();

            if (productId == null) {
                return null;
            }

            // Get all products and find the one with matching ID
            List<Product> products = productRepository.findAll();
            for (Product product : products) {
                if (product.getId().equals(productId)) {
                    return product;
                }
            }
        }

        // For other entity types, add similar code here

        return null; // Entity not found
    }
}