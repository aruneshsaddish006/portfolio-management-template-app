# 📂 Complete File Index - Legacy Vanguard Portal

> **Quick reference guide to all files in this repository**


## 🌐 Frontend Pages (5 HTML Files)

All located in `frontend/` directory

| File | Purpose | Features | Backend Service |
|------|---------|----------|----------------|
| **accounts-overview.html** | Main dashboard | Balance cards, asset mix chart, account table | AccountsOverviewServlet |
| **balances-holdings.html** | Holdings detail | Position table, filters, CSV export | BalancesHoldingsServlet |
| **portfolio-performance.html** | Performance charts | Line charts, time periods, benchmark comparison | PerformanceServlet |
| **transactions.html** | Transaction history | Transaction table, pagination, filtering | TransactionsServlet |
| **risk-analysis.html** | Risk metrics | Risk gauge, VaR, stress tests, sector charts | RiskAnalyticsService |

**Open any file in a browser - they work standalone (no server needed)!**

---

## ☕ Backend Java Files (28 Files)

### **Servlets** (2 files) - `backend/src/main/java/com/vanguard/portal/servlet/`

| File | Purpose | Anti-Patterns |
|------|---------|---------------|
| `AccountsOverviewServlet.java` | Main dashboard controller | None (orchestration) |
| `BalancesHoldingsServlet.java` | Holdings & CSV export | ⚠️ Synchronous blocking (lines 133-215) |

### **Services** (8 files) - `backend/src/main/java/com/vanguard/portal/service/`

| File | Purpose | Anti-Patterns |
|------|---------|---------------|
| `BalanceService.java` | Interface for balance operations | - |
| `BalanceServiceImpl.java` | Balance calculations | 🔴 N+1 query problem (lines 67-86) |
| `HoldingsService.java` | Interface for holdings operations | - |
| `HoldingsServiceImpl.java` | Holdings management | None |
| `MarketDataService.java` | Interface for market data | - |
| `MarketDataServiceImpl.java` | Market data integration | ⚠️ Legacy SOAP service |
| `RiskAnalyticsService.java` | Interface for risk calculations | - |
| `RiskAnalyticsServiceImpl.java` | Risk analytics engine | 🔴 Hardcoded credentials<br>🔴 Inefficient Monte Carlo<br>🔴 Direct JDBC |

### **DAOs** (5 files) - `backend/src/main/java/com/vanguard/portal/dao/`

| File | Purpose | Anti-Patterns |
|------|---------|---------------|
| `AccountDAO.java` | Account data access interface | - |
| `AccountDAOImpl.java` | Account database operations | None |
| `BalanceDAO.java` | Balance data access interface | - |
| `BalanceDAOImpl.java` | Balance database operations | None |
| `HoldingsDAO.java` | Holdings data access interface | - |
| `HoldingsDAOImpl.java` | Holdings database operations | 🔴 SQL injection (lines 145-160) |
| `MarketDataDAO.java` | Market data access interface | - |

### **Models** (6 files) - `backend/src/main/java/com/vanguard/portal/model/`

| File | Purpose | Fields |
|------|---------|--------|
| `Account.java` | Account domain model | accountNumber, accountName, accountType, balance |
| `Balance.java` | Balance snapshot model | accountId, totalValue, cashBalance, investedValue |
| `Holding.java` | Security position model | symbol, quantity, marketValue, costBasis, gainLoss |
| `Transaction.java` | Transaction record model | transactionId, type, symbol, quantity, amount, date |
| `Portfolio.java` | Portfolio aggregation model | portfolioId, totalValue, ytdReturn, accounts |
| `RiskMetrics.java` | Risk analytics model | VaR, beta, volatility, Sharpe ratio, stress tests |

### **Batch Jobs** (1 file) - `backend/src/main/java/com/vanguard/portal/batch/`

| File | Purpose | Anti-Patterns |
|------|---------|---------------|
| `BalanceAggregationJob.java` | Nightly balance rollup | ⚠️ Long-running (6-8 hours), single-threaded |

### **Utilities** (1 file) - `backend/src/main/java/com/vanguard/portal/util/`

| File | Purpose |
|------|---------|
| `SessionUtil.java` | Session management helper |

---

## ⚙️ Configuration Files (5 Files)

| File | Purpose | Anti-Patterns |
|------|---------|---------------|
| `backend/pom.xml` | Maven dependencies | 🔴 Log4j 1.2.17 (CVE-2019-17571) |
| `backend/src/main/resources/schema.sql` | Database schema | ⚠️ Business logic in stored procedures |
| `backend/src/main/resources/log4j.properties` | Logging configuration | - |
| `backend/src/main/webapp/WEB-INF/web.xml` | Servlet mappings | - |
| `backend/src/main/webapp/WEB-INF/applicationContext.xml` | Spring beans | - |

---

## 📚 Documentation Files (14 Files)

### **Top-Level Guides**

| File | Purpose | When to Read |
|------|---------|--------------|
| **START_HERE.md** | Quick orientation | ⭐ Read FIRST |
| **REPOSITORY_README.md** | GitHub README | For GitHub repo |
| **BUILD_SUMMARY.md** | Build completion summary | To verify everything is built |
| **INDEX.md** | This file - complete index | To find specific files |
| **README.md** | Original README | Legacy reference |


### **Backend Documentation**

