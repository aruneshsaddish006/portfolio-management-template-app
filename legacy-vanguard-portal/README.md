# Legacy Vanguard Asset Management Portal

A lightweight, functional legacy asset management portal built with self-contained HTML pages. No build process, no npm, no servers required - just open the HTML files in your browser.

## Overview

This portal demonstrates a realistic 100-year-old financial institution's legacy application that manages client assets. Built with vanilla JavaScript, inline CSS, and Chart.js for visualizations.

**Total Portfolio Value:** $68,917.42 (Jacob Michelini's accounts)

## Pages

### 1. accounts-overview.html
**Main dashboard showing:**
- Total Account Value: $68,917.42
- YTD Performance: +4.31% (+$2,847.19)
- Interactive performance chart (Chart.js line chart)
- Asset Mix donut chart (72.14% Stocks, 18.66% Bonds, 9.20% Short-Term)
- Account Summary table (4 accounts) - sortable
- Recent Activity table (10 transactions) - sortable

**Backend comments show:**
- `AccountsOverviewServlet.java`
- `BalanceServiceImpl.calculateTotalBalance()`
- `AssetAllocationServiceImpl.getAssetMix()`

### 2. balances-holdings.html
**Detailed holdings view with:**
- Summary cards (Total Value, Holdings Count, Total Gain/Loss, Day Change)
- Account filter dropdown (filter by specific account)
- Date range picker (as of date selector)
- Download Report button (exports to CSV)
- Holdings table with 13 positions - fully sortable
- Real-time filtering by account
- Total row showing portfolio aggregates

**Backend comments show:**
- `HoldingsServlet.java`
- `HoldingsServiceImpl.getAllHoldings(userId, accountFilter)`
- `ReportServiceImpl.exportHoldings(userId, format="CSV")`

### 3. portfolio-performance.html
**Performance analytics with:**
- Performance summary cards (YTD, 1Y, 3Y, Since Inception)
- Interactive portfolio value chart with period selector (1M, 3M, 6M, YTD, 1Y, 3Y, All)
- Asset class performance bar chart
- Benchmark comparison table (S&P 500, Russell 2000, MSCI EAFE, Bloomberg Agg, 60/40)

**Backend comments show:**
- `PerformanceServlet.java`
- `PerformanceServiceImpl.getPerformanceMetrics(userId)`
- `BenchmarkServiceImpl.compareToBenchmarks(userId, period="YTD")`

## Features

### Working Functionality
✅ **Chart.js charts** - Performance line charts, donut charts, bar charts
✅ **Sortable tables** - Click column headers to sort (ascending/descending)
✅ **Client-side filtering** - Filter holdings by account
✅ **CSV export** - Download holdings report as CSV file
✅ **Navigation** - Links between all 3 pages work
✅ **Responsive design** - Works on desktop and tablet
✅ **Realistic data** - Jacob Michelini's 4 accounts with 13 holdings

### Visual Design
🎨 **Vanguard red color scheme** (#c00, #a00)
🎨 **Professional financial UI** - Clean, modern, trustworthy
🎨 **Striped table rows** - Even/odd row coloring for readability
🎨 **Hover effects** - Interactive feedback on buttons and tables
🎨 **Color-coded values** - Green for gains, red for losses

## Data

### Accounts (4 total = $68,917.42)
1. **Individual Brokerage** (****5626) - $28,450.88
2. **Roth IRA** (****7182) - $24,821.35
3. **Traditional 401(k)** (****3309) - $12,445.19
4. **Taxable Investment** (****9201) - $3,200.00

### Holdings (13 positions)
- **VTI** - Vanguard Total Stock Market ETF (115 shares across 3 accounts)
- **VTSAX** - Vanguard Total Stock Market Index (105 shares across 2 accounts)
- **BND** - Vanguard Total Bond Market ETF (108 shares across 2 accounts)
- **VXUS** - Vanguard Total International Stock (125 shares across 2 accounts)
- **VFIAX** - Vanguard 500 Index Fund (20 shares)
- **VBTLX** - Vanguard Total Bond Market Index (35 shares)
- **VMFXX** - Vanguard Federal Money Market (131 shares)
- **CASH** - Cash Reserves ($34.00)

### Asset Allocation
- **Stocks:** 72.14%
- **Bonds:** 18.66%
- **Short-Term Reserves:** 9.20%

## How to Use

### Option 1: Direct File Open
1. Navigate to `/Users/omajanda/Downloads/Shepard Stable/legacy-vanguard-portal/`
2. Double-click `accounts-overview.html` to open in your default browser
3. Use the navigation links to visit other pages

### Option 2: Local Web Server (Recommended)
```bash
cd "/Users/omajanda/Downloads/Shepard Stable/legacy-vanguard-portal"

# Python 3
python3 -m http.server 8080

# Then open: http://localhost:8080/accounts-overview.html
```

### Option 3: VS Code Live Server
1. Open the `legacy-vanguard-portal` folder in VS Code
2. Install "Live Server" extension
3. Right-click `accounts-overview.html` → "Open with Live Server"

## Backend Integration Points

Each HTML page includes comments showing where backend servlets would be called:

```html
<!-- Backend: AccountsOverviewServlet.java -->
<!-- Business Logic: BalanceServiceImpl.calculateTotalBalance() -->
```

**Servlet Mappings (for reference):**
- `/api/accounts/overview` → AccountsOverviewServlet.java
- `/api/accounts/all` → AccountServiceImpl.getAllAccounts(userId)
- `/api/holdings/all` → HoldingsServiceImpl.getAllHoldings(userId)
- `/api/performance/metrics` → PerformanceServiceImpl.getPerformanceMetrics(userId)
- `/api/reports/export` → ReportServiceImpl.exportHoldings(userId, format)

**Business Logic Layer:**
- `com.vanguard.service.BalanceServiceImpl`
- `com.vanguard.service.AccountServiceImpl`
- `com.vanguard.service.HoldingsServiceImpl`
- `com.vanguard.service.AssetAllocationServiceImpl`
- `com.vanguard.service.PerformanceServiceImpl`
- `com.vanguard.service.BenchmarkServiceImpl`
- `com.vanguard.service.ReportServiceImpl`

## Technology Stack

- **HTML5** - Semantic markup
- **CSS3** - Inline styles (no external CSS files)
- **Vanilla JavaScript** - No frameworks, no dependencies
- **Chart.js 4.4.1** - Charts via CDN (only external dependency)

## Browser Compatibility

✅ Chrome 90+
✅ Firefox 88+
✅ Safari 14+
✅ Edge 90+

## File Structure

```
legacy-vanguard-portal/
├── accounts-overview.html      (Main dashboard)
├── balances-holdings.html      (Holdings detail)
├── portfolio-performance.html  (Performance charts)
└── README.md                   (This file)
```

## Key Features Demonstrated

### 1. Chart.js Integration
- **Line Chart:** Portfolio value over time with period selector
- **Donut Chart:** Asset allocation with percentages
- **Bar Chart:** Asset class performance comparison

### 2. Table Sorting
Click any column header with ⇅ icon to sort:
- String columns: Alphabetical A-Z / Z-A
- Numeric columns: Ascending / Descending
- Visual feedback: ↑ (ascending) ↓ (descending)

### 3. Client-Side Filtering
- Account filter dropdown updates holdings table instantly
- Date picker (placeholder for backend integration)
- Maintains sort order after filtering

### 4. CSV Export
- Click "Download Report" button
- Generates CSV file with current filtered holdings
- Filename: `vanguard-holdings-YYYY-MM-DD.csv`

### 5. Responsive Tables
- Striped rows (even/odd coloring)
- Hover effects for better UX
- Totals row with bold styling
- Color-coded gains/losses

## For Modernization Project

**Features to Modernize:**
1. ✨ Asset Mix Chart → Modern donut chart with red gradients
2. ✨ Performance Chart → Interactive time-series with animations
3. ✨ Holdings Table → Filterable, sortable with export functionality
4. ✨ Navigation → Modern SPA routing
5. ✨ Responsive Design → Mobile-first approach

**Backend Services to Replace:**
- Legacy Servlets → REST APIs or GraphQL
- Session management → JWT or OAuth 2.0
- SQL queries → ORM (JPA/Hibernate) or microservices

## Demo Scenario

**User:** Jacob Michelini (jacob.michelini@vanguard.com)
**Role:** Individual investor
**Portfolio:** $68,917.42 across 4 accounts

**User Journey:**
1. Login → See Accounts Overview ($68,917.42 total)
2. Click "Balances & Holdings" → See 13 holdings across 4 accounts
3. Filter by "Roth IRA" → See only holdings in that account
4. Click "Download Report" → Export filtered holdings to CSV
5. Click "Portfolio Performance" → See YTD performance charts
6. Select "1 Year" period → See 12-month performance history
7. Review benchmark comparison → Portfolio outperforming S&P 500

## Notes

- **No server required** - Pure client-side HTML/JS/CSS
- **No build process** - No npm install, no webpack, no babel
- **Chart.js CDN** - Only external dependency (loads from CDN)
- **Realistic data** - Based on actual Vanguard fund tickers and prices
- **Backend comments** - Show where servlet calls would happen
- **Total balance matches** - $68,917.42 as specified in requirements

## License

Demonstration code for legacy application modernization projects.

---
**Purpose:** Demonstrate realistic legacy financial portal for modernization analysis
