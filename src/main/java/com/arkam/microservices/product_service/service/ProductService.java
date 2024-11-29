package com.arkam.microservices.product_service.service;

import com.arkam.microservices.product_service.model.Product;
import com.arkam.microservices.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageUploadService imageUploadService; // Autowire the ImageUploadService

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product createProduct(Product product, MultipartFile imageFile) {
        // Upload the image and get the URL from S3
        String imageUrl = imageUploadService.uploadFile(imageFile);

        // Set the image URL on the product object
        product.setImage(imageUrl);

        // Calculate the total amount for the product
        product.setTotalAmount(product.getUnitPrice() * product.getQuantity() - product.getDiscount());

        // Save the product
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct, MultipartFile imageFile) {
        Product existingProduct = getProductById(id);

        // If there is a new image, upload it and update the image URL
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = imageUploadService.uploadFile(imageFile);
            existingProduct.setImage(imageUrl);
        }

        // Update other product details
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setUnitPrice(updatedProduct.getUnitPrice());
        existingProduct.setUnitCost(updatedProduct.getUnitCost());
        existingProduct.setDiscount(updatedProduct.getDiscount());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setSupplierName(updatedProduct.getSupplierName());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setTotalAmount(updatedProduct.getUnitPrice() * updatedProduct.getQuantity() - updatedProduct.getDiscount());
        existingProduct.setSkuCode(updatedProduct.getSkuCode());

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Boolean checkId(Long id){
        try {
            Optional<Product> product = productRepository.findById(id);
            if(product.isPresent()){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            throw new RuntimeException("Product not found");
        }
    }
}