| File | Location | Purpose |
|------|----------|---------|
| **BACKEND_README.md** | `backend/` | Backend architecture overview |
| **ANTI_PATTERNS_QUICK_REF.md** | `backend/` | Quick anti-pattern reference |
| **SLINGSHOT_GUIDE.md** | `backend/` | Slingshot expectations |
| **PROJECT_STATS.txt** | `backend/` | Code statistics |

---

## 🗂️ Complete Directory Tree

```
legacy-vanguard-portal/
│
├── 📄 START_HERE.md                    ⭐ READ FIRST
├── 📄 REPOSITORY_README.md             GitHub README
├── 📄 BUILD_SUMMARY.md                 Build verification
├── 📄 INDEX.md                         This file
├── 📄 README.md                        Original README
├── 📄 SLINGSHOT_UPLOAD_GUIDE.md       Slingshot guide
├── 📄 MODERNIZATION_CASE.md           Business case ($3.6M ROI)
├── 📄 MASTER_README.md                Complete overview
├── 📄 QUICKSTART.md                   Quick start
├── 📄 FEATURES.md                     Feature details
│
├── 📁 frontend/                        Frontend HTML pages
│   ├── 🌐 accounts-overview.html      Dashboard
│   ├── 🌐 balances-holdings.html      Holdings
│   ├── 🌐 portfolio-performance.html  Performance
│   ├── 🌐 transactions.html           Transactions
│   └── 🌐 risk-analysis.html          Risk analysis
│
└── 📁 backend/                         Java backend
    ├── 📄 pom.xml                      Maven config
    ├── 📄 BACKEND_README.md           Backend docs
    ├── 📄 ANTI_PATTERNS_QUICK_REF.md  Anti-pattern ref
    ├── 📄 SLINGSHOT_GUIDE.md          Slingshot info
    ├── 📄 PROJECT_STATS.txt           Code stats
    │
    ├── 📁 src/main/java/com/vanguard/portal/
    │   ├── 📁 servlet/                 (2 servlets)
    │   │   ├── ☕ AccountsOverviewServlet.java
    │   │   └── ☕ BalancesHoldingsServlet.java
    │   │
    │   ├── 📁 service/                 (8 services)
    │   │   ├── ☕ BalanceService.java
    │   │   ├── ☕ BalanceServiceImpl.java
    │   │   ├── ☕ HoldingsService.java
    │   │   ├── ☕ HoldingsServiceImpl.java
    │   │   ├── ☕ MarketDataService.java
    │   │   ├── ☕ MarketDataServiceImpl.java
    │   │   ├── ☕ RiskAnalyticsService.java
    │   │   └── ☕ RiskAnalyticsServiceImpl.java
    │   │
    │   ├── 📁 dao/                     (7 DAOs)
    │   │   ├── ☕ AccountDAO.java
    │   │   ├── ☕ AccountDAOImpl.java
    │   │   ├── ☕ BalanceDAO.java
    │   │   ├── ☕ BalanceDAOImpl.java
    │   │   ├── ☕ HoldingsDAO.java
    │   │   ├── ☕ HoldingsDAOImpl.java
    │   │   └── ☕ MarketDataDAO.java
    │   │
    │   ├── 📁 model/                   (6 models)
    │   │   ├── ☕ Account.java
    │   │   ├── ☕ Balance.java
    │   │   ├── ☕ Holding.java
    │   │   ├── ☕ Transaction.java
    │   │   ├── ☕ Portfolio.java
    │   │   └── ☕ RiskMetrics.java
    │   │
    │   ├── 📁 batch/                   (1 job)
    │   │   └── ☕ BalanceAggregationJob.java
    │   │
    │   └── 📁 util/                    (1 util)
    │       └── ☕ SessionUtil.java
    │
    ├── 📁 src/main/resources/
    │   ├── 🗄️ schema.sql              Database schema
    │   └── ⚙️ log4j.properties         Logging config
    │
    └── 📁 src/main/webapp/WEB-INF/
        ├── ⚙️ web.xml                  Servlet mappings
        └── ⚙️ applicationContext.xml   Spring config
```
---

## 📊 File Statistics

| Category | Count |
|----------|-------|
| **HTML Pages** | 5 |
| **Java Files** | 28 |
| **Config Files** | 5 |
| **Documentation** | 14 |
| **Total Files** | ~52 |
| **Total LOC** | ~4,800 |

---

## 🔍 Finding Specific Things

### **Anti-Patterns**
- N+1 Query: `backend/src/main/java/com/vanguard/portal/service/BalanceServiceImpl.java:67-86`
- SQL Injection: `backend/src/main/java/com/vanguard/portal/dao/HoldingsDAOImpl.java:145-160`
- Hardcoded Credentials: `backend/src/main/java/com/vanguard/portal/service/RiskAnalyticsServiceImpl.java:45-47`
- Log4j CVE: `backend/pom.xml`
- All anti-patterns: `backend/ANTI_PATTERNS_QUICK_REF.md`

### **Business Value**
- ROI Calculation: `MODERNIZATION_CASE.md` (search for "$3.6M")
- Cost-Benefit Analysis: `MODERNIZATION_CASE.md` (Section: Financial Analysis)
- Risk Mitigation: `MODERNIZATION_CASE.md` (Section: Risk Assessment)

### **Technical Details**
- Architecture: `REPOSITORY_README.md` (Section: System Architecture)
- Technology Stack: `REPOSITORY_README.md` (Section: Technology Stack)
- Features: `FEATURES.md` or `REPOSITORY_README.md` (Section: Features)x

