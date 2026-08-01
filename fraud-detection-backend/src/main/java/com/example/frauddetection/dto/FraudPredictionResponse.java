package com.example.frauddetection.dto;

public record FraudPredictionResponse(double fraudProbabilityPercent, boolean fraud) {}
