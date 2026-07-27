package com.example.frauddetection.dto;

import com.example.frauddetection.model.CaseStatus;

public class UpdateFraudCaseRequest {

    private CaseStatus status;
    private String comment;
    private Boolean cardBlocked;
    private Boolean transactionBlocked;

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getCardBlocked() {
        return cardBlocked;
    }

    public void setCardBlocked(Boolean cardBlocked) {
        this.cardBlocked = cardBlocked;
    }

    public Boolean getTransactionBlocked() {
        return transactionBlocked;
    }

    public void setTransactionBlocked(Boolean transactionBlocked) {
        this.transactionBlocked = transactionBlocked;
    }
}