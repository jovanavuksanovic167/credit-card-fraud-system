package com.example.frauddetection.controller;

import com.example.frauddetection.dto.FraudPredictionRequest;
import com.example.frauddetection.dto.FraudPredictionResponse;
import com.example.frauddetection.service.DeepNettsFraudService;
import com.fraudmodel.deepnetts.CreditCardFraudPredictor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deepnetts")
@CrossOrigin(origins = "*")
public class DeepNettsFraudController {

  private final DeepNettsFraudService fraudService;

  public DeepNettsFraudController(DeepNettsFraudService fraudService) {
    this.fraudService = fraudService;
  }

  @PostMapping("/predict")
  public FraudPredictionResponse predict(@RequestBody FraudPredictionRequest request) {

    CreditCardFraudPredictor.PredictionResult result =
        fraudService.predict(
            request.distanceFromHome(),
            request.distanceFromLastTransaction(),
            request.ratioToMedianPurchasePrice(),
            request.repeatRetailer(),
            request.usedChip(),
            request.usedPinNumber(),
            request.onlineOrder(),
            request.amount());

    double probabilityPercent = Math.round(result.fraudProbability() * 100_000_000.0) / 1_000_000.0;

    return new FraudPredictionResponse(probabilityPercent, result.fraud());
  }
}
