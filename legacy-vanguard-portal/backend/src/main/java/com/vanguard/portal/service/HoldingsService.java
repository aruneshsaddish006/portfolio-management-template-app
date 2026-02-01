package com.vanguard.portal.service;

import com.vanguard.portal.model.Holding;
import java.util.List;

/**
 * Service interface for holdings operations.
 */
public interface HoldingsService {

    List<Holding> getCustomerHoldings(String customerId);

    List<Holding> searchHoldings(String customerId, String symbol);

    void updateHoldingPrices(String customerId);
}
