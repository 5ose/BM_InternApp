package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class SeaweedfsService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String masterUrl;
    private final String volumeUrl;

    public SeaweedfsService(
            @Value("${seaweedfs.master-url:http://localhost:9333}") String masterUrl,
            @Value("${seaweedfs.volume-url:http://localhost:8090}") String volumeUrl
    ) {
        this.masterUrl = masterUrl;
        this.volumeUrl = volumeUrl;
    }

    public String upload(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();

            if (originalFilename == null || originalFilename.isBlank()) {
                originalFilename = "document";
            }

            String fileName = UUID.randomUUID() + "-" + originalFilename.replace(" ", "_");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(masterUrl + "/submit", request, Map.class);

            return response.getBody().get("fid").toString();
        } catch (IOException exception) {
            throw new RuntimeException("Could not upload file to SeaweedFS", exception);
        }
    }

    public byte[] download(String storagePath) {
        return restTemplate.getForObject(volumeUrl + "/" + storagePath, byte[].class);
    }

    public void delete(String storagePath) {
        restTemplate.delete(volumeUrl + "/" + storagePath);
    }
}
