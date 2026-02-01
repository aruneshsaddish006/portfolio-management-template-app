# Legacy Vanguard Asset Management Portal
> This repository contains a lightweight but realistic legacy asset management portal to test code modernization platforms

## 📁 Repository Structure

```
legacy-vanguard-portal/
├── 📄 MASTER_README.md              ← You are here
├── 📄 MODERNIZATION_CASE.md         ← Business justification (why modernize?)
├── 📄 QUICKSTART.md                 ← 30-second start guide
├── 📄 README.md                     ← Frontend documentation
├── 📄 FEATURES.md                   ← Technical feature breakdown
│
├── 🌐 Frontend (HTML Pages - Open in Browser)
│   ├── accounts-overview.html       ← Main dashboard ($68,917.42 portfolio)
│   ├── balances-holdings.html       ← Holdings table with filters
│   └── portfolio-performance.html   ← Performance charts
│
└── ☕ Backend (Java Code )
    ├── BACKEND_README.md            ← Backend architecture overview
    ├── SLINGSHOT_GUIDE.md           ← How to upload to Slingshot
    ├── ANTI_PATTERNS_QUICK_REF.md   ← Quick reference for anti-patterns
    ├── PROJECT_STATS.txt            ← Code statistics
    ├── pom.xml                      ← Maven config with legacy dependencies
    └── src/
        ├── main/java/com/vanguard/portal/
        │   ├── servlet/             ← 2 servlets with vulnerabilities
        │   ├── service/             ← 5 services with N+1 queries
        │   ├── dao/                 ← 6 DAOs with SQL injection
        │   ├── model/               ← 4 domain models
        │   ├── batch/               ← 1 long-running batch job
        │   └── util/                ← 1 utility class
        ├── main/resources/
        │   ├── schema.sql           ← Database schema + stored procedure
        │   └── log4j.properties     ← Legacy logging (CVE vulnerability)
        └── main/webapp/WEB-INF/
            ├── web.xml              ← Servlet mappings
            └── applicationContext.xml ← Spring configuration
```

**Total Files:** 33
- HTML Pages: 3
- Documentation: 9
- Java Source: 20
- Configuration: 5

---

## 🚀 Quick Start (3 Options)

### Option 1: View Frontend Only (No Installation)
```bash
cd legacy-vanguard-portal
open accounts-overview.html
```
✅ **Best for:** Quick demos, showing visual design, functional UI

### Option 2: Run with Local Server (Recommended)
```bash
cd legacy-vanguard-portal
python3 -m http.server 8080
# Open: http://localhost:8080/accounts-overview.html
```
✅ **Best for:** Professional demos, avoiding CORS issues

### Option 3: Analyze Backend with Slingshot
```bash
cd legacy-vanguard-portal/backend
# Upload to Slingshot: src/main/java/**/*.java
```
✅ **Best for:** Modernization analysis, risk assessment, AI-assisted refactoring

---

## 💡 What Makes This "Legacy"?

### Frontend (HTML/CSS/JavaScript)
- ❌ **No modern framework** (React, Vue, Angular)
- ❌ **Inline CSS and JavaScript** (no build pipeline)
- ❌ **jQuery 1.x patterns** (no ES6+)
- ❌ **Monolithic HTML files** (15-17KB each)
- ✅ **Works without npm/webpack**

### Backend (Java)
- ❌ **Java 8** (2014, 5 versions behind)
- ❌ **Spring 4.3.18** (2018, 3 major versions behind)
- ❌ **Log4j 1.2.17** (EOL 2015, has CVEs)
- ❌ **Servlet 3.1** (Java EE 7, 2013)
- ❌ **SOAP Web Services** (2000s technology)

### Architecture Anti-Patterns
1. **N+1 Query Problem** - `BalanceServiceImpl.java:67-86`
2. **SQL Injection** - `HoldingsDAOImpl.java:145-160`
3. **Synchronous Blocking** - `BalancesHoldingsServlet.java:133-215`
4. **Legacy SOAP** - `MarketDataServiceImpl.java`
5. **Long-Running Batch** - `BalanceAggregationJob.java`
6. **Business Logic in DB** - `schema.sql` (stored procedure)


## 📈 By the Numbers

### Portfolio Data (Realistic Vanguard Account)
- **Total Balance:** $68,917.42
- **Owner:** Jacob A. Michelini
- **Accounts:** 4 (Brokerage, Roth IRA, 401(k), Taxable)
- **Holdings:** 13 positions (VTI, VTSAX, BND, VXUS, etc.)
- **Asset Allocation:** 72.14% Stocks, 18.66% Bonds, 9.20% Short-Term
- **YTD Return:** +4.31% (+$2,847.19)

### Codebase Statistics
- **Total Lines of Code:** ~2,500 (excluding comments/blanks)
- **Java Files:** 20
- **Configuration Files:** 5
- **HTML Pages:** 3
- **Documentation Files:** 9
- **Security Vulnerabilities:** 12
- **Performance Bottlenecks:** 4
- **Anti-Patterns:** 6

### Modernization Impact (Projected)
- **Cost Savings:** $300K/year (maintenance reduction)
- **Velocity Improvement:** 12x faster (6 months → 2 weeks)
- **Security Risk Reduction:** 100% (fix all 12 vulnerabilities)
- **Performance Improvement:** 5-10x (eliminate N+1 queries, batch optimization)

---

## 🔒 Security Vulnerabilities (Intentional)

> ⚠️ **Note:** These vulnerabilities are INTENTIONALLY included for educational purposes and Slingshot analysis. Do NOT deploy this code to production.

| Severity | Issue | Location | CVSS Score |
|----------|-------|----------|------------|
| 🔴 Critical | SQL Injection | `HoldingsDAOImpl.java:145-160` | 9.8 |
| 🔴 Critical | Log4j 1.2 CVE | `pom.xml` (dependency) | 9.6 |
| 🟠 High | XSS Vulnerability | HTML pages (unescaped ${user.name}) | 7.4 |
| 🟠 High | Weak Session Mgmt | `SessionUtil.java` | 7.1 |

**Total Risk Score:** 33.9 / 40 (High Risk)

---

## 🧪 Testing Strategy (Like-for-Like Validation)

### 1. Golden Dataset Testing
```bash
# Create 100 test scenarios with known inputs/outputs
# Run against legacy system → capture baseline
# Run against modernized system → compare results
# Difference = 0? ✅ Like-for-like preserved
```

### 2. Shadow Mode Deployment
```bash
# Deploy modern API alongside legacy system
# Route 10% of traffic to both systems
# Compare responses in real-time
# Alert on discrepancies
```

### 3. Property-Based Testing
```bash
# Define invariants:
# - "total balance = sum of account balances"
# - "asset allocation percentages sum to 100%"
# Generate random inputs
# Validate properties hold in both systems
```

**Ready to explore?** Start with `QUICKSTART.md` or open `accounts-overview.html` in your browser!

---

**Repository Version:** 1.0.0
**Last Updated:** January 31, 2026
**Total Files:** 33
**Total Lines of Code:** ~2,500
**Documentation Pages:** 9
