package com.example.deepnettsaiservice.controller;

import com.example.deepnettsaiservice.dto.PredictionResponse;
import com.example.deepnettsaiservice.service.PredictionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PredictionController {

  private final PredictionService predictionService;

  public PredictionController(PredictionService predictionService) {
    this.predictionService = predictionService;
  }

  @PostMapping("/process-daily-transactions")
  public PredictionResponse processDailyTransactions() {
    return predictionService.processDailyTransactions();
  }
}
