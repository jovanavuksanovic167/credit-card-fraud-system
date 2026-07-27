package com.example.frauddetection.controller;

import com.example.frauddetection.dto.UpdateFraudCaseRequest;
import com.example.frauddetection.model.CaseStatus;
import com.example.frauddetection.model.FraudCase;
import com.example.frauddetection.repository.FraudCaseRepository;
import org.springframework.web.bind.annotation.*;
import com.example.frauddetection.model.CaseStatus;

import java.util.List;

@RestController
@RequestMapping("/api/fraud-cases")
@CrossOrigin(origins = "*")
public class FraudCaseController {

    private final FraudCaseRepository fraudCaseRepository;

    public FraudCaseController(FraudCaseRepository fraudCaseRepository) {
        this.fraudCaseRepository = fraudCaseRepository;
    }

    @GetMapping
public List<FraudCase> getAllFraudCases(@RequestParam(required = false) CaseStatus status) {
    if (status != null) {
        return fraudCaseRepository.findByStatus(status);
    }

    return fraudCaseRepository.findAll();
}

    @GetMapping("/{id}")
    public FraudCase getFraudCaseById(@PathVariable Long id) {
        return fraudCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fraud case not found with id: " + id));
    }

    @PutMapping("/{id}")
    public FraudCase updateFraudCase(@PathVariable Long id,
                                     @RequestBody UpdateFraudCaseRequest request) {

        FraudCase fraudCase = fraudCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fraud case not found with id: " + id));

        if (request.getStatus() != null) {
            fraudCase.setStatus(request.getStatus());
        }

        if (request.getComment() != null) {
            fraudCase.setComment(request.getComment());
        }

        if (request.getCardBlocked() != null) {
            fraudCase.setCardBlocked(request.getCardBlocked());
        }

        if (request.getTransactionBlocked() != null) {
            fraudCase.setTransactionBlocked(request.getTransactionBlocked());
        }

        return fraudCaseRepository.save(fraudCase);
    }
}