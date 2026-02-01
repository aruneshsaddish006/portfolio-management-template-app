package com.vanguard.portal.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Domain model representing portfolio risk analytics metrics.
 *
 * <p>Includes Value-at-Risk, stress test results, concentration analysis,
 * volatility measures, and regulatory compliance indicators.</p>
 *
 * @author Legacy Systems Team
 * @version 1.0
 * @since 2004
 */
public class RiskMetrics implements Serializable {

    private static final long serialVersionUID = 1L;

    private String portfolioId;
    private Date calculationDate;

    // Value-at-Risk metrics
    private BigDecimal valueAtRisk95; // 95% confidence level
    private BigDecimal valueAtRisk99; // 99% confidence level
    private int varTimeHorizon; // in days

    // Volatility measures
    private Double portfolioVolatility; // Annualized standard deviation
    private Double sharpeRatio;
    private Double sortinoRatio;
    private Double beta; // vs. market benchmark
    private Double alpha; // Jensen's alpha

    // Stress testing
    private BigDecimal maxDrawdown;
    private Integer recoveryTimeDays;
    private BigDecimal stressTestLoss2008; // 2008 Financial Crisis scenario
    private BigDecimal stressTestLoss2020; // COVID-19 scenario
    private BigDecimal stressTestLoss1987; // Black Monday scenario

    // Concentration risk
    private Map<String, Double> sectorConcentration; // Sector -> % of portfolio
    private Map<String, Double> assetClassConcentration; // Asset class -> % of portfolio
    private Map<String, Double> issuerConcentration; // Issuer -> % of portfolio
    private Double largestPositionPct; // Largest single position as % of portfolio
    private Integer numConcentrationViolations;

    // Liquidity risk
    private BigDecimal liquidAssets; // Assets that can be sold within 1 day
    private Double liquidityRatio; // Liquid assets / Total assets
    private Integer averageDaysToLiquidate;

    // Credit risk (for bond portfolios)
    private Double weightedAverageCreditRating;
    private Double highYieldExposurePct;
    private BigDecimal creditRiskAmount;

    // Compliance
    private Boolean isCompliant;
    private Integer numViolations;
    private String[] complianceViolations;

    // Calculation metadata
    private String calculationMethod; // e.g., "Historical", "Monte Carlo", "Parametric"
    private Integer numSimulations; // For Monte Carlo
    private Long calculationTimeMs;

    // Constructors
    public RiskMetrics() {
    }

    public RiskMetrics(String portfolioId) {
        this.portfolioId = portfolioId;
        this.calculationDate = new Date();
    }

    // Getters and Setters
    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Date getCalculationDate() {
        return calculationDate;
    }

    public void setCalculationDate(Date calculationDate) {
        this.calculationDate = calculationDate;
    }

    public BigDecimal getValueAtRisk95() {
        return valueAtRisk95;
    }

    public void setValueAtRisk95(BigDecimal valueAtRisk95) {
        this.valueAtRisk95 = valueAtRisk95;
    }

    public BigDecimal getValueAtRisk99() {
        return valueAtRisk99;
    }

    public void setValueAtRisk99(BigDecimal valueAtRisk99) {
        this.valueAtRisk99 = valueAtRisk99;
    }

    public int getVarTimeHorizon() {
        return varTimeHorizon;
    }

    public void setVarTimeHorizon(int varTimeHorizon) {
        this.varTimeHorizon = varTimeHorizon;
    }

    public Double getPortfolioVolatility() {
        return portfolioVolatility;
    }

    public void setPortfolioVolatility(Double portfolioVolatility) {
        this.portfolioVolatility = portfolioVolatility;
    }

    public Double getSharpeRatio() {
        return sharpeRatio;
    }

