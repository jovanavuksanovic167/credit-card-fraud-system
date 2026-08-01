package com.example.deepnettsaiservice.service;

import com.example.deepnettsaiservice.dto.TransactionDto;
import com.example.deepnettsaiservice.model.FraudPrediction;
import com.example.deepnettsaiservice.predictor.CreditCardFraudPredictor;
import org.springframework.stereotype.Service;

@Service
public class DeepNettsModelService {

  private final CreditCardFraudPredictor predictor;

  public DeepNettsModelService(CreditCardFraudPredictor predictor) {
    this.predictor = predictor;
  }

  public FraudPrediction predict(TransactionDto transaction) {

    CreditCardFraudPredictor.PredictionResult result =
        predictor.predict(
            transaction.distanceFromHome(),
            transaction.distanceFromLastTransaction(),
            transaction.ratioToMedianPurchasePrice(),
            transaction.repeatRetailer(),
            transaction.usedChip(),
            transaction.usedPinNumber(),
            transaction.onlineOrder(),
            transaction.amount());

    return new FraudPrediction(result.fraudProbability(), result.fraud() ? 1 : 0);
  }
}
