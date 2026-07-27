package com.example.frauddetection.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_cases")
public class FraudCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    private CaseStatus status;

    private String comment;

    private Boolean cardBlocked;

    private Boolean transactionBlocked;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public FraudCase() {
        this.status = CaseStatus.NEW;
        this.cardBlocked = false;
        this.transactionBlocked = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public FraudCase(Transaction transaction) {
        this.transaction = transaction;
        this.status = CaseStatus.NEW;
        this.cardBlocked = false;
        this.transactionBlocked = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        this.updatedAt = LocalDateTime.now();
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getCardBlocked() {
        return cardBlocked;
    }

    public void setCardBlocked(Boolean cardBlocked) {
        this.cardBlocked = cardBlocked;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getTransactionBlocked() {
        return transactionBlocked;
    }

    public void setTransactionBlocked(Boolean transactionBlocked) {
        this.transactionBlocked = transactionBlocked;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}