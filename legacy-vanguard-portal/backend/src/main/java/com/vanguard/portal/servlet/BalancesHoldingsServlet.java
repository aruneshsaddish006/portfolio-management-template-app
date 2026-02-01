package com.vanguard.portal.servlet;

import com.vanguard.portal.dao.HoldingsDAO;
import com.vanguard.portal.model.Holding;
import com.vanguard.portal.service.HoldingsService;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Servlet for viewing and exporting customer holdings and balances.
 * Provides both HTML views and CSV export functionality.
 *
 * PERFORMANCE ISSUE: Synchronous CSV generation blocks request thread
 * and can cause timeout issues for customers with large portfolios.
 *
 * @author Legacy Team
 * @since 2014
 */
public class BalancesHoldingsServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(BalancesHoldingsServlet.class);
    private static final long serialVersionUID = 1L;

    private HoldingsService holdingsService;
    private HoldingsDAO holdingsDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        this.holdingsService = (HoldingsService) context.getBean("holdingsService");
        this.holdingsDAO = (HoldingsDAO) context.getBean("holdingsDAO");
        logger.info("BalancesHoldingsServlet initialized");
    }

    /**
     * Displays holdings and balances page.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customerId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String customerId = (String) session.getAttribute("customerId");
        String action = request.getParameter("action");

        // Handle CSV export request
        if ("export".equals(action)) {
            exportHoldingsToCSV(customerId, request, response);
            return;
        }

        // Handle search/filter request
        if ("search".equals(action)) {
            handleSearch(customerId, request, response);
            return;
        }

        // Default: display holdings page
        displayHoldingsPage(customerId, request, response);
    }

    /**
     * Displays the main holdings page with all customer holdings.
     */
    private void displayHoldingsPage(String customerId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("Displaying holdings page for customer: " + customerId);

        try {
            List<Holding> holdings = holdingsDAO.findByCustomerId(customerId);

            // Calculate portfolio summary
            BigDecimal totalMarketValue = BigDecimal.ZERO;
            BigDecimal totalGainLoss = BigDecimal.ZERO;

            for (Holding holding : holdings) {
                totalMarketValue = totalMarketValue.add(holding.getMarketValue());
                BigDecimal costBasis = holding.getQuantity().multiply(holding.getPurchasePrice());
                BigDecimal gainLoss = holding.getMarketValue().subtract(costBasis);
                totalGainLoss = totalGainLoss.add(gainLoss);
            }

            request.setAttribute("holdings", holdings);
            request.setAttribute("totalMarketValue", totalMarketValue);
            request.setAttribute("totalGainLoss", totalGainLoss);
            request.setAttribute("holdingsCount", holdings.size());

            request.getRequestDispatcher("/WEB-INF/jsp/holdings.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error displaying holdings page", e);
            request.setAttribute("errorMessage", "Unable to load holdings data");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles search and filter requests.
     * SECURITY ISSUE: Uses vulnerable searchHoldings method with SQL injection risk.
     */
    private void handleSearch(String customerId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String symbolFilter = request.getParameter("symbol");
        String accountType = request.getParameter("accountType");

        logger.info("Searching holdings for customer " + customerId + " with symbol: " + symbolFilter);

        try {
            // VULNERABILITY: This method has SQL injection vulnerability
            List<Holding> holdings = holdingsDAO.searchHoldings(customerId, symbolFilter, accountType);

            request.setAttribute("holdings", holdings);
            request.setAttribute("searchSymbol", symbolFilter);
            request.setAttribute("searchAccountType", accountType);

            request.getRequestDispatcher("/WEB-INF/jsp/holdings.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error searching holdings", e);
            request.setAttribute("errorMessage", "Search failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    /**
     * PERFORMANCE ISSUE: Synchronous Blocking CSV Export
     *
     * This method generates CSV files synchronously in the request thread,
     * causing several critical problems:
     *
     * PROBLEMS:
     * 1. Thread Blocking: Request thread is blocked for 30-60 seconds for large portfolios
     * 2. Timeout Risk: Customers with 1000+ holdings exceed servlet timeout (30s)
     * 3. Resource Exhaustion: Multiple concurrent exports can exhaust thread pool
     * 4. Poor UX: User's browser shows loading spinner for entire duration
     * 5. No Progress Feedback: User has no indication of export progress
     *
     * IMPACT:
     * - Production incidents during quarterly reporting (100+ concurrent exports)
     * - Thread pool exhaustion leads to 503 errors for ALL users
     * - Customer complaints about "frozen" export page
     *
     * RECOMMENDED FIX:
     * 1. Implement async export with message queue (RabbitMQ/Kafka)
     * 2. Generate file in background job
     * 3. Notify customer via email or UI when ready
     * 4. Store in S3/blob storage with expiring download link
     * 5. Add progress indicator and estimated completion time
     *
     * MODERN ALTERNATIVE:
     * - Use streaming CSV generation with chunked transfer encoding
     * - Implement WebSocket for real-time progress updates
     * - Consider pagination with client-side CSV assembly
     */
    private void exportHoldingsToCSV(String customerId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        logger.info("Starting CSV export for customer: " + customerId);

        try {
            // Fetch ALL holdings - this can be 1000+ records for large portfolios
            List<Holding> holdings = holdingsDAO.findByCustomerId(customerId);

            logger.info("Retrieved " + holdings.size() + " holdings for CSV export");

            // Set response headers for CSV download
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"holdings_" + customerId + ".csv\"");

            PrintWriter writer = response.getWriter();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Write CSV header
            writer.println("Account Number,Symbol,Quantity,Purchase Price,Current Price,Market Value,Gain/Loss %,Purchase Date");

            // BLOCKING OPERATION: Write all holdings synchronously
            // For 1000 holdings, this takes 30-60 seconds
            for (Holding holding : holdings) {
                StringBuilder line = new StringBuilder();
                line.append(holding.getAccountNumber()).append(",");
                line.append(holding.getSymbol()).append(",");
                line.append(holding.getQuantity()).append(",");
                line.append(holding.getPurchasePrice()).append(",");
                line.append(holding.getCurrentPrice()).append(",");
                line.append(holding.getMarketValue()).append(",");
                line.append(holding.getGainLossPct()).append("%,");
                line.append(dateFormat.format(holding.getPurchaseDate()));

                writer.println(line.toString());

                // Simulate realistic processing delay (database fetch, calculations)
                // In reality, this includes market data lookups and currency conversions
                if (holdings.size() > 100) {
                    try {
                        Thread.sleep(5); // 5ms per holding = 5 seconds for 1000 holdings
                    } catch (InterruptedException e) {
                        logger.warn("CSV export interrupted", e);
                    }
                }
            }

            writer.flush();

            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("CSV export completed in " + elapsedTime + "ms for " + holdings.size() + " holdings");

            // Alert on slow exports
            if (elapsedTime > 10000) {
                logger.warn("SLOW EXPORT: CSV generation took " + elapsedTime + "ms for customer " + customerId);
            }

        } catch (Exception e) {
            logger.error("Error exporting holdings to CSV for customer " + customerId, e);
            response.setContentType("text/html");
            response.getWriter().println("<html><body><h3>Export failed. Please try again later.</h3></body></html>");
        }
    }

    @Override
    public void destroy() {
        logger.info("BalancesHoldingsServlet destroyed");
        super.destroy();
    }
}
