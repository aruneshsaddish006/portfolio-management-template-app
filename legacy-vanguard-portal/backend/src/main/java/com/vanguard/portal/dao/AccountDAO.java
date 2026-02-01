package com.vanguard.portal.dao;

import com.vanguard.portal.model.Account;
import java.util.List;

/**
 * Data Access Object interface for Account operations.
 */
public interface AccountDAO {

    List<Account> findByCustomerId(String customerId);

    Account findByAccountNumber(String accountNumber);

    void save(Account account);

    void update(Account account);
}
