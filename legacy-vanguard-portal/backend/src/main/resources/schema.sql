-- Legacy Vanguard Portal Database Schema
-- Created: 2013
-- Last Modified: 2018
-- Database: MySQL 5.7

-- Accounts table
CREATE TABLE IF NOT EXISTS accounts (
    account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    customer_id VARCHAR(20) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    account_name VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    open_date DATE NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_id (customer_id),
    INDEX idx_account_type (account_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Balances table
CREATE TABLE IF NOT EXISTS balances (
    balance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    current_balance DECIMAL(15,2) DEFAULT 0.00,
    available_balance DECIMAL(15,2) DEFAULT 0.00,
    pending_balance DECIMAL(15,2) DEFAULT 0.00,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number),
    UNIQUE KEY uk_account_number (account_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Holdings table
CREATE TABLE IF NOT EXISTS holdings (
    holding_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    quantity DECIMAL(15,4) NOT NULL,
    purchase_price DECIMAL(10,2) NOT NULL,
    current_price DECIMAL(10,2) DEFAULT 0.00,
    market_value DECIMAL(15,2) DEFAULT 0.00,
    gain_loss_pct DECIMAL(8,4) DEFAULT 0.00,
    purchase_date DATE NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number),
    INDEX idx_account_number (account_number),
    INDEX idx_symbol (symbol),
    UNIQUE KEY uk_account_symbol (account_number, symbol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) DEFAULT 'PENDING',
    transaction_date DATE NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number),
    INDEX idx_account_number (account_number),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Balance summary table (populated by nightly batch job)
CREATE TABLE IF NOT EXISTS balance_summary (
    summary_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    total_balance DECIMAL(18,2) DEFAULT 0.00,
    total_equity DECIMAL(18,2) DEFAULT 0.00,
    total_cash DECIMAL(18,2) DEFAULT 0.00,
    account_count INT DEFAULT 0,
    last_aggregated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================================================
-- STORED PROCEDURE: sp_aggregate_customer_balance
-- ============================================================================
-- ANTI-PATTERN: Business Logic in Database
--
-- This stored procedure contains complex business logic for balance aggregation.
-- It's called by the nightly batch job (BalanceAggregationJob.java).
--
-- PROBLEMS:
-- 1. Business logic is embedded in the database, making it hard to test
-- 2. No version control integration (changes require DBA intervention)
-- 3. Database-specific (Oracle/MySQL specific syntax)
-- 4. Performance issues with cursors and temp tables
-- 5. Hard to debug and maintain
-- 6. Bypasses application-layer security and validation
--
-- This procedure does the following:
-- - Aggregates all account balances for a customer
-- - Calculates total equity from holdings
-- - Handles currency conversions (not shown for brevity)
-- - Updates the balance_summary table
--
-- In a modern architecture, this logic would be in the application layer
-- where it can be unit tested, versioned, and optimized.
-- ============================================================================

DELIMITER $$

CREATE PROCEDURE sp_aggregate_customer_balance(IN p_customer_id VARCHAR(20))
BEGIN
    DECLARE v_total_balance DECIMAL(18,2) DEFAULT 0.00;
    DECLARE v_total_equity DECIMAL(18,2) DEFAULT 0.00;
    DECLARE v_total_cash DECIMAL(18,2) DEFAULT 0.00;
    DECLARE v_account_count INT DEFAULT 0;
    DECLARE v_account_number VARCHAR(20);
    DECLARE v_current_balance DECIMAL(15,2);
    DECLARE done INT DEFAULT FALSE;

    -- Cursor to iterate through all accounts for the customer
    -- PERFORMANCE ISSUE: Cursor-based iteration is slow for large datasets
    DECLARE account_cursor CURSOR FOR
        SELECT account_number
        FROM accounts
        WHERE customer_id = p_customer_id AND status = 'ACTIVE';

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- Create temporary table for intermediate calculations
    CREATE TEMPORARY TABLE IF NOT EXISTS temp_balances (
        account_number VARCHAR(20),
        cash_balance DECIMAL(15,2),
        equity_balance DECIMAL(15,2)
    );

    -- Open cursor and process each account
    OPEN account_cursor;

    account_loop: LOOP
        FETCH account_cursor INTO v_account_number;

        IF done THEN
            LEAVE account_loop;
        END IF;

        -- Get cash balance
        SELECT IFNULL(current_balance, 0.00) INTO v_current_balance
        FROM balances
        WHERE account_number = v_account_number;

        -- Calculate equity value from holdings
        SELECT IFNULL(SUM(market_value), 0.00) INTO v_total_equity
        FROM holdings
        WHERE account_number = v_account_number;

        -- Insert into temp table
        INSERT INTO temp_balances (account_number, cash_balance, equity_balance)
        VALUES (v_account_number, v_current_balance, v_total_equity);

        SET v_account_count = v_account_count + 1;

    END LOOP;

    CLOSE account_cursor;

    -- Aggregate totals from temp table
    SELECT
        IFNULL(SUM(cash_balance), 0.00),
        IFNULL(SUM(equity_balance), 0.00),
        IFNULL(SUM(cash_balance + equity_balance), 0.00)
    INTO v_total_cash, v_total_equity, v_total_balance
    FROM temp_balances;

    -- Update or insert into balance_summary table
    INSERT INTO balance_summary (customer_id, total_balance, total_equity, total_cash, account_count, last_aggregated)
    VALUES (p_customer_id, v_total_balance, v_total_equity, v_total_cash, v_account_count, NOW())
    ON DUPLICATE KEY UPDATE
        total_balance = v_total_balance,
        total_equity = v_total_equity,
        total_cash = v_total_cash,
        account_count = v_account_count,
        last_aggregated = NOW();

    -- Clean up
    DROP TEMPORARY TABLE IF EXISTS temp_balances;

    -- Log completion (in real implementation, this would go to audit table)
    -- SELECT CONCAT('Aggregated balance for customer ', p_customer_id, ': $', v_total_balance) AS result;

END$$

DELIMITER ;

-- Sample data for testing
INSERT INTO accounts (account_number, customer_id, account_type, account_name, status, open_date) VALUES
('ACC001', 'CUST001', 'IRA', 'Traditional IRA', 'ACTIVE', '2015-01-15'),
('ACC002', 'CUST001', 'BROKERAGE', 'Individual Brokerage', 'ACTIVE', '2016-03-20'),
('ACC003', 'CUST002', '401K', 'Company 401(k)', 'ACTIVE', '2014-06-10');

INSERT INTO balances (account_number, current_balance, available_balance, pending_balance) VALUES
('ACC001', 50000.00, 48000.00, 2000.00),
('ACC002', 125000.00, 125000.00, 0.00),
('ACC003', 75000.00, 72000.00, 3000.00);

INSERT INTO holdings (account_number, symbol, quantity, purchase_price, current_price, market_value, gain_loss_pct, purchase_date) VALUES
('ACC001', 'VFIAX', 100.50, 250.00, 320.50, 32200.25, 28.20, '2018-01-10'),
('ACC002', 'VTSAX', 200.00, 105.00, 115.75, 23150.00, 10.24, '2019-05-15'),
('ACC003', 'VBTLX', 500.00, 85.00, 88.25, 44125.00, 3.82, '2017-11-20');
