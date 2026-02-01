package com.vanguard.portal.dao;

import com.vanguard.portal.model.Holding;
import java.util.List;

/**
 * Data Access Object interface for Holdings operations.
 */
public interface HoldingsDAO {

    List<Holding> findByAccountNumber(String accountNumber);

    List<Holding> findByCustomerId(String customerId);

    List<Holding> searchHoldings(String customerId, String symbolFilter, String accountType);

    List<Holding> getTopPerformers(String customerId, int limit);

    void save(Holding holding);
}
