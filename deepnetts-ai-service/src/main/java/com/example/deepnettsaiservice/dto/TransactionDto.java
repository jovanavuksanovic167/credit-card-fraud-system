package com.example.deepnettsaiservice.dto;

public record TransactionDto(
    double amount,
    double distanceFromHome,
    double distanceFromLastTransaction,
    double ratioToMedianPurchasePrice,
    int repeatRetailer,
    int usedChip,
    int usedPinNumber,
    int onlineOrder,
    int fraud,
    double fraudProbability,
    int aiPrediction) {}
