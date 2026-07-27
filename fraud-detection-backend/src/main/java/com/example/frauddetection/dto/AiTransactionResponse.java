package com.example.frauddetection.dto;

public class AiTransactionResponse {

    private Double amount;
    private Double distanceFromHome;
    private Double distanceFromLastTransaction;
    private Double ratioToMedianPurchasePrice;

    private Integer repeatRetailer;
    private Integer usedChip;
    private Integer usedPinNumber;
    private Integer onlineOrder;

    private Integer fraud;

    private Double fraudProbability;
    private Integer aiPrediction;

    public Double getDistanceFromHome() {
        return distanceFromHome;
    }

    public void setDistanceFromHome(Double distanceFromHome) {
        this.distanceFromHome = distanceFromHome;
    }

    public Double getDistanceFromLastTransaction() {
        return distanceFromLastTransaction;
    }

    public void setDistanceFromLastTransaction(Double distanceFromLastTransaction) {
        this.distanceFromLastTransaction = distanceFromLastTransaction;
    }

    public Double getRatioToMedianPurchasePrice() {
        return ratioToMedianPurchasePrice;
    }

    public void setRatioToMedianPurchasePrice(Double ratioToMedianPurchasePrice) {
        this.ratioToMedianPurchasePrice = ratioToMedianPurchasePrice;
    }

    public Integer getRepeatRetailer() {
        return repeatRetailer;
    }

    public void setRepeatRetailer(Integer repeatRetailer) {
        this.repeatRetailer = repeatRetailer;
    }

    public Integer getUsedChip() {
        return usedChip;
    }

    public void setUsedChip(Integer usedChip) {
        this.usedChip = usedChip;
    }

    public Integer getUsedPinNumber() {
        return usedPinNumber;
    }

    public void setUsedPinNumber(Integer usedPinNumber) {
        this.usedPinNumber = usedPinNumber;
    }

    public Integer getOnlineOrder() {
        return onlineOrder;
    }

    public void setOnlineOrder(Integer onlineOrder) {
        this.onlineOrder = onlineOrder;
    }

    public Integer getFraud() {
        return fraud;
    }

    public void setFraud(Integer fraud) {
        this.fraud = fraud;
    }

    public Double getFraudProbability() {
        return fraudProbability;
    }

    public void setFraudProbability(Double fraudProbability) {
        this.fraudProbability = fraudProbability;
    }

    public Integer getAiPrediction() {
        return aiPrediction;
    }

    public void setAiPrediction(Integer aiPrediction) {
        this.aiPrediction = aiPrediction;
    }

    public Double getAmount() {
    return amount;
}

public void setAmount(Double amount) {
    this.amount = amount;
}
}