package com.arkam.microservices.product_service.dto;

public class ProductDTO {

    private Long id;
    private String image;
    private String name;
    private Double unitPrice;
    private Double unitCost;
    private Double discount;
    private String category;
    private String supplierName;
    private Integer quantity;
    private Double totalAmount;
    private String skuCode;

    public ProductDTO() {}

    public ProductDTO(Long id, String image, String name, Double unitPrice, Double unitCost, Double discount,
                      String category, String supplierName, Integer quantity, Double totalAmount, String skuCode) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.unitPrice = unitPrice;
        this.unitCost = unitCost;
        this.discount = discount;
        this.category = category;
        this.supplierName = supplierName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.skuCode = skuCode;
    }

    // Getters and Setters
}
