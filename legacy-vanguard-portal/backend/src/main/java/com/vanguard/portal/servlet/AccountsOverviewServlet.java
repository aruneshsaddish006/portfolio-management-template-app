package com.vanguard.portal.servlet;

import com.vanguard.portal.service.BalanceService;
import com.vanguard.portal.util.SessionUtil;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Main servlet for the Accounts Overview page.
 * Displays customer account summary including balances and account details.
 *
 * This is the primary entry point for the /accounts/overview endpoint
 * and one of the most frequently accessed pages in the application.
 *
 * @author Legacy Team
 * @since 2013
 */
public class AccountsOverviewServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AccountsOverviewServlet.class);
    private static final long serialVersionUID = 1L;

    private BalanceService balanceService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize Spring dependencies
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        this.balanceService = (BalanceService) context.getBean("balanceService");
        logger.info("AccountsOverviewServlet initialized");
    }

    /**
     * Handles GET requests to display the accounts overview page.
     * This method retrieves customer account data and forwards to the JSP view.
     *
     * Performance characteristics:
     * - Average response time: 2-3 seconds (due to N+1 queries in BalanceService)
     * - Peak load: 500+ requests/minute during market open
     * - Database queries per request: 15-25 (scales with number of accounts)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        logger.info("Processing accounts overview request");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customerId") == null) {
            logger.warn("No valid session found, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String customerId = (String) session.getAttribute("customerId");
        logger.info("Loading accounts overview for customer: " + customerId);

        try {
            // Retrieve account summary - this triggers the N+1 query problem
            Map<String, Object> accountSummary = balanceService.getCustomerAccountSummary(customerId);

            // Extract summary data for display
            BigDecimal totalBalance = (BigDecimal) accountSummary.get("totalBalance");
            Integer accountCount = (Integer) accountSummary.get("accountCount");

            // Set attributes for JSP rendering
            request.setAttribute("accountSummary", accountSummary);
            request.setAttribute("totalBalance", totalBalance);
            request.setAttribute("accountCount", accountCount);
            request.setAttribute("customerName", session.getAttribute("customerName"));

            // Additional customer preferences
            String displayFormat = SessionUtil.getDisplayFormat(session);
            request.setAttribute("displayFormat", displayFormat);

            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("Accounts overview loaded in " + elapsedTime + "ms for customer " + customerId);

            // Performance monitoring - log slow requests
            if (elapsedTime > 2000) {
                logger.warn("SLOW REQUEST: Accounts overview took " + elapsedTime + "ms for customer " + customerId +
                           " with " + accountCount + " accounts");
            }

            // Forward to JSP view
            request.getRequestDispatcher("/WEB-INF/jsp/accounts-overview.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading accounts overview for customer " + customerId, e);
            request.setAttribute("errorMessage", "Unable to load account information. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests for account-related actions.
     * Currently supports:
     * - Refreshing account data
     * - Filtering by account type
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customerId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        logger.info("POST request with action: " + action);

        if ("refresh".equals(action)) {
            // Refresh account data by redirecting to GET
            response.sendRedirect(request.getContextPath() + "/accounts/overview");
        } else if ("filter".equals(action)) {
            // Store filter preference in session
            String accountTypeFilter = request.getParameter("accountType");
            session.setAttribute("accountTypeFilter", accountTypeFilter);
            response.sendRedirect(request.getContextPath() + "/accounts/overview");
        } else {
            // Unknown action, just redirect
            response.sendRedirect(request.getContextPath() + "/accounts/overview");
        }
    }

    @Override
    public void destroy() {
        logger.info("AccountsOverviewServlet destroyed");
        super.destroy();
    }
}
