package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class SeaweedfsService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String filerUrl;
    private final String documentDirectory;

    public SeaweedfsService(
            @Value("${seaweedfs.filer-url:http://localhost:8888}") String filerUrl,
            @Value("${seaweedfs.document-directory:/documents}") String documentDirectory
    ) {
        this.filerUrl = filerUrl;
        this.documentDirectory = documentDirectory;
    }

    public String upload(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();

            if (originalFilename == null || originalFilename.isBlank()) {
                originalFilename = "document";
            }

            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            String fileName = UUID.randomUUID() + "-" + originalFilename.replace(" ", "_");
            String storagePath = documentDirectory + "/" + fileName;

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, contentType);

            HttpEntity<byte[]> request = new HttpEntity<>(file.getBytes(), headers);
            restTemplate.exchange(filerUrl + storagePath, HttpMethod.PUT, request, String.class);

            return storagePath;
        } catch (IOException exception) {
            throw new RuntimeException("Could not upload file to SeaweedFS", exception);
        }
    }

    public byte[] download(String storagePath) {
        return restTemplate.getForObject(filerUrl + storagePath, byte[].class);
    }

    public void delete(String storagePath) {
        restTemplate.delete(filerUrl + storagePath);
    }
}
