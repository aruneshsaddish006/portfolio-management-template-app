# Anti-Patterns Quick Reference

This document provides a quick reference to all intentional anti-patterns in this legacy codebase.

## 1. N+1 Query Problem

**File:** `src/main/java/com/vanguard/portal/service/BalanceServiceImpl.java`
**Lines:** 67-86 (calculateAccountBalance method)
**Pattern:** Method called in loop for each account, executing separate DB queries
**Impact:** 21 database queries for 10 accounts instead of 3
**Symptom:** Dashboard takes 3+ seconds to load

**Example:**
```java
for (Account account : accounts) {
    Balance balance = balanceDAO.findByAccountNumber(account.getAccountNumber()); // N+1!
    List<Map<String, Object>> transactions = balanceDAO.getRecentTransactions(accountNumber, 30); // N+1!
}
```

---

## 2. SQL Injection Vulnerability

**File:** `src/main/java/com/vanguard/portal/dao/HoldingsDAOImpl.java`
**Lines:** 145-160 (searchHoldings method)
**Pattern:** String concatenation for SQL query construction
**Impact:** Critical security vulnerability, potential data breach
**Compliance Risk:** Violates PCI-DSS, SOX

**Example:**
```java
String sql = "SELECT h.* FROM holdings h WHERE a.customer_id = '" + customerId + "' ";
sql += "AND h.symbol LIKE '%" + symbolFilter + "%' "; // VULNERABLE!
```

**Exploit:**
```
symbolFilter = "VFIAX' OR '1'='1"
→ Returns all holdings bypassing authorization
```

---

## 3. Synchronous Blocking CSV Export

**File:** `src/main/java/com/vanguard/portal/servlet/BalancesHoldingsServlet.java`
**Lines:** 133-215 (exportHoldingsToCSV method)
**Pattern:** Synchronous file generation blocking request thread
**Impact:** 30-60 second blocking operation, thread pool exhaustion
**Symptom:** 503 errors during quarterly reporting

**Example:**
```java
// This blocks for 30-60 seconds for large portfolios
for (Holding holding : holdings) { // 1000+ iterations
    writer.println(line.toString());
    Thread.sleep(5); // Simulates processing delay
}
```

---

## 4. Legacy SOAP Web Services

**File:** `src/main/java/com/vanguard/portal/service/MarketDataServiceImpl.java`
**Lines:** 42-140 (getStockQuotes method)
**Pattern:** SOAP instead of REST APIs
**Impact:** 500-800ms latency vs 50-100ms, 3x higher costs
**Technical Debt:** Verbose XML, hard to maintain

**Example:**
```java
SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
SOAPConnection soapConnection = soapConnectionFactory.createConnection();
SOAPMessage soapMessage = messageFactory.createMessage();
// 50+ lines of SOAP boilerplate...
```

---

## 5. Long-Running Batch Job

**File:** `src/main/java/com/vanguard/portal/batch/BalanceAggregationJob.java`
**Lines:** 60-120 (aggregateCustomerBalances method)
**Pattern:** Single-threaded processing of 2M records
**Impact:** 6-8 hour runtime, no restart capability
**Symptom:** Morning balance discrepancies when job fails

**Example:**
```java
@Scheduled(cron = "0 0 2 * * ?")
public void aggregateCustomerBalances() {
    for (String customerId : customerIds) { // 2,000,000 iterations!
        aggregateBalancesForCustomer(conn, customerId);
    }
}
```

---

## 6. Business Logic in Database

**File:** `src/main/resources/schema.sql`
**Lines:** 90-180 (stored procedure sp_aggregate_customer_balance)
**Pattern:** Complex calculations in 500+ line stored procedure
**Impact:** Hard to test, debug, version control
**Lock-in:** Database-specific (Oracle/MySQL)

**Example:**
```sql
CREATE PROCEDURE sp_aggregate_customer_balance(IN p_customer_id VARCHAR(20))
BEGIN
    -- 100+ lines of PL/SQL with cursors, temp tables, complex logic
    DECLARE account_cursor CURSOR FOR ...
    -- Business rules embedded in database!
END$$
```

---

## Summary Table

| # | Anti-Pattern | File | Severity | Type |
|---|--------------|------|----------|------|
| 1 | N+1 Queries | BalanceServiceImpl.java | HIGH | Performance |
| 2 | SQL Injection | HoldingsDAOImpl.java | CRITICAL | Security |
| 3 | Sync Blocking | BalancesHoldingsServlet.java | HIGH | Performance |
| 4 | Legacy SOAP | MarketDataServiceImpl.java | MEDIUM | Tech Debt |
| 5 | Long Batch | BalanceAggregationJob.java | MEDIUM | Scalability |
| 6 | DB Logic | schema.sql | MEDIUM | Maintainability |

---

## Legacy Dependencies

**File:** `pom.xml`

- Spring Framework 4.3.18 (3 major versions behind)
- Log4j 1.2.17 (EOL 2015, has CVEs)
- Servlet API 3.1 (Java EE 7, 2013)
- MySQL Connector 5.1.47 (older version)
- SOAP APIs (javax.xml.soap)

---

## Quick Testing Scenarios

### Test N+1 Query Problem
1. Access `/accounts/overview` with customer having 10+ accounts
2. Check logs: Should see 20+ database queries
3. Expected: Page load > 2 seconds

### Test SQL Injection
1. Access `/holdings?action=search&symbol=VFIAX' OR '1'='1`
2. Expected: Returns all holdings (security breach)

### Test CSV Export Blocking
1. Access `/holdings?action=export` with 1000+ holdings
2. Expected: Browser hangs for 30-60 seconds
3. Concurrent exports cause 503 errors

### Test SOAP Performance
1. Call `getStockQuotes()` with 10 symbols
2. Check logs: Should take 500-800ms
3. Compare to REST equivalent: 50-100ms

---

## Modernization Priority

1. **CRITICAL** - Fix SQL injection (security)
2. **CRITICAL** - Update Log4j (security)
3. **HIGH** - Fix N+1 queries (performance)
4. **HIGH** - Async CSV export (scalability)
5. **MEDIUM** - Replace SOAP with REST (cost/performance)
6. **MEDIUM** - Optimize batch job (reliability)
7. **MEDIUM** - Move DB logic to app layer (maintainability)

---

**Supporting (3 files):**
8. pom.xml
9. web.xml
10. applicationContext.xml

**Total:** 10 core files covering all anti-patterns
