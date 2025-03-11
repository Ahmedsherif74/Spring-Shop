package com.example.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.example.model.Product;

@Repository
public class ProductRepository extends MainRepository<Product> {

    @Value("${spring.application.productDataPath}")
    private String defaultProductDataPath;

    public ProductRepository() {}

    @Override
    protected String getDataPath() {
        String envPath = System.getenv("PRODUCT_DATA_PATH");
        if (envPath != null && !envPath.isEmpty()) {
            return envPath;
        } else {
            return defaultProductDataPath;
        }
    }

    @Override
    protected Class<Product[]> getArrayType() {
        return Product[].class;
    }

    public Product addProduct(Product product) {
        List<Product> products = getProducts();
        products.add(product);
        saveAll(new ArrayList<>(products));
        return product;
    }

    public ArrayList<Product> getProducts() {
        try {
            ArrayList<Product> products = this.findAll();
            return (products != null) ? products : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Product getProductById(UUID productId) {
        List<Product> products = this.findAll();
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        List<Product> products = this.findAll();
        Product updatedProduct = null;

        for (Product product : products) {
            if (product.getId().equals(productId)) {
                product.setName(newName);
                product.setPrice(newPrice);
                updatedProduct = product;
                break;
            }
        }

        if (updatedProduct != null) {
            this.saveAll(new ArrayList<>(products));
        }

        return updatedProduct;
    }

    public void deleteProductById(UUID productId) {
        List<Product> products = this.findAll();
        List<Product> updatedProducts = new ArrayList<>();

        for (Product product : products) {
            if (!product.getId().equals(productId)) {
                updatedProducts.add(product);
            }
        }
        this.saveAll((ArrayList<Product>) updatedProducts);
    }

    public void applyDiscountToProducts(double discount, List<UUID> productIds) {
        List<Product> products = this.findAll();
        for (Product product : products) {
            if (productIds.contains(product.getId())) {
                double newPrice = product.getPrice() * (1 - discount/100.0);
                newPrice = Math.round(newPrice * 100.0) / 100.0;
                product.setPrice(newPrice);
            }
        }
        this.saveAll((ArrayList<Product>)products);
    }
}