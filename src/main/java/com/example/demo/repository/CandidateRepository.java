package com.example.demo.repository;

import com.example.demo.entity.Candidate;
import com.example.demo.entity.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findByStatus(CandidateStatus status);

    List<Candidate> findByFullNameContainingIgnoreCase(String fullName);
}
