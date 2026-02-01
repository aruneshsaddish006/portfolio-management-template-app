package com.vanguard.portal.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service interface for market data operations.
 */
public interface MarketDataService {

    Map<String, BigDecimal> getStockQuotes(String[] symbols);

    BigDecimal getStockQuote(String symbol);

    Map<String, BigDecimal> getMarketIndices();
}
