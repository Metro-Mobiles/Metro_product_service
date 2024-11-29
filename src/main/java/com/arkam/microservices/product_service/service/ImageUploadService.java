package com.arkam.microservices.product_service.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ImageUploadService {

    @Autowired
    private AmazonS3 s3Client;

    // Define the S3 bucket name and region
    String bucketName = "eaduom";  // Replace with your actual bucket name
    String bucketRegion = "eu-north-1";  // Replace with your actual region
    String fileName;

    // Method to upload a file to S3 with public read access
    @Transactional
    public String uploadFile(final MultipartFile multipartFile) {
        try {
            // Convert the MultipartFile to a File object
            final File file = convertMultiPartFileToFile(multipartFile);
            // Upload the file to S3 with public-read ACL
            fileName = uploadFileToS3Bucket(file);
            // Delete the local temporary file after upload
            file.deleteOnExit();
        } catch (final AmazonServiceException ex) {
            // Handle S3 service exceptions
            System.out.println("Error while uploading file: " + ex.getMessage());
        }

        // Return the S3 URL for the uploaded file
        return String.format("https://s3.%s.amazonaws.com/%s/%s", bucketRegion, bucketName, fileName);
    }

    // Helper method to convert MultipartFile to a File
    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        // Create a temporary file with the same name as the original file
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            // Write the file content to the temporary file
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            System.out.println("Error converting multipart file to file: " + ex.getMessage());
        }
        return file;
    }

    // Method to upload the file to S3 with public read access (PublicRead ACL)
    @Transactional
    public String uploadFileToS3Bucket(final File file) {
        // Create PutObjectRequest with PublicRead ACL to make the file publicly accessible
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getName(), file)
                .withCannedAcl(CannedAccessControlList.PublicRead);  // Set the file to be publicly readable

        // Upload the file to the S3 bucket
        s3Client.putObject(putObjectRequest);

        // Return the file name (for URL construction)
        return file.getName();
    }
}
