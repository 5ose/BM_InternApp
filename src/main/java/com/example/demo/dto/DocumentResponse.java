package com.example.demo.dto;

import java.time.LocalDateTime;

public record DocumentResponse(
        Long id,
        String originalFilename,
        Long fileSize,
        String contentType,
        String storagePath,
        LocalDateTime uploadTime
) {
}
