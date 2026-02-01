package com.vanguard.portal.dao;

import com.vanguard.portal.model.Holding;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Holdings table.
 * Manages investment holdings and positions for customer accounts.
 *
 * @author Legacy Team
 * @since 2014
 */
@Repository("holdingsDAO")
public class HoldingsDAOImpl implements HoldingsDAO {

    private static final Logger logger = Logger.getLogger(HoldingsDAOImpl.class);

    @Autowired
    private DataSource dataSource;

    /**
     * Retrieves all holdings for a specific account.
     * Uses prepared statements for safe SQL execution.
     */
    @Override
    public List<Holding> findByAccountNumber(String accountNumber) {
        logger.info("Fetching holdings for account: " + accountNumber);

        List<Holding> holdings = new ArrayList<>();
        String sql = "SELECT * FROM holdings WHERE account_number = ? ORDER BY purchase_date DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                holdings.add(mapResultSetToHolding(rs));
            }

            logger.info("Found " + holdings.size() + " holdings for account " + accountNumber);

        } catch (SQLException e) {
            logger.error("Error fetching holdings for account " + accountNumber, e);
            throw new RuntimeException("Database error", e);
        }

        return holdings;
    }

    /**
     * Retrieves holdings by customer ID across all accounts.
     * Provides portfolio-wide view of investments.
     */
    @Override
    public List<Holding> findByCustomerId(String customerId) {
        logger.info("Fetching holdings for customer: " + customerId);

        List<Holding> holdings = new ArrayList<>();
        String sql = "SELECT h.* FROM holdings h " +
                     "JOIN accounts a ON h.account_number = a.account_number " +
                     "WHERE a.customer_id = ? " +
                     "ORDER BY h.market_value DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                holdings.add(mapResultSetToHolding(rs));
            }

            logger.info("Found " + holdings.size() + " total holdings for customer " + customerId);

        } catch (SQLException e) {
            logger.error("Error fetching holdings for customer " + customerId, e);
            throw new RuntimeException("Database error", e);
        }

        return holdings;
    }

    /**
     * SECURITY VULNERABILITY: SQL Injection
     *
     * This method constructs SQL queries by concatenating user input directly
     * into the SQL string without proper parameterization or sanitization.
     *
     * EXPLOIT EXAMPLE:
     *   symbolFilter = "VFIAX' OR '1'='1"
     *   Resulting SQL: ... WHERE symbol LIKE '%VFIAX' OR '1'='1%'
     *   This returns ALL holdings, bypassing authorization checks!
     *
     * ATTACK VECTOR:
     *   symbolFilter = "'; DROP TABLE holdings; --"
     *   Could potentially delete the entire holdings table!
     *
     * IMPACT: Data breach, unauthorized access to customer holdings,
     * potential data loss or corruption.
     *
     * COMPLIANCE RISK: Violates PCI-DSS, SOX, and financial data protection
     * regulations. Could result in regulatory fines and legal liability.
     *
     * RECOMMENDED FIX: Use PreparedStatement with parameter binding (?)
     * instead of string concatenation. Implement input validation and
     * sanitization at the service layer.
     */
    @Override
    public List<Holding> searchHoldings(String customerId, String symbolFilter, String accountType) {
        logger.info("Searching holdings for customer: " + customerId + " with symbol filter: " + symbolFilter);

        List<Holding> holdings = new ArrayList<>();

        // VULNERABILITY: Direct string concatenation creates SQL injection risk
        String sql = "SELECT h.* FROM holdings h " +
                     "JOIN accounts a ON h.account_number = a.account_number " +
                     "WHERE a.customer_id = '" + customerId + "' ";

        // Additional SQL injection points
        if (symbolFilter != null && !symbolFilter.isEmpty()) {
            sql += "AND h.symbol LIKE '%" + symbolFilter + "%' ";
        }

        if (accountType != null && !accountType.isEmpty()) {
            sql += "AND a.account_type = '" + accountType + "' ";
        }

        sql += "ORDER BY h.market_value DESC";

        logger.debug("Executing SQL: " + sql); // Logging the unsafe SQL

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) { // Using Statement instead of PreparedStatement

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                holdings.add(mapResultSetToHolding(rs));
            }

            logger.info("Search returned " + holdings.size() + " holdings");

        } catch (SQLException e) {
            logger.error("Error searching holdings with filter: " + symbolFilter, e);
            throw new RuntimeException("Database error", e);
        }

        return holdings;
    }

    /**
     * Retrieves top performing holdings for a customer.
     * Used in portfolio performance reports.
     */
    @Override
    public List<Holding> getTopPerformers(String customerId, int limit) {
        logger.info("Fetching top " + limit + " performers for customer: " + customerId);

        List<Holding> holdings = new ArrayList<>();
        String sql = "SELECT h.* FROM holdings h " +
                     "JOIN accounts a ON h.account_number = a.account_number " +
                     "WHERE a.customer_id = ? " +
                     "AND h.gain_loss_pct > 0 " +
                     "ORDER BY h.gain_loss_pct DESC " +
                     "LIMIT ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                holdings.add(mapResultSetToHolding(rs));
            }

        } catch (SQLException e) {
            logger.error("Error fetching top performers", e);
            throw new RuntimeException("Database error", e);
        }

        return holdings;
    }

    /**
     * Saves or updates a holding record.
     */
    @Override
    public void save(Holding holding) {
        logger.info("Saving holding: " + holding.getSymbol() + " for account " + holding.getAccountNumber());

        String sql = "INSERT INTO holdings (account_number, symbol, quantity, purchase_price, " +
                     "current_price, market_value, gain_loss_pct, purchase_date, last_updated) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "quantity = ?, current_price = ?, market_value = ?, gain_loss_pct = ?, last_updated = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, holding.getAccountNumber());
            stmt.setString(2, holding.getSymbol());
            stmt.setBigDecimal(3, holding.getQuantity());
            stmt.setBigDecimal(4, holding.getPurchasePrice());
            stmt.setBigDecimal(5, holding.getCurrentPrice());
            stmt.setBigDecimal(6, holding.getMarketValue());
            stmt.setBigDecimal(7, holding.getGainLossPct());
            stmt.setDate(8, new java.sql.Date(holding.getPurchaseDate().getTime()));
            stmt.setTimestamp(9, new java.sql.Timestamp(new Date().getTime()));

            // ON DUPLICATE KEY UPDATE parameters
            stmt.setBigDecimal(10, holding.getQuantity());
            stmt.setBigDecimal(11, holding.getCurrentPrice());
            stmt.setBigDecimal(12, holding.getMarketValue());
            stmt.setBigDecimal(13, holding.getGainLossPct());
            stmt.setTimestamp(14, new java.sql.Timestamp(new Date().getTime()));

            stmt.executeUpdate();

            logger.info("Holding saved successfully");

        } catch (SQLException e) {
            logger.error("Error saving holding", e);
            throw new RuntimeException("Database error", e);
        }
    }

    /**
     * Maps database result set to Holding domain object.
     */
    private Holding mapResultSetToHolding(ResultSet rs) throws SQLException {
        Holding holding = new Holding();
        holding.setHoldingId(rs.getLong("holding_id"));
        holding.setAccountNumber(rs.getString("account_number"));
        holding.setSymbol(rs.getString("symbol"));
        holding.setQuantity(rs.getBigDecimal("quantity"));
        holding.setPurchasePrice(rs.getBigDecimal("purchase_price"));
        holding.setCurrentPrice(rs.getBigDecimal("current_price"));
        holding.setMarketValue(rs.getBigDecimal("market_value"));
        holding.setGainLossPct(rs.getBigDecimal("gain_loss_pct"));
        holding.setPurchaseDate(rs.getDate("purchase_date"));
        holding.setLastUpdated(rs.getTimestamp("last_updated"));
        return holding;
    }
}
