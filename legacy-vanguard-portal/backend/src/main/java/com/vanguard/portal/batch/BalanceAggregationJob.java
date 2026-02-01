package com.vanguard.portal.batch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Nightly batch job to aggregate customer balances and update summary tables.
 * Runs every night at 2:00 AM EST to consolidate daily transactions into
 * account balances and portfolio summaries.
 *
 * PERFORMANCE ISSUES:
 * - Long-running database operations block other processes
 * - Business logic embedded in stored procedures
 * - No progress tracking or restart capability
 * - Single-threaded processing of millions of records
 *
 * @author Legacy Team
 * @since 2013
 */
@Component
public class BalanceAggregationJob {

    private static final Logger logger = Logger.getLogger(BalanceAggregationJob.class);

    @Autowired
    private DataSource dataSource;

    /**
     * PERFORMANCE ISSUE: Long-Running Batch Job
     *
     * This job aggregates balances for all customers and runs nightly.
     * It processes millions of transaction records in a single thread.
     *
     * PROBLEMS:
     * 1. Duration: 6-8 hours to complete (for 2M customers)
     * 2. No Parallelization: Single-threaded, processes one customer at a time
     * 3. No Restart: If job fails at 90%, must restart from beginning
     * 4. Database Lock: Holds locks on balance tables, blocking real-time updates
     * 5. No Progress Tracking: No visibility into job progress or ETA
     * 6. Memory Issues: Loads large result sets into memory at once
     *
     * IMPACT:
     * - Job frequently exceeds batch window (2 AM - 6 AM)
     * - Morning balance discrepancies when job doesn't finish
     * - Customer complaints about "stale" balance data
     * - Failed job recovery requires manual intervention
     * - Database contention affects online transaction processing
     *
     * RECOMMENDED FIX:
     * 1. Implement chunked processing (process customers in batches of 1000)
     * 2. Use Spring Batch for restart capability and progress tracking
     * 3. Parallelize across multiple threads/nodes
     * 4. Implement incremental processing (only changed accounts)
     * 5. Use optimistic locking instead of table locks
     * 6. Consider event-sourcing for real-time balance updates
     * 7. Move complex calculations out of database stored procedures
     */
    @Scheduled(cron = "0 0 2 * * ?") // Run at 2:00 AM daily
    public void aggregateCustomerBalances() {
        long startTime = System.currentTimeMillis();
        logger.info("=== Starting Balance Aggregation Job ===");

        Connection conn = null;
        int totalCustomers = 0;
        int processedCustomers = 0;
        int failedCustomers = 0;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); // Transaction per customer

            // Get list of all customers to process
            List<String> customerIds = getAllCustomerIds(conn);
            totalCustomers = customerIds.size();

            logger.info("Processing balance aggregation for " + totalCustomers + " customers");

            // PROBLEM: Single-threaded loop processing millions of customers
            for (String customerId : customerIds) {
                try {
                    // Process one customer at a time
                    aggregateBalancesForCustomer(conn, customerId);
                    processedCustomers++;

                    // Commit after each customer (to avoid giant transactions)
                    conn.commit();

                    // Log progress every 1000 customers
                    if (processedCustomers % 1000 == 0) {
                        long elapsed = System.currentTimeMillis() - startTime;
                        double percentComplete = (processedCustomers * 100.0) / totalCustomers;
                        logger.info(String.format("Progress: %d/%d customers (%.2f%%) in %d seconds",
                                processedCustomers, totalCustomers, percentComplete, elapsed / 1000));
                    }

                } catch (Exception e) {
                    logger.error("Failed to process customer: " + customerId, e);
                    failedCustomers++;
                    conn.rollback(); // Rollback this customer only
                }
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("=== Balance Aggregation Job Completed ===");
            logger.info("Total time: " + (elapsedTime / 1000 / 60) + " minutes");
            logger.info("Processed: " + processedCustomers + " customers");
            logger.info("Failed: " + failedCustomers + " customers");

            // Alert if job took too long
            if (elapsedTime > 4 * 60 * 60 * 1000) { // 4 hours
                logger.warn("PERFORMANCE WARNING: Batch job exceeded 4 hours!");
            }

        } catch (Exception e) {
            logger.error("Critical error in balance aggregation job", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                }
            }
        }
    }

    /**
     * PROBLEM: Business Logic in Stored Procedure
     *
     * This method calls a stored procedure that contains complex business logic
     * for balance calculations. This creates several issues:
     *
     * 1. Hard to Test: Can't unit test business logic in stored procedures
     * 2. Hard to Debug: Limited debugging tools for SQL
     * 3. Hard to Version: Database schema changes require DBA involvement
     * 4. Database Lock-in: Stored procedures are database-specific (Oracle)
     * 5. No Code Review: Business logic changes bypass code review process
     * 6. Performance: Stored procedure does full table scans
     *
     * The stored procedure (sp_aggregate_customer_balance) contains 500+ lines
     * of PL/SQL with nested cursors, temp tables, and complex calculations.
     */
    private void aggregateBalancesForCustomer(Connection conn, String customerId) throws SQLException {
        // Call stored procedure with business logic
        String sql = "{call sp_aggregate_customer_balance(?)}";

        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, customerId);

            // BLOCKING OPERATION: Stored procedure can take 1-5 seconds per customer
            // For 2M customers, this is 2M-10M seconds = 23-115 days if purely sequential!
            // (In practice, it's parallelized at DB level, but still slow)
            stmt.execute();
        }
    }

    /**
     * Retrieves all customer IDs that need balance aggregation.
     * PROBLEM: Loads entire result set into memory (2M+ records).
     */
    private List<String> getAllCustomerIds(Connection conn) throws SQLException {
        List<String> customerIds = new ArrayList<>();

        String sql = "SELECT DISTINCT customer_id FROM accounts WHERE status = 'ACTIVE' ORDER BY customer_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // MEMORY ISSUE: Loading 2M customer IDs into ArrayList
            while (rs.next()) {
                customerIds.add(rs.getString("customer_id"));
            }
        }

        return customerIds;
    }

    /**
     * Manual job trigger for testing or recovery scenarios.
     * Called by operations team when nightly job fails.
     */
    public void runManualAggregation() {
        logger.warn("Manual balance aggregation triggered");
        aggregateCustomerBalances();
    }
}
