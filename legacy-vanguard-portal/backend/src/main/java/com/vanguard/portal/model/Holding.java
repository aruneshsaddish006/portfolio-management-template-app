package com.vanguard.portal.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Domain model representing an investment holding.
 * Tracks stocks, bonds, mutual funds, etc.
 *
 * @author Legacy Team
 */
public class Holding implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long holdingId;
    private String accountNumber;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private BigDecimal currentPrice;
    private BigDecimal marketValue;
    private BigDecimal gainLossPct;
    private Date purchaseDate;
    private Date lastUpdated;

    public Holding() {
        this.quantity = BigDecimal.ZERO;
        this.purchasePrice = BigDecimal.ZERO;
        this.currentPrice = BigDecimal.ZERO;
        this.marketValue = BigDecimal.ZERO;
        this.gainLossPct = BigDecimal.ZERO;
    }

    // Getters and Setters

    public Long getHoldingId() {
        return holdingId;
    }

    public void setHoldingId(Long holdingId) {
        this.holdingId = holdingId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(BigDecimal marketValue) {
        this.marketValue = marketValue;
    }

    public BigDecimal getGainLossPct() {
        return gainLossPct;
    }

    public void setGainLossPct(BigDecimal gainLossPct) {
        this.gainLossPct = gainLossPct;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "Holding{" +
                "symbol='" + symbol + '\'' +
                ", quantity=" + quantity +
                ", currentPrice=" + currentPrice +
                ", marketValue=" + marketValue +
                '}';
    }
}
