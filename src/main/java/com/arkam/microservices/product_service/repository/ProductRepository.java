package com.arkam.microservices.product_service.repository;

import com.arkam.microservices.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findBySkuCode(String skuCode);
}
