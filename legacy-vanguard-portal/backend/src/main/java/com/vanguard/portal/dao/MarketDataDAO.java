package com.vanguard.portal.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for market price data and historical quotes.
 *
 * @author Legacy Systems Team
 * @version 1.0
 * @since 2004
 */
public interface MarketDataDAO {

    /**
     * Gets the current market price for a security.
     *
     * @param symbol the ticker symbol
     * @return current price, or null if not found
     */
    BigDecimal getCurrentPrice(String symbol);

    /**
     * Gets historical prices for a security over a date range.
     *
     * @param symbol the ticker symbol
     * @param startDate start of date range
     * @param endDate end of date range
     * @return map of date to closing price
     */
    Map<Date, BigDecimal> getHistoricalPrices(String symbol, Date startDate, Date endDate);

    /**
     * Gets the daily price change for a security.
     *
     * @param symbol the ticker symbol
     * @return price change amount
     */
    BigDecimal getDailyPriceChange(String symbol);

    /**
     * Gets the daily price change percentage.
     *
     * @param symbol the ticker symbol
     * @return price change as percentage
     */
    Double getDailyPriceChangePercent(String symbol);

    /**
     * Gets historical volatility (standard deviation of returns).
     *
     * @param symbol the ticker symbol
     * @param periodDays number of days for calculation
     * @return annualized volatility
     */
    Double getHistoricalVolatility(String symbol, int periodDays);

    /**
     * Gets the sector classification for a security.
     *
     * @param symbol the ticker symbol
     * @return sector name (e.g., "Technology", "Healthcare")
     */
    String getSectorForSymbol(String symbol);

    /**
     * Batch fetches current prices for multiple symbols.
     *
     * @param symbols list of ticker symbols
     * @return map of symbol to current price
     */
    Map<String, BigDecimal> getCurrentPrices(List<String> symbols);
}
