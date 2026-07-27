package com.example.frauddetection.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    private LocalDateTime createdAt;

    public Transaction() {
        this.createdAt = LocalDateTime.now();
    }

    public Transaction(Double amount,

                   Double distanceFromHome,

                   Double distanceFromLastTransaction,

                   Double ratioToMedianPurchasePrice,

                   Integer repeatRetailer,

                   Integer usedChip,

                   Integer usedPinNumber,

                   Integer onlineOrder,

                   Integer fraud) {

    this.amount = amount;

    this.distanceFromHome = distanceFromHome;

    this.distanceFromLastTransaction = distanceFromLastTransaction;

    this.ratioToMedianPurchasePrice = ratioToMedianPurchasePrice;

    this.repeatRetailer = repeatRetailer;

    this.usedChip = usedChip;

    this.usedPinNumber = usedPinNumber;

    this.onlineOrder = onlineOrder;

    this.fraud = fraud;

    this.createdAt = LocalDateTime.now();

}

    public Long getId() {
        return id;
    }
public Double getAmount() {
    return amount;
}

public void setAmount(Double amount) {
    this.amount = amount;
}
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}