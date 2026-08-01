package com.example.frauddetection.dto;

import java.util.ArrayList;
import java.util.List;

public class AiDailyProcessingResponse {

  private Integer generatedTransactions;
  private List<AiTransactionResponse> transactions = new ArrayList<>();

  public AiDailyProcessingResponse() {}

  public AiDailyProcessingResponse(
      Integer generatedTransactions, List<AiTransactionResponse> transactions) {
    this.generatedTransactions = generatedTransactions;
    this.transactions = transactions;
  }

  public Integer getGeneratedTransactions() {
    return generatedTransactions;
  }

  public void setGeneratedTransactions(Integer generatedTransactions) {
    this.generatedTransactions = generatedTransactions;
  }

  public List<AiTransactionResponse> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<AiTransactionResponse> transactions) {
    this.transactions = transactions;
  }
}
