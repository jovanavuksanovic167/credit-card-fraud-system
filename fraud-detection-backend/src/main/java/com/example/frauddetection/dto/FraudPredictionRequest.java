package com.example.frauddetection.dto;

public record FraudPredictionRequest(
    double distanceFromHome,
    double distanceFromLastTransaction,
    double ratioToMedianPurchasePrice,
    double repeatRetailer,
    double usedChip,
    double usedPinNumber,
    double onlineOrder,
    double amount) {}
