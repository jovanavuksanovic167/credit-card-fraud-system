package com.example.frauddetection.service;

import com.fraudmodel.deepnetts.CreditCardFraudPredictor;
import org.springframework.stereotype.Service;

@Service
public class DeepNettsFraudService {

  private final CreditCardFraudPredictor predictor;

  public DeepNettsFraudService() throws Exception {
    this.predictor = new CreditCardFraudPredictor();
  }

  public CreditCardFraudPredictor.PredictionResult predict(
      double distanceFromHome,
      double distanceFromLastTransaction,
      double ratioToMedianPurchasePrice,
      double repeatRetailer,
      double usedChip,
      double usedPinNumber,
      double onlineOrder,
      double amount) {
    return predictor.predict(
        distanceFromHome,
        distanceFromLastTransaction,
        ratioToMedianPurchasePrice,
        repeatRetailer,
        usedChip,
        usedPinNumber,
        onlineOrder,
        amount);
  }
}
