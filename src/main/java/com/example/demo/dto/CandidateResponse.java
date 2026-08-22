package com.example.demo.dto;

import com.example.demo.entity.CandidateStatus;

import java.time.LocalDateTime;

public record CandidateResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String position,
        String tags,
        CandidateStatus status,
        Long documentId,
        LocalDateTime createdAt
) {
}
