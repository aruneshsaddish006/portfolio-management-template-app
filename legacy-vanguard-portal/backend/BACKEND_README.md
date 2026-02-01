# Legacy Vanguard Portal - Backend

This is a realistic legacy Java backend codebase representing a financial services application from 2013-2018. It contains intentional anti-patterns and technical debt for modernization analysis.

## Architecture Overview

### Technology Stack (LEGACY)

- **Java 8** (2014)
- **Spring Framework 4.3.18** (3 major versions behind, released 2018)
- **Servlet API 3.1** (Java EE 7, 2013)
- **Log4j 1.2.17** (EOL 2015, has security vulnerabilities)
- **MySQL 5.7**
- **SOAP Web Services** (2000s technology)

### Application Structure

```
backend/
├── src/main/java/com/vanguard/portal/
│   ├── batch/
│   │   └── BalanceAggregationJob.java        ⚠️ Long-running batch job
│   ├── dao/
│   │   ├── HoldingsDAOImpl.java               🔴 SQL injection vulnerability
│   │   ├── AccountDAOImpl.java
│   │   └── BalanceDAOImpl.java
│   ├── model/
│   │   ├── Account.java
│   │   ├── Balance.java
│   │   ├── Holding.java
│   │   └── Transaction.java
│   ├── service/
│   │   ├── BalanceServiceImpl.java            ⚠️ N+1 query problem
│   │   ├── MarketDataServiceImpl.java         📜 Legacy SOAP client
│   │   └── HoldingsServiceImpl.java
│   ├── servlet/
│   │   ├── AccountsOverviewServlet.java       📊 Main accounts page
│   │   └── BalancesHoldingsServlet.java       ⚠️ Synchronous CSV export
│   └── util/
│       └── SessionUtil.java
├── src/main/resources/
│   └── schema.sql                              🗄️ Business logic in stored procedure
└── src/main/webapp/WEB-INF/
    ├── web.xml
    └── applicationContext.xml
```

## Key Anti-Patterns and Issues

### 1. N+1 Query Problem
**File:** `BalanceServiceImpl.java` (lines 67-86)
**Issue:** Loop fetches balances one account at a time
**Impact:** 21 queries for 10 accounts instead of 3
**Symptom:** Dashboard load time: 3+ seconds

### 2. SQL Injection Vulnerability
**File:** `HoldingsDAOImpl.java` (lines 145-160)
**Issue:** String concatenation in SQL queries
**Impact:** Security breach, unauthorized data access
**Compliance:** Violates PCI-DSS, SOX regulations

### 3. Synchronous Blocking Export
**File:** `BalancesHoldingsServlet.java` (exportHoldingsToCSV method)
**Issue:** CSV generation blocks request thread for 30-60 seconds
**Impact:** Thread pool exhaustion, timeouts, poor UX
**Symptom:** 503 errors during quarterly reporting

### 4. Legacy SOAP Integration
**File:** `MarketDataServiceImpl.java`
**Issue:** Uses SOAP instead of REST APIs
**Impact:** 500-800ms latency vs 50-100ms, high bandwidth usage
**Cost:** 3x higher API costs than REST equivalent

### 5. Long-Running Batch Job
**File:** `BalanceAggregationJob.java`
**Issue:** Single-threaded processing of 2M customers (6-8 hours)
**Impact:** No restart capability, database locks, stale morning data
**Symptom:** Failed jobs require manual intervention

### 6. Business Logic in Database
**File:** `schema.sql` (stored procedure sp_aggregate_customer_balance)
**Issue:** Complex calculations in 500+ line stored procedure
**Impact:** Hard to test, debug, and version control
**Lock-in:** Database-specific (Oracle/MySQL)

## Database Schema

### Core Tables

- **accounts** - Customer account information
- **balances** - Current account balances
- **holdings** - Investment positions (stocks, bonds, funds)
- **transactions** - Financial transaction history
- **balance_summary** - Aggregated balances (populated by batch job)

### Sample Data

The schema includes sample data for testing:
- 3 accounts across 2 customers
- Balances and holdings for each account
- Various account types (IRA, Brokerage, 401k)

## Build and Run

### Prerequisites

- Java 8+
- Maven 3.x
- MySQL 5.7+

### Build

```bash
cd backend
mvn clean package
```

### Database Setup

```bash
mysql -u root -p < src/main/resources/schema.sql
```

### Deploy

Deploy the generated WAR file (`target/vanguard-portal.war`) to:
- Apache Tomcat 8.5+
- JBoss/WildFly
- Any Java EE 7 compatible server

## Known Issues and Technical Debt

### Security
- ⚠️ SQL injection in HoldingsDAOImpl.searchHoldings()
- ⚠️ Log4j 1.2.17 has known CVEs
- ⚠️ Hardcoded credentials in MarketDataServiceImpl
- ⚠️ Plain text passwords in SOAP authentication

### Performance
- ⚠️ N+1 queries in balance calculations
- ⚠️ No caching layer
- ⚠️ Synchronous blocking operations
- ⚠️ Single-threaded batch processing

### Maintainability
- ⚠️ Business logic in stored procedures
- ⚠️ Tight coupling to legacy SOAP APIs
- ⚠️ No unit tests (legacy codebase)
- ⚠️ Outdated dependencies (Spring 4.x, Servlet 3.x)

### Scalability
- ⚠️ Thread pool exhaustion during peak load
- ⚠️ Database connection pool too small
- ⚠️ No horizontal scaling support
- ⚠️ Batch job exceeds processing window

## Modernization Opportunities

1. **Migrate to Spring Boot 3.x** - Modern framework, embedded server
2. **Replace SOAP with REST APIs** - JSON, HTTP/2, better performance
3. **Implement async processing** - Message queues for exports and batch jobs
4. **Add caching layer** - Redis for frequently accessed data
5. **Refactor DAOs to use JPA** - ORM, type-safe queries
6. **Move business logic to application layer** - Testable, maintainable
7. **Implement batch processing framework** - Spring Batch for restart capability
8. **Add comprehensive test coverage** - Unit, integration, performance tests

## Use with Slingshot

This codebase is designed for analysis with Anthropic's Slingshot modernization tool. See `SLINGSHOT_GUIDE.md` for details on uploading and analyzing this code.

## Questions or Issues?

This is a demonstration codebase for legacy modernization scenarios. The anti-patterns are intentional and documented for educational purposes.
