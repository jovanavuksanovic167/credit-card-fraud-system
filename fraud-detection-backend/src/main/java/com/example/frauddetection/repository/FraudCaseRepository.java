package com.example.frauddetection.repository;

import com.example.frauddetection.model.CaseStatus;
import com.example.frauddetection.model.FraudCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FraudCaseRepository extends JpaRepository<FraudCase, Long> {

    long countByStatus(CaseStatus status);

    long countByCardBlockedTrue();

    long countByTransactionBlockedTrue();

    List<FraudCase> findByStatus(CaseStatus status);
}