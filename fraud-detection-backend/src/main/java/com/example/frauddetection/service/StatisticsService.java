package com.example.frauddetection.service;

import com.example.frauddetection.dto.StatisticsResponse;
import com.example.frauddetection.model.CaseStatus;
import com.example.frauddetection.repository.FraudCaseRepository;
import com.example.frauddetection.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final TransactionRepository transactionRepository;
    private final FraudCaseRepository fraudCaseRepository;

    public StatisticsService(TransactionRepository transactionRepository,
                             FraudCaseRepository fraudCaseRepository) {
        this.transactionRepository = transactionRepository;
        this.fraudCaseRepository = fraudCaseRepository;
    }

    public StatisticsResponse getStatistics() {
        long totalTransactions = transactionRepository.count();
        long totalFraudCases = fraudCaseRepository.count();

        long newCases = fraudCaseRepository.countByStatus(CaseStatus.NEW);
        long inReviewCases = fraudCaseRepository.countByStatus(CaseStatus.IN_REVIEW);
        long confirmedFrauds = fraudCaseRepository.countByStatus(CaseStatus.CONFIRMED_FRAUD);
        long falseAlerts = fraudCaseRepository.countByStatus(CaseStatus.FALSE_ALERT);

        long blockedCards = fraudCaseRepository.countByCardBlockedTrue();
        long blockedTransactions = fraudCaseRepository.countByTransactionBlockedTrue();

        return new StatisticsResponse(
                totalTransactions,
                totalFraudCases,
                newCases,
                inReviewCases,
                confirmedFrauds,
                falseAlerts,
                blockedCards,
                blockedTransactions
        );
    }
}