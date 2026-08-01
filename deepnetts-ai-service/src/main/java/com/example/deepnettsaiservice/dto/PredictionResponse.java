package com.example.deepnettsaiservice.dto;

import java.util.List;

public record PredictionResponse(int generatedTransactions, List<TransactionDto> transactions) {}
