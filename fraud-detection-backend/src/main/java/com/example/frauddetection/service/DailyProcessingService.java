package com.example.frauddetection.service;

import com.example.frauddetection.dto.AiDailyProcessingResponse;
import com.example.frauddetection.dto.AiTransactionResponse;
import com.example.frauddetection.dto.DailyProcessingResponse;
import com.example.frauddetection.model.FraudCase;
import com.example.frauddetection.model.Transaction;
import com.example.frauddetection.repository.FraudCaseRepository;
import com.example.frauddetection.repository.TransactionRepository;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyProcessingService {

  private final AiServiceClient aiServiceClient;
  private final TransactionRepository transactionRepository;
  private final FraudCaseRepository fraudCaseRepository;

  public DailyProcessingService(
      AiServiceClient aiServiceClient,
      TransactionRepository transactionRepository,
      FraudCaseRepository fraudCaseRepository) {
    this.aiServiceClient = aiServiceClient;
    this.transactionRepository = transactionRepository;
    this.fraudCaseRepository = fraudCaseRepository;
  }

  @Transactional
  public DailyProcessingResponse processDailyTransactions(int count) {
    AiDailyProcessingResponse aiResponse = aiServiceClient.processDailyTransactions();

    int savedTransactions = 0;
    int createdFraudCases = 0;

    List<AiTransactionResponse> aiTransactions =
        aiResponse.getTransactions() != null
            ? aiResponse.getTransactions()
            : Collections.emptyList();

    for (AiTransactionResponse aiTransaction : aiTransactions) {
      Transaction transaction = mapToTransaction(aiTransaction);

      Transaction savedTransaction = transactionRepository.save(transaction);

      savedTransactions++;

      if (Integer.valueOf(1).equals(savedTransaction.getAiPrediction())) {
        FraudCase fraudCase = new FraudCase(savedTransaction);
        fraudCaseRepository.save(fraudCase);

        createdFraudCases++;
      }
    }

    int generatedTransactions =
        aiResponse.getGeneratedTransactions() != null ? aiResponse.getGeneratedTransactions() : 0;

    return new DailyProcessingResponse(generatedTransactions, savedTransactions, createdFraudCases);
  }

  private Transaction mapToTransaction(AiTransactionResponse aiTransaction) {
    Transaction transaction =
        new Transaction(
            aiTransaction.getAmount(),
            aiTransaction.getDistanceFromHome(),
            aiTransaction.getDistanceFromLastTransaction(),
            aiTransaction.getRatioToMedianPurchasePrice(),
            aiTransaction.getRepeatRetailer(),
            aiTransaction.getUsedChip(),
            aiTransaction.getUsedPinNumber(),
            aiTransaction.getOnlineOrder(),
            aiTransaction.getFraud());

    transaction.setFraudProbability(aiTransaction.getFraudProbability());

    transaction.setAiPrediction(aiTransaction.getAiPrediction());

    return transaction;
  }
}
