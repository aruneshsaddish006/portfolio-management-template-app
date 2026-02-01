package com.vanguard.portal.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service interface for balance operations.
 */
public interface BalanceService {

    Map<String, Object> getCustomerAccountSummary(String customerId);

    BigDecimal calculateTotalPortfolioValue(String customerId);

    Map<String, Object> getAccountBalanceDetails(String accountNumber);

    void updateAccountBalance(String accountNumber, BigDecimal amount, String transactionType);
}