    public void setSharpeRatio(Double sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    public Double getSortinoRatio() {
        return sortinoRatio;
    }

    public void setSortinoRatio(Double sortinoRatio) {
        this.sortinoRatio = sortinoRatio;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public BigDecimal getMaxDrawdown() {
        return maxDrawdown;
    }

    public void setMaxDrawdown(BigDecimal maxDrawdown) {
        this.maxDrawdown = maxDrawdown;
    }

    public void setMaxDrawdown(double maxDrawdown) {
        this.maxDrawdown = BigDecimal.valueOf(maxDrawdown);
    }

    public Integer getRecoveryTimeDays() {
        return recoveryTimeDays;
    }

    public void setRecoveryTimeDays(Integer recoveryTimeDays) {
        this.recoveryTimeDays = recoveryTimeDays;
    }

    public BigDecimal getStressTestLoss2008() {
        return stressTestLoss2008;
    }

    public void setStressTestLoss2008(BigDecimal stressTestLoss2008) {
        this.stressTestLoss2008 = stressTestLoss2008;
    }

    public BigDecimal getStressTestLoss2020() {
        return stressTestLoss2020;
    }

    public void setStressTestLoss2020(BigDecimal stressTestLoss2020) {
        this.stressTestLoss2020 = stressTestLoss2020;
    }

    public BigDecimal getStressTestLoss1987() {
        return stressTestLoss1987;
    }

    public void setStressTestLoss1987(BigDecimal stressTestLoss1987) {
        this.stressTestLoss1987 = stressTestLoss1987;
    }

    public Map<String, Double> getSectorConcentration() {
        return sectorConcentration;
    }

    public void setSectorConcentration(Map<String, Double> sectorConcentration) {
        this.sectorConcentration = sectorConcentration;
    }

    public Map<String, Double> getAssetClassConcentration() {
        return assetClassConcentration;
    }

    public void setAssetClassConcentration(Map<String, Double> assetClassConcentration) {
        this.assetClassConcentration = assetClassConcentration;
    }

    public Map<String, Double> getIssuerConcentration() {
        return issuerConcentration;
    }

    public void setIssuerConcentration(Map<String, Double> issuerConcentration) {
        this.issuerConcentration = issuerConcentration;
    }

    public Double getLargestPositionPct() {
        return largestPositionPct;
    }

    public void setLargestPositionPct(Double largestPositionPct) {
        this.largestPositionPct = largestPositionPct;
    }

    public Integer getNumConcentrationViolations() {
        return numConcentrationViolations;
    }

    public void setNumConcentrationViolations(Integer numConcentrationViolations) {
        this.numConcentrationViolations = numConcentrationViolations;
    }

    public BigDecimal getLiquidAssets() {
        return liquidAssets;
    }

    public void setLiquidAssets(BigDecimal liquidAssets) {
        this.liquidAssets = liquidAssets;
    }

    public Double getLiquidityRatio() {
        return liquidityRatio;
    }

    public void setLiquidityRatio(Double liquidityRatio) {
        this.liquidityRatio = liquidityRatio;
    }

    public Integer getAverageDaysToLiquidate() {
        return averageDaysToLiquidate;
    }

    public void setAverageDaysToLiquidate(Integer averageDaysToLiquidate) {
        this.averageDaysToLiquidate = averageDaysToLiquidate;
    }

    public Double getWeightedAverageCreditRating() {
        return weightedAverageCreditRating;
    }

    public void setWeightedAverageCreditRating(Double weightedAverageCreditRating) {
        this.weightedAverageCreditRating = weightedAverageCreditRating;
    }

    public Double getHighYieldExposurePct() {
        return highYieldExposurePct;
    }

    public void setHighYieldExposurePct(Double highYieldExposurePct) {
        this.highYieldExposurePct = highYieldExposurePct;
    }

    public BigDecimal getCreditRiskAmount() {
        return creditRiskAmount;
    }

    public void setCreditRiskAmount(BigDecimal creditRiskAmount) {
        this.creditRiskAmount = creditRiskAmount;
    }

    public Boolean getCompliant() {
        return isCompliant;
    }

    public void setCompliant(Boolean compliant) {
        isCompliant = compliant;
    }

    public Integer getNumViolations() {
        return numViolations;
    }

    public void setNumViolations(Integer numViolations) {
        this.numViolations = numViolations;
    }

    public String[] getComplianceViolations() {
        return complianceViolations;
    }

    public void setComplianceViolations(String[] complianceViolations) {
        this.complianceViolations = complianceViolations;
    }

    public String getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    public Integer getNumSimulations() {
        return numSimulations;
    }

    public void setNumSimulations(Integer numSimulations) {
        this.numSimulations = numSimulations;
    }

    public Long getCalculationTimeMs() {
        return calculationTimeMs;
    }

    public void setCalculationTimeMs(Long calculationTimeMs) {
        this.calculationTimeMs = calculationTimeMs;
    }

    @Override
    public String toString() {
        return "RiskMetrics{" +
                "portfolioId='" + portfolioId + '\'' +
                ", calculationDate=" + calculationDate +
                ", valueAtRisk95=" + valueAtRisk95 +
                ", portfolioVolatility=" + portfolioVolatility +
                ", sharpeRatio=" + sharpeRatio +
                ", beta=" + beta +
                '}';
    }
}
