package com.vanguard.portal.service;

import com.vanguard.portal.dao.HoldingsDAO;
import com.vanguard.portal.dao.MarketDataDAO;
import com.vanguard.portal.model.Holding;
import com.vanguard.portal.model.Portfolio;
import com.vanguard.portal.model.RiskMetrics;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of RiskAnalyticsService with legacy anti-patterns.
 *
 * <p><strong>ANTI-PATTERN #8: Inefficient Monte Carlo Simulation</strong></p>
 * <ul>
 *   <li>Single-threaded Monte Carlo with 100,000+ iterations</li>
 *   <li>No caching of historical volatility calculations</li>
 *   <li>Recalculates correlation matrix on every request</li>
 *   <li>Peak runtime: 45-90 seconds per VaR calculation</li>
 *   <li>Blocks servlet threads during calculation</li>
 * </ul>
 *
 * <p><strong>ANTI-PATTERN #9: Direct JDBC in Service Layer</strong></p>
 * <ul>
 *   <li>Bypasses DAO abstraction for "performance reasons"</li>
 *   <li>Creates new connections instead of using pool</li>
 *   <li>No connection cleanup in error paths</li>
 *   <li>Hardcoded connection strings with credentials</li>
 * </ul>
 *
 * @author Legacy Systems Team
 * @version 1.0
 * @since 2004
 */
public class RiskAnalyticsServiceImpl implements RiskAnalyticsService {

    private static final Logger logger = Logger.getLogger(RiskAnalyticsServiceImpl.class);

    // ANTI-PATTERN: Hardcoded credentials
    private static final String DB_URL = "jdbc:oracle:thin:@prod-db-01:1521:VGRD";
    private static final String DB_USER = "risk_analytics";
    private static final String DB_PASS = "AnalyticsP@ss2004";

    private HoldingsDAO holdingsDAO;
    private MarketDataDAO marketDataDAO;

    /**
     * Calculates Value-at-Risk using Monte Carlo simulation.
     *
     * <p><strong>ANTI-PATTERN: Inefficient Monte Carlo Implementation</strong></p>
     * This method runs 100,000 Monte Carlo simulations in a single thread,
     * recalculating the full correlation matrix on every request. No caching,
     * no parallelization, no incremental computation.
     *
     * <p>Typical runtime: 45-90 seconds (blocks servlet thread)</p>
     */
    @Override
    public double calculateValueAtRisk(String portfolioId, double confidenceLevel, int timeHorizon) {
        logger.info("Starting VaR calculation for portfolio: " + portfolioId);

        long startTime = System.currentTimeMillis();

        try {
            // ANTI-PATTERN: Fetch all holdings without pagination
            List<Holding> holdings = holdingsDAO.getHoldingsByAccount(portfolioId);

            // ANTI-PATTERN: Recalculate correlation matrix on every request (expensive!)
            double[][] correlationMatrix = calculateCorrelationMatrix(holdings);

            // ANTI-PATTERN: Single-threaded Monte Carlo simulation
            int numSimulations = 100000; // Takes 60+ seconds
            double[] simulatedReturns = new double[numSimulations];

            for (int i = 0; i < numSimulations; i++) {
                // Simulate portfolio return for each scenario
                double portfolioReturn = 0.0;

                for (int h = 0; h < holdings.size(); h++) {
                    Holding holding = holdings.get(h);

                    // ANTI-PATTERN: Fetch historical volatility from DB on every iteration
                    double volatility = getHistoricalVolatility(holding.getSymbol());
                    double randomReturn = generateRandomReturn(volatility);

                    portfolioReturn += (holding.getMarketValue() * randomReturn);
                }

                simulatedReturns[i] = portfolioReturn;

                // ANTI-PATTERN: Log every 1000th iteration (floods logs)
                if (i % 1000 == 0) {
                    logger.debug("Completed simulation " + i + " of " + numSimulations);
                }
            }

            // Calculate VaR at specified confidence level
            java.util.Arrays.sort(simulatedReturns);
            int varIndex = (int) ((1.0 - confidenceLevel) * numSimulations);
            double var = Math.abs(simulatedReturns[varIndex]);

            long endTime = System.currentTimeMillis();
            logger.info("VaR calculation completed in " + (endTime - startTime) + "ms");

            return var;

        } catch (Exception e) {
            logger.error("Error calculating VaR: " + e.getMessage(), e);
            // ANTI-PATTERN: Return 0 instead of throwing exception
            return 0.0;
        }
    }

