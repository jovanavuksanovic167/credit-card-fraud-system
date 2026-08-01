package com.example.frauddetection.dto;

public class DailyProcessingResponse {

  private int generatedTransactions;
  private int savedTransactions;
  private int createdFraudCases;

  public DailyProcessingResponse() {}

  public DailyProcessingResponse(
      int generatedTransactions, int savedTransactions, int createdFraudCases) {
    this.generatedTransactions = generatedTransactions;
    this.savedTransactions = savedTransactions;
    this.createdFraudCases = createdFraudCases;
  }

  public int getGeneratedTransactions() {
    return generatedTransactions;
  }

  public void setGeneratedTransactions(int generatedTransactions) {
    this.generatedTransactions = generatedTransactions;
  }

  public int getSavedTransactions() {
    return savedTransactions;
  }

  public void setSavedTransactions(int savedTransactions) {
    this.savedTransactions = savedTransactions;
  }

  public int getCreatedFraudCases() {
    return createdFraudCases;
  }

  public void setCreatedFraudCases(int createdFraudCases) {
    this.createdFraudCases = createdFraudCases;
  }
}
