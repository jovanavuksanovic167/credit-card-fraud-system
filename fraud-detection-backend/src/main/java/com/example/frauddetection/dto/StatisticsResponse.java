package com.example.frauddetection.dto;

public class StatisticsResponse {

    private long totalTransactions;
    private long totalFraudCases;

    private long newCases;
    private long inReviewCases;
    private long confirmedFrauds;
    private long falseAlerts;

    private long blockedCards;
    private long blockedTransactions;

    public StatisticsResponse() {
    }

    public StatisticsResponse(long totalTransactions,
                              long totalFraudCases,
                              long newCases,
                              long inReviewCases,
                              long confirmedFrauds,
                              long falseAlerts,
                              long blockedCards,
                              long blockedTransactions) {
        this.totalTransactions = totalTransactions;
        this.totalFraudCases = totalFraudCases;
        this.newCases = newCases;
        this.inReviewCases = inReviewCases;
        this.confirmedFrauds = confirmedFrauds;
        this.falseAlerts = falseAlerts;
        this.blockedCards = blockedCards;
        this.blockedTransactions = blockedTransactions;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public long getTotalFraudCases() {
        return totalFraudCases;
    }

    public void setTotalFraudCases(long totalFraudCases) {
        this.totalFraudCases = totalFraudCases;
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

    public long getConfirmedFrauds() {
        return confirmedFrauds;
    }

    public void setConfirmedFrauds(long confirmedFrauds) {
        this.confirmedFrauds = confirmedFrauds;
    }

    public long getFalseAlerts() {
        return falseAlerts;
    }

    public void setFalseAlerts(long falseAlerts) {
        this.falseAlerts = falseAlerts;
    }

    public long getBlockedCards() {
        return blockedCards;
    }

    public void setBlockedCards(long blockedCards) {
        this.blockedCards = blockedCards;
    }

    public long getBlockedTransactions() {
        return blockedTransactions;
    }

    public void setBlockedTransactions(long blockedTransactions) {
        this.blockedTransactions = blockedTransactions;
    }
}