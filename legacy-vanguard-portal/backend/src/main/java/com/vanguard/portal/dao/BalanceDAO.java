package com.vanguard.portal.dao;

import com.vanguard.portal.model.Balance;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object interface for Balance operations.
 */
public interface BalanceDAO {

    Balance findByAccountNumber(String accountNumber);

    List<Map<String, Object>> getRecentTransactions(String accountNumber, int days);

    void update(Balance balance);

    void save(Balance balance);
}
