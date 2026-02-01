package com.vanguard.portal.service;

import com.vanguard.portal.dao.HoldingsDAO;
import com.vanguard.portal.model.Holding;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service("holdingsService")
public class HoldingsServiceImpl implements HoldingsService {

    private static final Logger logger = Logger.getLogger(HoldingsServiceImpl.class);

    @Autowired
    private HoldingsDAO holdingsDAO;

    @Autowired
    private MarketDataService marketDataService;

    @Override
    public List<Holding> getCustomerHoldings(String customerId) {
        logger.info("Fetching holdings for customer: " + customerId);
        return holdingsDAO.findByCustomerId(customerId);
    }

    @Override
    public List<Holding> searchHoldings(String customerId, String symbol) {
        logger.info("Searching holdings for customer: " + customerId + ", symbol: " + symbol);
        return holdingsDAO.searchHoldings(customerId, symbol, null);
    }

    @Override
    public void updateHoldingPrices(String customerId) {
        logger.info("Updating holding prices for customer: " + customerId);

        List<Holding> holdings = holdingsDAO.findByCustomerId(customerId);

        // Get unique symbols
        String[] symbols = holdings.stream()
                .map(Holding::getSymbol)
                .distinct()
                .toArray(String[]::new);

        // Fetch current prices via SOAP
        Map<String, BigDecimal> currentPrices = marketDataService.getStockQuotes(symbols);

        // Update each holding with current price
        for (Holding holding : holdings) {
            BigDecimal currentPrice = currentPrices.get(holding.getSymbol());
            if (currentPrice != null) {
                holding.setCurrentPrice(currentPrice);
                holding.setMarketValue(holding.getQuantity().multiply(currentPrice));

                // Calculate gain/loss percentage
                BigDecimal costBasis = holding.getPurchasePrice();
                BigDecimal gainLoss = currentPrice.subtract(costBasis);
                BigDecimal gainLossPct = gainLoss.divide(costBasis, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"));
                holding.setGainLossPct(gainLossPct);

                holdingsDAO.save(holding);
            }
        }

        logger.info("Updated prices for " + holdings.size() + " holdings");
    }
}
