package com.vanguard.portal.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Domain model representing a client investment portfolio.
 *
 * <p>A portfolio aggregates one or more accounts and tracks overall
 * investment performance, risk metrics, and asset allocation.</p>
 *
 * @author Legacy Systems Team
 * @version 1.0
 * @since 2004
 */
public class Portfolio implements Serializable {

    private static final long serialVersionUID = 1L;

    private String portfolioId;
    private String clientId;
    private String portfolioName;
    private BigDecimal totalValue;
    private BigDecimal totalGainLoss;
    private Double ytdReturn;
    private Date inceptionDate;
    private Date lastUpdated;
    private List<Account> accounts;
    private String riskProfile; // Conservative, Moderate, Aggressive
    private String investmentObjective; // Growth, Income, Balanced

    // Constructors
    public Portfolio() {
    }

    public Portfolio(String portfolioId, String clientId, String portfolioName) {
        this.portfolioId = portfolioId;
        this.clientId = clientId;
        this.portfolioName = portfolioName;
    }

    // Getters and Setters
    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getTotalGainLoss() {
        return totalGainLoss;
    }

    public void setTotalGainLoss(BigDecimal totalGainLoss) {
        this.totalGainLoss = totalGainLoss;
    }

    public Double getYtdReturn() {
        return ytdReturn;
    }

    public void setYtdReturn(Double ytdReturn) {
        this.ytdReturn = ytdReturn;
    }

    public Date getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(Date inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(String riskProfile) {
        this.riskProfile = riskProfile;
    }

    public String getInvestmentObjective() {
        return investmentObjective;
    }

    public void setInvestmentObjective(String investmentObjective) {
        this.investmentObjective = investmentObjective;
    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "portfolioId='" + portfolioId + '\'' +
                ", portfolioName='" + portfolioName + '\'' +
                ", totalValue=" + totalValue +
                ", ytdReturn=" + ytdReturn +
                '}';
    }
}