    /**
     * Calculates correlation matrix for all holdings.
     *
     * <p><strong>ANTI-PATTERN: No caching, recalculates every time</strong></p>
     * This method fetches 5 years of daily price data for every holding
     * and calculates the full correlation matrix from scratch. Should be
     * cached for at least 1 day, but isn't.
     */
    private double[][] calculateCorrelationMatrix(List<Holding> holdings) {
        logger.debug("Calculating correlation matrix for " + holdings.size() + " holdings");

        int n = holdings.size();
        double[][] correlationMatrix = new double[n][n];

        // ANTI-PATTERN: Nested loops with O(n²) database queries
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    correlationMatrix[i][j] = 1.0;
                } else {
                    // ANTI-PATTERN: Fetch 5 years of price data for each pair
                    String symbol1 = holdings.get(i).getSymbol();
                    String symbol2 = holdings.get(j).getSymbol();

                    double correlation = calculatePairwiseCorrelation(symbol1, symbol2);
                    correlationMatrix[i][j] = correlation;
                }
            }
        }

        return correlationMatrix;
    }

    /**
     * Calculates correlation between two securities.
     *
     * <p><strong>ANTI-PATTERN: Direct JDBC bypassing DAO layer</strong></p>
     * Uses raw JDBC connections instead of going through DAO abstraction.
     * Creates new connection on every call instead of using connection pool.
     */
    private double calculatePairwiseCorrelation(String symbol1, String symbol2) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // ANTI-PATTERN: Create new connection instead of using pool
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            stmt = conn.createStatement();

            // ANTI-PATTERN: Fetch 5 years of data (1,250 trading days) for correlation calc
            String sql = "SELECT " +
                    "    CORR(p1.close_price, p2.close_price) as correlation " +
                    "FROM market_prices p1 " +
                    "JOIN market_prices p2 ON p1.price_date = p2.price_date " +
                    "WHERE p1.symbol = '" + symbol1 + "' " +
                    "    AND p2.symbol = '" + symbol2 + "' " +
                    "    AND p1.price_date > SYSDATE - 1825"; // 5 years

            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getDouble("correlation");
            }

            return 0.0;

        } catch (Exception e) {
            logger.error("Error calculating correlation: " + e.getMessage());
            return 0.0;
        } finally {
            // ANTI-PATTERN: Connection leak if exception occurs before this point
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Error closing resources", e);
            }
        }
    }

    /**
     * Gets historical volatility for a security.
     *
     * <p><strong>ANTI-PATTERN: Called inside Monte Carlo loop (100K times!)</strong></p>
     * This should be pre-calculated and cached, but instead it runs a DB query
     * inside the Monte Carlo simulation loop.
     */
    private double getHistoricalVolatility(String symbol) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            stmt = conn.createStatement();

            // Calculate 30-day historical volatility
            String sql = "SELECT STDDEV(daily_return) * SQRT(252) as volatility " +
                    "FROM (" +
                    "    SELECT (close_price - LAG(close_price) OVER (ORDER BY price_date)) / " +
                    "           LAG(close_price) OVER (ORDER BY price_date) as daily_return " +
                    "    FROM market_prices " +
                    "    WHERE symbol = '" + symbol + "' " +
                    "        AND price_date > SYSDATE - 30" +
                    ")";

            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getDouble("volatility");
            }

            return 0.20; // Default 20% volatility

        } catch (Exception e) {
            logger.error("Error fetching volatility for " + symbol, e);
            return 0.20;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }

    /**
     * Generates random return using Box-Muller transform.
     */
    private double generateRandomReturn(double volatility) {
        // Box-Muller transform for normal distribution
        double u1 = Math.random();
        double u2 = Math.random();
        double z = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
        return z * volatility / Math.sqrt(252.0); // Daily volatility
    }

    @Override
    public RiskMetrics runStressTests(String portfolioId, List<String> scenarioIds) {
        // ANTI-PATTERN: Synchronous execution of multiple stress scenarios
        logger.info("Running " + scenarioIds.size() + " stress test scenarios");

        RiskMetrics metrics = new RiskMetrics();
        double maxDrawdown = 0.0;

        for (String scenarioId : scenarioIds) {
            // Each scenario takes 5-10 seconds
            double scenarioLoss = calculateScenarioLoss(portfolioId, scenarioId);
            if (scenarioLoss > maxDrawdown) {
                maxDrawdown = scenarioLoss;
            }
        }

        metrics.setMaxDrawdown(maxDrawdown);
        return metrics;
    }

    private double calculateScenarioLoss(String portfolioId, String scenarioId) {
        // Simplified: In reality, this would apply historical shock factors
        return Math.random() * 100000; // Simulated loss
    }

    @Override
    public RiskMetrics analyzeConcentrationRisk(String portfolioId) {
        logger.info("Analyzing concentration risk for portfolio: " + portfolioId);

        RiskMetrics metrics = new RiskMetrics();

        // ANTI-PATTERN: Multiple sequential DB queries instead of single JOIN
        List<Holding> holdings = holdingsDAO.getHoldingsByAccount(portfolioId);

        Map<String, Double> sectorConcentration = new HashMap<>();
        double totalValue = 0.0;

        for (Holding holding : holdings) {
            // ANTI-PATTERN: N+1 query to get sector for each holding
            String sector = getSectorForSymbol(holding.getSymbol());

            double currentValue = sectorConcentration.getOrDefault(sector, 0.0);
            sectorConcentration.put(sector, currentValue + holding.getMarketValue());

            totalValue += holding.getMarketValue();
        }

        // Check if any sector exceeds 30% concentration limit
        for (Map.Entry<String, Double> entry : sectorConcentration.entrySet()) {
            double concentration = entry.getValue() / totalValue;
            if (concentration > 0.30) {
                logger.warn("Concentration limit exceeded for sector: " + entry.getKey() +
                        " (" + (concentration * 100) + "%)");
            }
        }

        return metrics;
    }

    /**
     * Gets sector classification for a symbol.
     *
     * <p><strong>ANTI-PATTERN: Called in loop (N+1 pattern)</strong></p>
     */
    private String getSectorForSymbol(String symbol) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            stmt = conn.createStatement();

            String sql = "SELECT sector FROM securities WHERE symbol = '" + symbol + "'";
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getString("sector");
            }

            return "Unknown";

        } catch (Exception e) {
            logger.error("Error fetching sector for " + symbol, e);
            return "Unknown";
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    @Override
    public List<String> checkRegulatoryCompliance(String portfolioId) {
        List<String> violations = new ArrayList<>();

        // Simplified compliance checks
        RiskMetrics metrics = analyzeConcentrationRisk(portfolioId);

        // SEC 15c3-3 Customer Protection Rule checks would go here
        // FINRA Rule 4210 Margin Requirements checks would go here

        return violations;
    }

    @Override
    public double calculatePortfolioBeta(String portfolioId, String benchmarkSymbol, int periodDays) {
        logger.info("Calculating beta for portfolio " + portfolioId +
                " vs " + benchmarkSymbol + " over " + periodDays + " days");

        // ANTI-PATTERN: Simplified implementation that doesn't handle data properly
        // In reality, would need covariance and variance calculations

        return 1.0; // Simplified: assume market beta
    }

    public void setHoldingsDAO(HoldingsDAO holdingsDAO) {
        this.holdingsDAO = holdingsDAO;
    }

    public void setMarketDataDAO(MarketDataDAO marketDataDAO) {
        this.marketDataDAO = marketDataDAO;
    }
}
