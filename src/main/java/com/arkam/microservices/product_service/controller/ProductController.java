package com.arkam.microservices.product_service.controller;

import com.arkam.microservices.product_service.dto.ProductUpdateRequest;
import com.arkam.microservices.product_service.model.Product;
import com.arkam.microservices.product_service.service.ImageUploadService;
import com.arkam.microservices.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;
    private final ImageUploadService imageUploadService;


    @Autowired
    public ProductController(ProductService productService, ImageUploadService imageUploadService) {
        this.productService = productService;
        this.imageUploadService = imageUploadService;
    }

    @GetMapping("/retrieveAll")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/getById")
    public ResponseEntity<Product> getProductById(@RequestParam Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Product> createProduct(
            @RequestParam("image") MultipartFile image,
            @RequestParam("name") String name,
            @RequestParam("unitPrice") Double unitPrice,
            @RequestParam("unitCost") Double unitCost,
            @RequestParam("discount") Double discount,
            @RequestParam("category") String category,
            @RequestParam("supplierName") String supplierName,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("skuCode") String skuCode,
            @RequestParam("description") String description,
            @RequestParam("ratingCount") Integer ratingCount,
            @RequestParam("rating") float rating) {

        // Upload the image to S3 and get the URL
        String imageUrl = imageUploadService.uploadFile(image);

        // Create the product with the image URL and other data
        Product product = new Product(imageUrl, name, unitPrice, unitCost, discount, category, supplierName, quantity, unitPrice * quantity - discount, skuCode, description, ratingCount, rating);

        // Save the product and return it
        Product savedProduct = productService.createProduct(product, image);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/updateProduct")
    public ResponseEntity<Product> updateProduct(
            @RequestParam Long id,
            @ModelAttribute ProductUpdateRequest updateRequest,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Product existingProduct = productService.getProductById(id);

        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = imageUploadService.uploadFile(image);
            existingProduct.setImage(imageUrl);
        }

        if (updateRequest.getName() != null) existingProduct.setName(updateRequest.getName());
        if (updateRequest.getUnitPrice() != null) existingProduct.setUnitPrice(updateRequest.getUnitPrice());
        if (updateRequest.getUnitCost() != null) existingProduct.setUnitCost(updateRequest.getUnitCost());
        if (updateRequest.getDiscount() != null) existingProduct.setDiscount(updateRequest.getDiscount());
        if (updateRequest.getCategory() != null) existingProduct.setCategory(updateRequest.getCategory());
        if (updateRequest.getSupplierName() != null) existingProduct.setSupplierName(updateRequest.getSupplierName());
        if (updateRequest.getQuantity() != null) existingProduct.setQuantity(updateRequest.getQuantity());
        if (updateRequest.getSkuCode() != null) existingProduct.setSkuCode(updateRequest.getSkuCode());
        if (updateRequest.getDescription() != null) existingProduct.setDescription(updateRequest.getDescription());
        if (updateRequest.getRatingCount() != null) existingProduct.setRatingCount(updateRequest.getRatingCount());
        if (updateRequest.getRating() != null) existingProduct.setRating(updateRequest.getRating());

        existingProduct.setTotalAmount(existingProduct.getUnitPrice() * existingProduct.getQuantity() - existingProduct.getDiscount());

        Product updatedProduct = productService.updateProduct(id, existingProduct, image);
        return ResponseEntity.ok(updatedProduct);
    }


    @DeleteMapping("/deleteProduct")
    public ResponseEntity<Void> deleteProduct(@RequestParam Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
