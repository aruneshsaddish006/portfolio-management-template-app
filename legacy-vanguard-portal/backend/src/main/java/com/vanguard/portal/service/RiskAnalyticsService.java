package com.vanguard.portal.service;

import com.vanguard.portal.model.Portfolio;
import com.vanguard.portal.model.RiskMetrics;
import java.util.List;

/**
 * Service interface for portfolio risk analytics and compliance calculations.
 *
 * <p>This service handles Value-at-Risk (VaR), stress testing, concentration
 * analysis, and regulatory compliance checks for investment portfolios.</p>
 *
 * @author Legacy Systems Team
 * @version 1.0
 * @since 2004
 */
public interface RiskAnalyticsService {

    /**
     * Calculates Value-at-Risk for a given portfolio.
     *
     * @param portfolioId the unique identifier of the portfolio
     * @param confidenceLevel the confidence level (e.g., 0.95 for 95%)
     * @param timeHorizon the time horizon in days
     * @return the calculated VaR amount
     */
    double calculateValueAtRisk(String portfolioId, double confidenceLevel, int timeHorizon);

    /**
     * Performs portfolio stress testing against historical market scenarios.
     *
     * @param portfolioId the unique identifier of the portfolio
     * @param scenarioIds list of scenario identifiers to test against
     * @return risk metrics including maximum drawdown and recovery time
     */
    RiskMetrics runStressTests(String portfolioId, List<String> scenarioIds);

    /**
     * Analyzes portfolio concentration risk across asset classes, sectors, and issuers.
     *
     * @param portfolioId the unique identifier of the portfolio
     * @return concentration risk report
     */
    RiskMetrics analyzeConcentrationRisk(String portfolioId);

    /**
     * Checks portfolio compliance against regulatory requirements (SEC, FINRA).
     *
     * @param portfolioId the unique identifier of the portfolio
     * @return list of compliance violations, empty if fully compliant
     */
    List<String> checkRegulatoryCompliance(String portfolioId);

    /**
     * Calculates portfolio beta relative to market benchmark.
     *
     * @param portfolioId the unique identifier of the portfolio
     * @param benchmarkSymbol the benchmark ticker symbol (e.g., "SPY")
     * @param periodDays the lookback period in days
     * @return the calculated beta coefficient
     */
    double calculatePortfolioBeta(String portfolioId, String benchmarkSymbol, int periodDays);
}
