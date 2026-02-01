package com.vanguard.portal.service;

import com.vanguard.portal.dao.AccountDAO;
import com.vanguard.portal.dao.BalanceDAO;
import com.vanguard.portal.model.Account;
import com.vanguard.portal.model.Balance;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for balance operations.
 * Handles account balance calculations and aggregations.
 *
 * @author Legacy Team
 * @since 2015
 */
@Service("balanceService")
public class BalanceServiceImpl implements BalanceService {

    private static final Logger logger = Logger.getLogger(BalanceServiceImpl.class);

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private BalanceDAO balanceDAO;

    /**
     * Retrieves customer account summary including total balance across all accounts.
     * Used by the dashboard and accounts overview page.
     *
     * @param customerId the customer identifier
     * @return map containing account details and balances
     */
    @Override
    public Map<String, Object> getCustomerAccountSummary(String customerId) {
        logger.info("Fetching account summary for customer: " + customerId);

        Map<String, Object> summary = new HashMap<>();
        List<Account> accounts = accountDAO.findByCustomerId(customerId);

        BigDecimal totalBalance = BigDecimal.ZERO;
        List<Map<String, Object>> accountDetails = new ArrayList<>();

        // Process each account and calculate balances
        for (Account account : accounts) {
            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("accountNumber", account.getAccountNumber());
            accountInfo.put("accountType", account.getAccountType());
            accountInfo.put("accountName", account.getAccountName());

            // Calculate account balance
            BigDecimal accountBalance = calculateAccountBalance(account.getAccountNumber());
            accountInfo.put("balance", accountBalance);
            totalBalance = totalBalance.add(accountBalance);

            accountDetails.add(accountInfo);
        }

        summary.put("accounts", accountDetails);
        summary.put("totalBalance", totalBalance);
        summary.put("accountCount", accounts.size());

        logger.info("Retrieved " + accounts.size() + " accounts for customer " + customerId);
        return summary;
    }

    /**
     * PERFORMANCE ISSUE: N+1 Query Problem
     *
     * This method is called in a loop for each account (see lines 51-63 above).
     * For a customer with 10 accounts, this results in:
     *   - 1 query to get all accounts
     *   - 10 queries to get balances (one per account)
     *   - 10 queries to get transactions (one per account)
     * Total: 21 database queries instead of 3!
     *
     * IMPACT: Dashboard load time increases from 200ms to 3+ seconds for customers
     * with multiple accounts. Database connection pool exhaustion during peak hours.
     *
     * RECOMMENDED FIX: Use batch queries or JOIN operations to fetch all balances
     * in a single query, or implement a cached materialized view.
     */
    private BigDecimal calculateAccountBalance(String accountNumber) {
        // N+1 Query #1: Fetching balance for individual account
        Balance balance = balanceDAO.findByAccountNumber(accountNumber);
        BigDecimal currentBalance = balance != null ? balance.getCurrentBalance() : BigDecimal.ZERO;

        // N+1 Query #2: Fetching recent transactions for each account
        // This is particularly expensive as it scans the transactions table
        List<Map<String, Object>> recentTransactions = balanceDAO.getRecentTransactions(accountNumber, 30);

        // Apply transaction adjustments (pending transactions not yet reflected)
        for (Map<String, Object> transaction : recentTransactions) {
            String status = (String) transaction.get("status");
            if ("PENDING".equals(status)) {
                BigDecimal amount = (BigDecimal) transaction.get("amount");
                currentBalance = currentBalance.add(amount);
            }
        }

        return currentBalance;
    }

    /**
     * Calculates total portfolio value including cash and investments.
     * Used by financial advisors for portfolio analysis.
     */
    @Override
    public BigDecimal calculateTotalPortfolioValue(String customerId) {
        logger.info("Calculating total portfolio value for customer: " + customerId);

        Map<String, Object> summary = getCustomerAccountSummary(customerId);
        BigDecimal totalBalance = (BigDecimal) summary.get("totalBalance");

        // Additional calculations for investment accounts would go here
        // (simplified for this legacy system)

        return totalBalance;
    }

    /**
     * Retrieves detailed balance breakdown for a specific account.
     * Includes cash balance, pending transactions, and available balance.
     */
    @Override
    public Map<String, Object> getAccountBalanceDetails(String accountNumber) {
        logger.info("Fetching detailed balance for account: " + accountNumber);

        Map<String, Object> details = new HashMap<>();
        Balance balance = balanceDAO.findByAccountNumber(accountNumber);

        if (balance == null) {
            logger.warn("No balance found for account: " + accountNumber);
            details.put("error", "Account not found");
            return details;
        }

        details.put("currentBalance", balance.getCurrentBalance());
        details.put("availableBalance", balance.getAvailableBalance());
        details.put("pendingBalance", balance.getPendingBalance());
        details.put("lastUpdated", balance.getLastUpdated());

        return details;
    }

    /**
     * Updates account balance after transaction processing.
     * Called by the transaction processing system.
     */
    @Override
    public void updateAccountBalance(String accountNumber, BigDecimal amount, String transactionType) {
        logger.info("Updating balance for account " + accountNumber + " with amount " + amount);

        Balance balance = balanceDAO.findByAccountNumber(accountNumber);
        if (balance == null) {
            logger.error("Cannot update balance - account not found: " + accountNumber);
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }

        // Update balance based on transaction type
        BigDecimal newBalance = balance.getCurrentBalance();
        if ("CREDIT".equals(transactionType)) {
            newBalance = newBalance.add(amount);
        } else if ("DEBIT".equals(transactionType)) {
            newBalance = newBalance.subtract(amount);
        }

        balance.setCurrentBalance(newBalance);
        balanceDAO.update(balance);

        logger.info("Balance updated successfully for account: " + accountNumber);
    }
}
