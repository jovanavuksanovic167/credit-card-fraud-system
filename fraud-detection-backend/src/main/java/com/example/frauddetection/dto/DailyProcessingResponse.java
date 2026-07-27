package com.example.frauddetection.dto;

public class DailyProcessingResponse {

    private int generatedTransactions;
    private int savedTransactions;
    private int createdFraudCases;

    private String trainingStatus;

    private Double accuracy;
    private Double precision;
    private Double recall;
    private Double f1Score;
    private Double rocAuc;
    private Double prAuc;

    private Integer trueNegatives;
    private Integer falsePositives;
    private Integer falseNegatives;
    private Integer truePositives;

    public DailyProcessingResponse() {
    }

    public DailyProcessingResponse(
            int generatedTransactions,
            int savedTransactions,
            int createdFraudCases,
            String trainingStatus,
            Double accuracy,
            Double precision,
            Double recall,
            Double f1Score,
            Double rocAuc,
            Double prAuc,
            Integer trueNegatives,
            Integer falsePositives,
            Integer falseNegatives,
            Integer truePositives
    ) {
        this.generatedTransactions = generatedTransactions;
        this.savedTransactions = savedTransactions;
        this.createdFraudCases = createdFraudCases;
        this.trainingStatus = trainingStatus;
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1Score = f1Score;
        this.rocAuc = rocAuc;
        this.prAuc = prAuc;
        this.trueNegatives = trueNegatives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
        this.truePositives = truePositives;
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

    public String getTrainingStatus() {
        return trainingStatus;
    }

    public void setTrainingStatus(String trainingStatus) {
        this.trainingStatus = trainingStatus;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Double getPrecision() {
        return precision;
    }

    public void setPrecision(Double precision) {
        this.precision = precision;
    }

    public Double getRecall() {
        return recall;
    }

    public void setRecall(Double recall) {
        this.recall = recall;
    }

    public Double getF1Score() {
        return f1Score;
    }

    public void setF1Score(Double f1Score) {
        this.f1Score = f1Score;
    }

    public Double getRocAuc() {
        return rocAuc;
    }

    public void setRocAuc(Double rocAuc) {
        this.rocAuc = rocAuc;
    }

    public Double getPrAuc() {
        return prAuc;
    }

    public void setPrAuc(Double prAuc) {
        this.prAuc = prAuc;
    }

    public Integer getTrueNegatives() {
        return trueNegatives;
    }

    public void setTrueNegatives(Integer trueNegatives) {
        this.trueNegatives = trueNegatives;
    }

    public Integer getFalsePositives() {
        return falsePositives;
    }

    public void setFalsePositives(Integer falsePositives) {
        this.falsePositives = falsePositives;
    }

    public Integer getFalseNegatives() {
        return falseNegatives;
    }

    public void setFalseNegatives(Integer falseNegatives) {
        this.falseNegatives = falseNegatives;
    }

    public Integer getTruePositives() {
        return truePositives;
    }

    public void setTruePositives(Integer truePositives) {
        this.truePositives = truePositives;
    }
}