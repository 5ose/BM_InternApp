package com.example.demo.service;

import com.example.demo.dto.DocumentResponse;
import com.example.demo.entity.Document;
import com.example.demo.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final SeaweedfsService seaweedfsService;

    public DocumentService(DocumentRepository documentRepository, SeaweedfsService seaweedfsService) {
        this.documentRepository = documentRepository;
        this.seaweedfsService = seaweedfsService;
    }

    public DocumentResponse uploadDocument(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Document file is required");
        }

        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();

        if (filename == null || filename.isBlank()) {
            filename = "document";
        }

        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        String storagePath = seaweedfsService.upload(file);

        Document document = new Document();
        document.setOriginalFilename(filename);
        document.setFileSize(file.getSize());
        document.setContentType(contentType);
        document.setStoragePath(storagePath);
        document.setUploadTime(LocalDateTime.now());

        Document savedDocument = documentRepository.save(document);
        return toResponse(savedDocument);
    }

    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<DocumentResponse> getDocumentById(Long id) {
        return documentRepository.findById(id)
                .map(this::toResponse);
    }

    public Document getDocumentEntity(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found"));
    }

    public byte[] getFileContent(Long id) {
        Document document = getDocumentEntity(id);
        return seaweedfsService.download(document.getStoragePath());
    }

    public byte[] downloadDocument(Long id) {
        return getFileContent(id);
    }

    public void deleteDocument(Long id) {
        Document document = getDocumentEntity(id);

        seaweedfsService.delete(document.getStoragePath());
        documentRepository.delete(document);
    }

    private DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getOriginalFilename(),
                document.getFileSize(),
                document.getContentType(),
                document.getStoragePath(),
                document.getUploadTime()
        );
    }
}
