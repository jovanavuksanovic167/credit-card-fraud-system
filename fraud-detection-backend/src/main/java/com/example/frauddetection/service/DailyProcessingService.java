package com.example.frauddetection.service;

import com.example.frauddetection.dto.AiDailyProcessingResponse;
import com.example.frauddetection.dto.AiTransactionResponse;
import com.example.frauddetection.dto.DailyProcessingResponse;
import com.example.frauddetection.model.FraudCase;
import com.example.frauddetection.model.Transaction;
import com.example.frauddetection.repository.FraudCaseRepository;
import com.example.frauddetection.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyProcessingService {

    private final AiServiceClient aiServiceClient;
    private final TransactionRepository transactionRepository;
    private final FraudCaseRepository fraudCaseRepository;

    public DailyProcessingService(AiServiceClient aiServiceClient,
                                  TransactionRepository transactionRepository,
                                  FraudCaseRepository fraudCaseRepository) {
        this.aiServiceClient = aiServiceClient;
        this.transactionRepository = transactionRepository;
        this.fraudCaseRepository = fraudCaseRepository;
    }

    public DailyProcessingResponse processDailyTransactions(int count) {
        AiDailyProcessingResponse aiResponse = aiServiceClient.processDailyTransactions(count);

        int savedTransactions = 0;
        int createdFraudCases = 0;

        List<AiTransactionResponse> aiTransactions = aiResponse.getTransactions();

        for (AiTransactionResponse aiTransaction : aiTransactions) {
            Transaction transaction = mapToTransaction(aiTransaction);

            Transaction savedTransaction = transactionRepository.save(transaction);
            savedTransactions++;

            if (savedTransaction.getAiPrediction() != null
                    && savedTransaction.getAiPrediction() == 1) {

                FraudCase fraudCase = new FraudCase(savedTransaction);
                fraudCaseRepository.save(fraudCase);
                createdFraudCases++;
            }
        }

        return new DailyProcessingResponse(
                aiResponse.getGeneratedTransactions(),
                savedTransactions,
                createdFraudCases,
                aiResponse.getTrainingStatus(),
                aiResponse.getAccuracy(),
                aiResponse.getPrecision(),
                aiResponse.getRecall(),
                aiResponse.getF1Score(),
                aiResponse.getRocAuc(),
                aiResponse.getPrAuc(),
                aiResponse.getTrueNegatives(),
                aiResponse.getFalsePositives(),
                aiResponse.getFalseNegatives(),
                aiResponse.getTruePositives()
        );
    }

    private Transaction mapToTransaction(AiTransactionResponse aiTransaction) {
        Transaction transaction = new Transaction(
                aiTransaction.getAmount(),
                aiTransaction.getDistanceFromHome(),
                aiTransaction.getDistanceFromLastTransaction(),
                aiTransaction.getRatioToMedianPurchasePrice(),
                aiTransaction.getRepeatRetailer(),
                aiTransaction.getUsedChip(),
                aiTransaction.getUsedPinNumber(),
                aiTransaction.getOnlineOrder(),
                aiTransaction.getFraud()
        );

        transaction.setFraudProbability(aiTransaction.getFraudProbability());
        transaction.setAiPrediction(aiTransaction.getAiPrediction());

        return transaction;
    }
}