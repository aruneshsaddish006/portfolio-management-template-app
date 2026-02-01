# Legacy Vanguard Portfolio Management System

> **A realistic legacy Java codebase for demonstrating enterprise application modernization**

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-8-orange.svg)](https://www.oracle.com/java/)
[![Spring](https://img.shields.io/badge/Spring-4.3.18-green.svg)](https://spring.io/)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

## 🎯 Overview

This repository contains a **realistic legacy asset management portfolio system** modeled after platforms like Vanguard, Fidelity, and Charles Schwab. It represents a **100-year-old financial institution's** core investment management application - stable, trusted by millions of clients, but built with outdated technologies and patterns.

**Key Stats:**
- **~4,500 lines of code** across frontend and backend
- **9 intentional legacy anti-patterns** that create real modernization challenges
- **5 fully functional HTML pages** with Vanguard-inspired design
- **25+ Java classes** implementing servlet/service/DAO architecture
- **6 critical security vulnerabilities** (CVEs and injection flaws)
- **Realistic domain model** with accounts, holdings, transactions, and risk analytics

### The Solution
This repository provides a **realistic legacy codebase** that demonstrates:
1. **Common anti-patterns** found in 20+ year old enterprise applications
2. **Real security vulnerabilities** that modernization must address
3. **Complex business logic** trapped in databases and monolithic services
4. **Like-for-like behavior preservation** challenges during migration
5. **AI-assisted analysis** using tools like Slingshot to understand the system

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Frontend Layer                         │
│  5 HTML Pages (Vanguard UI) + Chart.js Visualizations  │
└────────────┬────────────────────────────────────────────┘
             │
             │ HTTP Requests
             ▼
┌─────────────────────────────────────────────────────────┐
│                  Servlet Layer                           │
│  AccountsOverviewServlet | BalancesHoldingsServlet      │
│  PortfolioPerformanceServlet | TransactionsServlet      │
└────────────┬────────────────────────────────────────────┘
             │
             │ Service Calls
             ▼
┌─────────────────────────────────────────────────────────┐
│                   Service Layer                          │
│  BalanceService | HoldingsService | RiskAnalyticsService│
│  MarketDataService | PerformanceCalculationService      │
└────────────┬────────────────────────────────────────────┘
             │
             │ Data Access
             ▼
┌─────────────────────────────────────────────────────────┐
│                     DAO Layer                            │
│  AccountDAO | BalanceDAO | HoldingsDAO | MarketDataDAO  │
└────────────┬────────────────────────────────────────────┘
             │
             │ JDBC Connections
             ▼
┌─────────────────────────────────────────────────────────┐
│               Oracle Database 11g                        │
│  + Stored Procedures (Business Logic in DB)             │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│              Background Processes                        │
│  BalanceAggregationJob (6-8 hour batch)                │
│  MarketDataSyncJob (SOAP integration)                   │
└─────────────────────────────────────────────────────────┘
```

---

## 💻 Technology Stack

### Frontend
- **HTML5** with inline CSS (no build process)
- **Chart.js** 4.4.1 for data visualization
- **Vanilla JavaScript** (no frameworks)
- **Vanguard-inspired design** (#c00 red branding)

### Backend
- **Java 8** (outdated but still widely used in legacy systems)
- **Spring Framework 4.3.18** (Spring MVC for servlets)
- **Servlet API 3.1** (traditional request/response model)
- **JDBC** for database access (some raw JDBC bypassing DAOs)
- **JAXB** for XML processing
- **Log4j 1.2.17** ⚠️ **CVE-2019-17571** (vulnerable version)

### Database
- **Oracle 11g** compatible
- **Stored procedures** with business logic
- **No ORM** (Hibernate/JPA) - raw SQL everywhere

### Build & Deployment
- **Maven 3.x** for dependency management
- **Tomcat 8.5** application server
- **No containerization** (Docker/Kubernetes)
- **No CI/CD** pipelines

---

## ✨ Features

### Frontend Pages

1. **Accounts Overview** (`accounts-overview.html`)
   - Portfolio summary with total balance ($847,392.45)
   - YTD performance tracking (+1.49%)
   - Asset allocation donut chart
   - Account summary table (4 accounts)
   - Recent transaction feed

2. **Balances & Holdings** (`balances-holdings.html`)
   - Detailed holdings table (10 positions)
   - Sortable columns (symbol, quantity, value, gains)
   - Account filtering
   - CSV export functionality
   - Real-time price data display

3. **Portfolio Performance** (`portfolio-performance.html`)
   - Performance line chart with Chart.js
   - Time period selector (1M, 3M, 6M, YTD, 1Y, 3Y, All)
   - Benchmark comparison (vs. S&P 500)
   - Returns breakdown by account
   - Asset class performance

4. **Transaction History** (`transactions.html`)
   - Complete transaction log with pagination
   - Transaction type badges (Buy, Sell, Dividend, Fee)
   - Date range filtering
   - Account-specific filtering
   - Summary cards (net cash flow, dividends, fees)

5. **Risk Analysis** (`risk-analysis.html`)
   - Risk score gauge (6.2/10 - Moderate)
   - Value-at-Risk calculations
   - Portfolio beta and volatility metrics
   - Stress test scenarios (2008, COVID-19, Black Monday)
   - Sector concentration charts
   - Regulatory compliance checks

### Backend Capabilities

- **Account Management**: Multi-account support (Brokerage, IRA, Roth IRA)
- **Holdings Tracking**: Real-time position tracking with cost basis
- **Performance Analytics**: Time-weighted returns, benchmark comparison
- **Risk Analytics**: VaR, beta, volatility, stress testing
- **Transaction Processing**: Buy/sell orders, dividends, contributions
- **Market Data Integration**: SOAP-based price feed (legacy)
- **Batch Processing**: Nightly balance aggregation job

---

## ⚠️ Legacy Anti-Patterns

This codebase intentionally includes **9 realistic anti-patterns** that Slingshot and other analysis tools will detect:

### 1. **N+1 Query Problem** 🔴
- **Location**: `BalanceServiceImpl.java` (lines 67-86)
- **Impact**: Executes 1 query per account instead of batch loading
- **Consequence**: 100ms → 2,500ms response time for 25 accounts

### 2. **SQL Injection Vulnerability** 🔴
- **Location**: `HoldingsDAOImpl.java` (lines 145-160)
- **Impact**: String concatenation in SQL queries
- **Consequence**: OWASP Top 10, critical security flaw

### 3. **Hardcoded Database Credentials** 🔴
- **Location**: `RiskAnalyticsServiceImpl.java` (lines 45-47)
- **Impact**: Passwords in source code
- **Consequence**: Security breach if code repository is compromised

### 4. **Legacy Log4j Vulnerability** 🔴
- **Location**: `pom.xml` (Log4j 1.2.17)
- **CVE**: CVE-2019-17571 (JMSSink deserialization)
- **Consequence**: Remote code execution vulnerability

### 5. **Synchronous Blocking Operations** ⚠️
- **Location**: `BalancesHoldingsServlet.java` (lines 133-215)
- **Impact**: CSV export blocks servlet threads for 2-5 minutes
- **Consequence**: Thread pool exhaustion under load

### 6. **SOAP Service Integration** ⚠️
- **Location**: `MarketDataServiceImpl.java`
- **Impact**: Deprecated SAAJ API for market data
- **Consequence**: 10x slower than REST, vendor lock-in

### 7. **Long-Running Batch Job** ⚠️
- **Location**: `BalanceAggregationJob.java`
- **Impact**: 6-8 hour single-threaded batch process
- **Consequence**: Data staleness, no real-time updates

### 8. **Business Logic in Database** ⚠️
- **Location**: `schema.sql` (stored procedures)
- **Impact**: Complex calculations in PL/SQL
- **Consequence**: Untestable, vendor lock-in, poor maintainability

### 9. **Inefficient Monte Carlo Simulation** ⚠️
- **Location**: `RiskAnalyticsServiceImpl.java` (lines 67-150)
- **Impact**: 100,000 single-threaded simulations
- **Consequence**: 45-90 second VaR calculation blocking servlet

---

## 🚀 Getting Started

### Prerequisites

- **Java 8** (JDK 1.8.0_xxx)
- **Maven 3.6+**
- **Apache Tomcat 8.5** (or any servlet 3.1 container)
- **Oracle Database 11g+** (or PostgreSQL with minor changes)

### Quick Start (Frontend Only)

The easiest way to see the application:

```bash
# Clone the repository
git clone https://github.com/your-username/legacy-vanguard-portal.git
cd legacy-vanguard-portal

# Open any HTML page in a browser
open frontend/accounts-overview.html

# Or use a simple HTTP server
cd frontend
python3 -m http.server 8000
# Navigate to http://localhost:8000/accounts-overview.html
```

**No backend required!** The HTML pages work standalone with static data and Chart.js visualizations.

---

## 📁 Project Structure

```
legacy-vanguard-portal/
│
├── frontend/                           # Frontend HTML pages
│   ├── accounts-overview.html          # Main dashboard
│   ├── balances-holdings.html          # Holdings detail
│   ├── portfolio-performance.html      # Performance charts
│   ├── transactions.html               # Transaction history
│   └── risk-analysis.html              # Risk metrics & stress tests
│
├── backend/                            # Java backend application
│   ├── src/main/java/                  # Source code
│   │   └── com/vanguard/portal/
│   │       ├── servlet/                # Servlet layer (4 servlets)
│   │       ├── service/                # Service layer (6 services)
│   │       ├── dao/                    # DAO layer (4 DAOs + implementations)
│   │       ├── model/                  # Domain models (7 POJOs)
│   │       ├── batch/                  # Background jobs
│   │       └── util/                   # Utilities
│   │
│   ├── src/main/resources/             # Configuration files
│   │   ├── schema.sql                  # Database schema + stored procs
│   │   └── log4j.properties            # Logging configuration
│   │
│   ├── src/main/webapp/                # Web application resources
│   │   └── WEB-INF/
│   │       ├── web.xml                 # Servlet mappings
│   │       └── applicationContext.xml  # Spring configuration
│   │
│   ├── pom.xml                         # Maven dependencies
│   ├── BACKEND_README.md               # Backend documentation
│   ├── ANTI_PATTERNS_QUICK_REF.md      # Anti-pattern reference
│   └── SLINGSHOT_GUIDE.md              # Slingshot upload guide
│
├── docs/                               # Documentation
│   ├── MASTER_README.md                # Complete overview
│   ├── MODERNIZATION_CASE.md           # $3.6M business case
│   ├── SLINGSHOT_UPLOAD_GUIDE.md       # Slingshot integration
│   ├── QUICKSTART.md                   # 30-second start guide
│   └── FEATURES.md                     # Feature breakdown
│
├── START_HERE.md                       # Quick orientation guide
└── README.md                           # This file
```

**Total Files**: ~45 files
- 5 HTML pages (frontend)
- 25 Java classes (backend)
- 5 configuration files
- 10 documentation files

---

## 🏃 Running the Application

### Option 1: Frontend Only (Recommended for Quick Demo)

```bash
cd frontend
open accounts-overview.html
```

All pages work without a backend server. Navigation, charts, and interactions are client-side only.

### Option 2: Full Stack (Requires Setup)

```bash
# 1. Build the backend
cd backend
mvn clean package

# 2. Deploy WAR to Tomcat
cp target/vanguard-portal.war $TOMCAT_HOME/webapps/

# 3. Configure database connection
# Edit backend/src/main/resources/applicationContext.xml
# Update JDBC URL, username, password

# 4. Initialize database schema
# Run backend/src/main/resources/schema.sql on your Oracle database

# 5. Start Tomcat
$TOMCAT_HOME/bin/catalina.sh run

# 6. Access application
open http://localhost:8080/vanguard-portal/accounts
```

### Option 3: Upload to Slingshot for Analysis

```bash
# Zip the entire repository
cd legacy-vanguard-portal
zip -r legacy-vanguard-portal.zip . -x "*.git*" -x "*node_modules*"

# Upload to Publicis Sapient Slingshot
# 1. Go to Slingshot web interface
# 2. Create new project: "Legacy Vanguard Modernization"
# 3. Upload legacy-vanguard-portal.zip
# 4. Wait 5-10 minutes for analysis
# 5. Download generated .md files
```

## 🔧 Modernization Opportunities

### Phase 1: Critical Security Fixes (Months 1-3)
- ✅ Upgrade Log4j 1.2.17 → 2.x (CVE remediation)
- ✅ Fix SQL injection with PreparedStatements
- ✅ Move database credentials to vault/secrets manager
- ✅ Enable HTTPS/TLS for all connections

**Estimated Effort**: 2-3 months | **Risk**: Low

### Phase 2: Data Access Layer (Months 4-6)
- ✅ Implement batch loading to fix N+1 queries
- ✅ Introduce Hibernate/JPA ORM
- ✅ Extract business logic from stored procedures
- ✅ Add database connection pooling

**Estimated Effort**: 3-4 months | **Risk**: Medium

### Phase 3: Async Processing (Months 7-9)
- ✅ Convert CSV export to async with message queue
- ✅ Parallelize batch jobs with Spring Batch
- ✅ Implement caching for market data
- ✅ Move to event-driven architecture

**Estimated Effort**: 3-4 months | **Risk**: Medium-High

### Phase 4: Service Modernization (Months 10-12)
- ✅ Replace SOAP with REST/gRPC
- ✅ Containerize with Docker/Kubernetes
- ✅ Implement CI/CD pipelines
- ✅ Move to microservices (optional)

**Estimated Effort**: 4-6 months | **Risk**: High

**Total Timeline**: 12-18 months
**Total Cost**: ~$2.1M
**Total Benefit**: ~$5.7M (ROI: 171%)

---

## 📊 Key Metrics

| Metric | Current State | After Modernization | Improvement |
|--------|---------------|---------------------|-------------|
| **Page Load Time** | 3.2s | 0.8s | 75% faster |
| **API Response Time** | 2,500ms | 150ms | 94% faster |
| **Production Incidents** | 45/month | 18/month | 60% reduction |
| **Feature Delivery** | 6 weeks | 2 weeks | 67% faster |
| **Infrastructure Cost** | $180K/year | $54K/year | 70% savings |
| **Developer Productivity** | Baseline | +40% | Significant gain |

---

## 🤝 Contributing

Contributions are welcome! This repository is designed for educational and demonstration purposes.

### How to Contribute

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/your-feature`)
3. **Add more legacy anti-patterns** (we love realistic technical debt!)
4. **Improve documentation**
5. **Submit a pull request**

### Adding New Anti-Patterns

If you want to contribute more realistic legacy patterns:
- Ensure they're based on real-world legacy systems
- Document the impact and modernization approach
- Add comments explaining why it's problematic
- Update `ANTI_PATTERNS_QUICK_REF.md`

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Inspired by real legacy systems at **Vanguard**, **Fidelity**, and **Charles Schwab**

---

## 📞 Questions?

For questions about this repository or legacy modernization:

- **Documentation**: See `docs/` folder
- **Quick Start**: Read `START_HERE.md`

---

**Built with ❤️ for the legacy modernization community**

*Remember: Legacy code isn't bad code - it's code that's been successful enough to survive. This repository celebrates that while showing paths forward.*
