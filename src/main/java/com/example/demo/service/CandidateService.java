package com.example.demo.service;

import com.example.demo.dto.CandidateRequest;
import com.example.demo.dto.CandidateResponse;
import com.example.demo.entity.Candidate;
import com.example.demo.entity.CandidateStatus;
import com.example.demo.repository.CandidateRepository;
import com.example.demo.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final DocumentRepository documentRepository;

    public CandidateService(CandidateRepository candidateRepository, DocumentRepository documentRepository) {
        this.candidateRepository = candidateRepository;
        this.documentRepository = documentRepository;
    }

    public CandidateResponse createCandidate(CandidateRequest request) {
        checkDocument(request.documentId());

        Candidate candidate = new Candidate();
        candidate.setFullName(request.fullName());
        candidate.setEmail(request.email());
        candidate.setPhone(request.phone());
        candidate.setPosition(request.position());
        candidate.setDocumentId(request.documentId());
        candidate.setCreatedAt(LocalDateTime.now());

        if (request.status() == null) {
            candidate.setStatus(CandidateStatus.APPLIED);
        } else {
            candidate.setStatus(request.status());
        }

        Candidate savedCandidate = candidateRepository.save(candidate);
        return toResponse(savedCandidate);
    }

    public List<CandidateResponse> getCandidates(String search, CandidateStatus status) {
        List<Candidate> candidates;

        if (status != null) {
            candidates = candidateRepository.findByStatus(status);
        } else if (search != null && !search.isBlank()) {
            candidates = candidateRepository.findByFullNameContainingIgnoreCase(search);
        } else {
            candidates = candidateRepository.findAll();
        }

        return candidates.stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<CandidateResponse> getCandidateById(Long id) {
        return candidateRepository.findById(id)
                .map(this::toResponse);
    }

    public CandidateResponse updateCandidate(Long id, CandidateRequest request) {
        checkDocument(request.documentId());

        Candidate candidate = getCandidateEntity(id);
        candidate.setFullName(request.fullName());
        candidate.setEmail(request.email());
        candidate.setPhone(request.phone());
        candidate.setPosition(request.position());
        candidate.setDocumentId(request.documentId());

        if (request.status() != null) {
            candidate.setStatus(request.status());
        }

        Candidate savedCandidate = candidateRepository.save(candidate);
        return toResponse(savedCandidate);
    }

    public CandidateResponse updateStatus(Long id, CandidateStatus status) {
        Candidate candidate = getCandidateEntity(id);
        candidate.setStatus(status);

        Candidate savedCandidate = candidateRepository.save(candidate);
        return toResponse(savedCandidate);
    }

    public CandidateResponse attachDocument(Long id, Long documentId) {
        checkDocument(documentId);

        Candidate candidate = getCandidateEntity(id);
        candidate.setDocumentId(documentId);

        Candidate savedCandidate = candidateRepository.save(candidate);
        return toResponse(savedCandidate);
    }

    public void deleteCandidate(Long id) {
        Candidate candidate = getCandidateEntity(id);
        candidateRepository.delete(candidate);
    }

    private Candidate getCandidateEntity(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Candidate not found"));
    }

    private void checkDocument(Long documentId) {
        if (documentId != null && !documentRepository.existsById(documentId)) {
            throw new NoSuchElementException("Document not found");
        }
    }

    private CandidateResponse toResponse(Candidate candidate) {
        return new CandidateResponse(
                candidate.getId(),
                candidate.getFullName(),
                candidate.getEmail(),
                candidate.getPhone(),
                candidate.getPosition(),
                candidate.getStatus(),
                candidate.getDocumentId(),
                candidate.getCreatedAt()
        );
    }
}
