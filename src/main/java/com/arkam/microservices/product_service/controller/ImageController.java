package com.arkam.microservices.product_service.controller;


import com.arkam.microservices.product_service.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/product/productImages")
@RestController
public class ImageController {

    @Autowired
    private ImageUploadService imageUploadService;

    @PostMapping(consumes = "multipart/form-data")
    public String createProduct(@RequestPart("image") MultipartFile imageFile){

        String filePath = imageUploadService.uploadFile(imageFile); // Handle file upload and generate filepath

        return filePath;
    }
}