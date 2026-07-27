package com.example.frauddetection.dto;

import java.util.Map;

public class FraudAnalyticsStatisticsResponse {

    private long totalCases;
    private long newCases;
    private long inReviewCases;
    private long confirmedFraudCases;
    private long falseAlertCases;

    private double preventedLoss;
    private double averageConfirmedFraudAmount;
    private double highestConfirmedFraudAmount;
    private double averageFalseAlertAmount;

    private long pinNotUsed;
    private long chipNotUsed;
    private long onlineOrders;
    private long newRetailers;

    private double averageDistanceFromHome;
    private double averageDistanceFromLastTransaction;
    private double averagePurchasePriceRatio;

    private Map<String, Long> caseStatusDistribution;

    private Map<String, Long> usedPinDistribution;
    private Map<String, Long> usedChipDistribution;
    private Map<String, Long> onlineOrderDistribution;
    private Map<String, Long> repeatRetailerDistribution;

    private Map<String, Long> distanceFromHomeCategories;
    private Map<String, Long> distanceFromLastTransactionCategories;
    private Map<String, Long> purchasePriceRatioCategories;

    public long getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(long totalCases) {
        this.totalCases = totalCases;
    }

    public long getNewCases() {
        return newCases;
    }

    public void setNewCases(long newCases) {
        this.newCases = newCases;
    }

    public long getInReviewCases() {
        return inReviewCases;
    }

    public void setInReviewCases(long inReviewCases) {
        this.inReviewCases = inReviewCases;
    }

    public long getConfirmedFraudCases() {
        return confirmedFraudCases;
    }

    public void setConfirmedFraudCases(long confirmedFraudCases) {
        this.confirmedFraudCases = confirmedFraudCases;
    }

    public long getFalseAlertCases() {
        return falseAlertCases;
    }

    public void setFalseAlertCases(long falseAlertCases) {
        this.falseAlertCases = falseAlertCases;
    }

    public double getPreventedLoss() {
        return preventedLoss;
    }

    public void setPreventedLoss(double preventedLoss) {
        this.preventedLoss = preventedLoss;
    }

    public double getAverageConfirmedFraudAmount() {
        return averageConfirmedFraudAmount;
    }

    public void setAverageConfirmedFraudAmount(double averageConfirmedFraudAmount) {
        this.averageConfirmedFraudAmount = averageConfirmedFraudAmount;
    }

    public double getHighestConfirmedFraudAmount() {
        return highestConfirmedFraudAmount;
    }

    public void setHighestConfirmedFraudAmount(double highestConfirmedFraudAmount) {
        this.highestConfirmedFraudAmount = highestConfirmedFraudAmount;
    }

    public double getAverageFalseAlertAmount() {
        return averageFalseAlertAmount;
    }

    public void setAverageFalseAlertAmount(double averageFalseAlertAmount) {
        this.averageFalseAlertAmount = averageFalseAlertAmount;
    }

    public long getPinNotUsed() {
        return pinNotUsed;
    }

    public void setPinNotUsed(long pinNotUsed) {
        this.pinNotUsed = pinNotUsed;
    }

    public long getChipNotUsed() {
        return chipNotUsed;
    }

    public void setChipNotUsed(long chipNotUsed) {
        this.chipNotUsed = chipNotUsed;
    }

    public long getOnlineOrders() {
        return onlineOrders;
    }

    public void setOnlineOrders(long onlineOrders) {
        this.onlineOrders = onlineOrders;
    }

    public long getNewRetailers() {
        return newRetailers;
    }

    public void setNewRetailers(long newRetailers) {
        this.newRetailers = newRetailers;
    }

    public double getAverageDistanceFromHome() {
        return averageDistanceFromHome;
    }

    public void setAverageDistanceFromHome(double averageDistanceFromHome) {
        this.averageDistanceFromHome = averageDistanceFromHome;
    }

    public double getAverageDistanceFromLastTransaction() {
        return averageDistanceFromLastTransaction;
    }

    public void setAverageDistanceFromLastTransaction(double averageDistanceFromLastTransaction) {
        this.averageDistanceFromLastTransaction = averageDistanceFromLastTransaction;
    }

    public double getAveragePurchasePriceRatio() {
        return averagePurchasePriceRatio;
    }

    public void setAveragePurchasePriceRatio(double averagePurchasePriceRatio) {
        this.averagePurchasePriceRatio = averagePurchasePriceRatio;
    }

    public Map<String, Long> getCaseStatusDistribution() {
        return caseStatusDistribution;
    }

    public void setCaseStatusDistribution(Map<String, Long> caseStatusDistribution) {
        this.caseStatusDistribution = caseStatusDistribution;
    }

    public Map<String, Long> getUsedPinDistribution() {
        return usedPinDistribution;
    }

    public void setUsedPinDistribution(Map<String, Long> usedPinDistribution) {
        this.usedPinDistribution = usedPinDistribution;
    }

    public Map<String, Long> getUsedChipDistribution() {
        return usedChipDistribution;
    }

    public void setUsedChipDistribution(Map<String, Long> usedChipDistribution) {
        this.usedChipDistribution = usedChipDistribution;
    }

    public Map<String, Long> getOnlineOrderDistribution() {
        return onlineOrderDistribution;
    }

    public void setOnlineOrderDistribution(Map<String, Long> onlineOrderDistribution) {
        this.onlineOrderDistribution = onlineOrderDistribution;
    }

    public Map<String, Long> getRepeatRetailerDistribution() {
        return repeatRetailerDistribution;
    }

    public void setRepeatRetailerDistribution(Map<String, Long> repeatRetailerDistribution) {
        this.repeatRetailerDistribution = repeatRetailerDistribution;
    }

    public Map<String, Long> getDistanceFromHomeCategories() {
        return distanceFromHomeCategories;
    }

    public void setDistanceFromHomeCategories(Map<String, Long> distanceFromHomeCategories) {
        this.distanceFromHomeCategories = distanceFromHomeCategories;
    }

    public Map<String, Long> getDistanceFromLastTransactionCategories() {
        return distanceFromLastTransactionCategories;
    }

    public void setDistanceFromLastTransactionCategories(Map<String, Long> distanceFromLastTransactionCategories) {
        this.distanceFromLastTransactionCategories = distanceFromLastTransactionCategories;
    }

    public Map<String, Long> getPurchasePriceRatioCategories() {
        return purchasePriceRatioCategories;
    }

    public void setPurchasePriceRatioCategories(Map<String, Long> purchasePriceRatioCategories) {
        this.purchasePriceRatioCategories = purchasePriceRatioCategories;
    }
}