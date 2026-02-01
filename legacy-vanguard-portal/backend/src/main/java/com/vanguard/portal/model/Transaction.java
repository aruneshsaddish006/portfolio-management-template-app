package com.vanguard.portal.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Domain model representing a financial transaction.
 *
 * @author Legacy Team
 */
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long transactionId;
    private String accountNumber;
    private String transactionType; // DEBIT, CREDIT, TRANSFER
    private BigDecimal amount;
    private String description;
    private String status; // PENDING, COMPLETED, FAILED
    private Date transactionDate;
    private Date createdDate;

    public Transaction() {
        this.amount = BigDecimal.ZERO;
    }

    // Getters and Setters

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionType='" + transactionType + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
