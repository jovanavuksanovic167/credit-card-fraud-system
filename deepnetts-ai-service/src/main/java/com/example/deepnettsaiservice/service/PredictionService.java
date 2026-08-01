package com.example.deepnettsaiservice.service;

import com.example.deepnettsaiservice.dto.PredictionResponse;
import com.example.deepnettsaiservice.dto.TransactionDto;
import com.example.deepnettsaiservice.model.FraudPrediction;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PredictionService {

  private static final int NUMBER_OF_TRANSACTIONS = 100;

  private final DatasetService datasetService;
  private final DeepNettsModelService deepNettsModelService;

  public PredictionService(
      DatasetService datasetService, DeepNettsModelService deepNettsModelService) {
    this.datasetService = datasetService;
    this.deepNettsModelService = deepNettsModelService;
  }

  public PredictionResponse processDailyTransactions() {

    List<TransactionDto> generatedTransactions =
        datasetService.generateTransactions(NUMBER_OF_TRANSACTIONS);

    List<TransactionDto> predictedFrauds = new ArrayList<>();

    for (TransactionDto transaction : generatedTransactions) {

      FraudPrediction prediction = deepNettsModelService.predict(transaction);

      TransactionDto predictedTransaction =
          new TransactionDto(
              transaction.amount(),
              transaction.distanceFromHome(),
              transaction.distanceFromLastTransaction(),
              transaction.ratioToMedianPurchasePrice(),
              transaction.repeatRetailer(),
              transaction.usedChip(),
              transaction.usedPinNumber(),
              transaction.onlineOrder(),
              transaction.fraud(),
              prediction.probability(),
              prediction.prediction());

      if (prediction.prediction() == 1) {
        predictedFrauds.add(predictedTransaction);
      }
    }

    return new PredictionResponse(NUMBER_OF_TRANSACTIONS, predictedFrauds);
  }
}
