# Feature Documentation - Legacy Vanguard Portal

## Complete Feature List

### Page 1: accounts-overview.html

#### Visual Components
1. **Vanguard Header**
   - Red navigation bar (#c00)
   - Logo with darker background (#a00)
   - Active page indicator (white bottom border)
   - 3 navigation links

2. **Total Account Value Card**
   - Large display: $68,917.42 (Vanguard red)
   - YTD change: +$2,847.19 (4.31%) in green
   - Performance line chart (Chart.js)
   - 5 data points showing YTD growth

3. **Asset Mix Card**
   - Donut chart (Chart.js)
   - Three segments:
     - Stocks: 72.14% (dark red)
     - Bonds: 18.66% (medium red)
     - Short-Term: 9.20% (light red)
   - Interactive legend with percentages

4. **Account Summary Table**
   - 4 accounts displayed
   - Columns: Account Name, Number, Type, Balance, YTD Return
   - Sortable (click headers)
   - Striped rows (even/odd coloring)
   - Hover effects

5. **Recent Activity Table**
   - 10 most recent transactions
   - Columns: Date, Description, Account, Amount
   - Color-coded amounts (green positive, red negative)
   - Sortable by date and amount

#### Functional Features
- ✅ Chart.js line chart renders on page load
- ✅ Chart.js donut chart with custom Vanguard colors
- ✅ Table sorting (click any header with ⇅ icon)
- ✅ Sort direction indicator (↑ ascending, ↓ descending)
- ✅ Responsive layout (2-column grid for cards)
- ✅ Navigation between pages

#### Backend Integration Points
```java
// Servlets
AccountsOverviewServlet.java
NavigationServlet.java

// Business Logic
BalanceServiceImpl.calculateTotalBalance()
AccountServiceImpl.getAccountSummary(userId)
AccountServiceImpl.getAllAccounts(userId)
AssetAllocationServiceImpl.getAssetMix()
TransactionServiceImpl.getRecentTransactions(userId, limit=10)
SessionManager.getCurrentUser()
```

---

### Page 2: balances-holdings.html

#### Visual Components
1. **Summary Cards (4 cards)**
   - Total Value: $68,917.42
   - Total Holdings: 13
   - Total Gain/Loss: +$8,245.67 (green)
   - Day Change: +$287.42 (green)
   - Red left border accent

2. **Filters Bar**
   - Account dropdown (5 options: All, 4 specific accounts)
   - Date picker (as of date selector)
   - Download Report button (red with download icon)

3. **Holdings Table**
   - 13 holdings across 4 accounts
   - Columns: Account, Symbol, Description, Quantity, Price, Value, Day Change, Total Gain/Loss
   - Symbol column in red (Vanguard brand)
   - Color-coded changes (green/red)
   - Totals footer row

#### Functional Features
- ✅ Account filtering (dropdown updates table instantly)
- ✅ Table sorting on all columns
- ✅ CSV export (downloads file with filtered data)
- ✅ Real-time total calculations
- ✅ Filename generation: vanguard-holdings-YYYY-MM-DD.csv
- ✅ Maintains sort order after filtering

#### Holdings Data
```javascript
13 positions across 4 accounts:
- ****5626 (Individual Brokerage): 3 holdings
- ****7182 (Roth IRA): 3 holdings
- ****3309 (Traditional 401k): 3 holdings
- ****9201 (Taxable Investment): 4 holdings

Total: $68,917.42
Day Change: +$287.42 (0.42%)
Total Gain: +$8,245.67 (13.6%)
```

#### Backend Integration Points
```java
// Servlets
HoldingsServlet.java
HoldingsFilterServlet.java

// Business Logic
HoldingsServiceImpl.getHoldingsSummary(userId)
HoldingsServiceImpl.getAllHoldings(userId, accountFilter)
ReportServiceImpl.exportHoldings(userId, format="CSV")
```

---

### Page 3: portfolio-performance.html

#### Visual Components
1. **Performance Summary Cards (4 cards)**
   - YTD: +4.31% (+$2,847.19)
   - 1 Year: +12.87% (+$7,892.45)
   - 3 Year: +8.24% (annualized)
   - Since Inception: +15.67% (Feb 2020)

2. **Portfolio Value Chart**
   - Large line chart (400px height)
   - Period selector buttons (7 periods: 1M, 3M, 6M, YTD, 1Y, 3Y, All)
   - Dynamic data loading on period change
   - Active button highlighted in red

3. **Asset Performance Bar Chart**
   - 4 asset classes
   - YTD returns: Stocks +5.84%, Bonds -0.42%, International +3.12%, Money Market +0.15%
   - Color-coded bars (shades of red)

4. **Benchmark Comparison Table**
   - 5 benchmarks vs portfolio
   - Columns: Benchmark, Return (YTD), Your Portfolio, Difference
   - Portfolio outperforming all benchmarks
   - Color-coded returns

#### Functional Features
- ✅ Period selector (7 time periods)
- ✅ Dynamic chart updates
- ✅ Chart animation on period change
- ✅ Tooltip on chart hover
- ✅ Responsive chart sizing

#### Performance Data by Period
```javascript
1M:  6 data points
3M:  7 data points
6M:  6 data points
YTD: 5 data points (default)
1Y:  12 data points
3Y:  9 data points
All: 5 data points (yearly)

Current Value: $68,917.42
Starting Value (YTD): $66,070.23
Gain: +$2,847.19 (+4.31%)
```

#### Backend Integration Points
```java
// Servlets
PerformanceServlet.java

// Business Logic
PerformanceServiceImpl.getPerformanceMetrics(userId)
PerformanceServiceImpl.calculateYTDReturn()
PerformanceServiceImpl.getPortfolioHistory(userId, period)
AssetAllocationServiceImpl.getAssetPerformance(userId)
BenchmarkServiceImpl.compareToBenchmarks(userId, period="YTD")
```

---

## Chart.js Configuration

### Line Chart (Performance)
```javascript
type: 'line'
borderColor: '#c00' (Vanguard red)
backgroundColor: 'rgba(204, 0, 0, 0.1)' (red gradient fill)
tension: 0.4 (smooth curves)
pointRadius: 3-4 (visible data points)
fill: true (area under line)
```

### Donut Chart (Asset Mix)
```javascript
type: 'doughnut'
colors: ['#c00', '#a00', '#ffb3b3'] (red gradient)
legend: 'bottom' position
labels: Include percentages
borderWidth: 2 (white borders between segments)
```

### Bar Chart (Asset Performance)
```javascript
type: 'bar'
colors: Shades of red
borderWidth: 2
y-axis: Percentage format
```

---

## Data Model

### Account Object
```javascript
{
  name: String,         // "Individual Brokerage"
  number: String,       // "****5626"
  type: String,         // "Brokerage" | "Retirement"
  balance: Number,      // 28450.88
  ytdReturn: Number     // 5.24 (percentage)
}
```

### Holding Object
```javascript
{
  account: String,       // "****5626"
  symbol: String,        // "VTI"
  description: String,   // "Vanguard Total Stock Market ETF"
  quantity: Number,      // 75
  price: Number,         // 249.20
  value: Number,         // 18690.00
  dayChangeAmt: Number,  // 112.35
  dayChangePct: Number,  // 0.6
  totalGainAmt: Number,  // 2690.00
  totalGainPct: Number   // 16.8
}
```

### Transaction Object
```javascript
{
  date: String,          // "2024-01-29"
  description: String,   // "Dividend - VTI"
  account: String,       // "****5626"
  amount: Number         // 142.50 (positive) or -1247.85 (negative)
}
```

---

## Color Scheme

### Vanguard Brand Colors
```css
Primary Red:    #c00 (rgb(204, 0, 0))
Darker Red:     #a00 (rgb(170, 0, 0))
Light Red:      #ffb3b3 (rgb(255, 179, 179))

Positive Green: #10b981 (rgb(16, 185, 129))
Negative Red:   #ef4444 (rgb(239, 68, 68))

Background:     #f5f5f5 (light gray)
Card:           #ffffff (white)
Border:         #e5e7eb (gray)
Text Primary:   #111827 (dark gray)
Text Secondary: #6b7280 (medium gray)
```

---

## Sorting Algorithm

```javascript
// String sorting (alphabetical)
return isAscending
  ? bValue.localeCompare(aValue)
  : aValue.localeCompare(bValue);

// Numeric sorting (parse and compare)
const aNum = parseFloat(aValue.replace(/[$,%+]/g, ''));
const bNum = parseFloat(bValue.replace(/[$,%+]/g, ''));
return isAscending ? bNum - aNum : aNum - bNum;
```

## Filter Algorithm

```javascript
// Account filtering
if (accountFilter === 'all') {
  filteredHoldings = [...holdings];
} else {
  filteredHoldings = holdings.filter(h =>
    h.account.includes(accountFilter)
  );
}

// Re-render table with filtered data
populateHoldingsTable(filteredHoldings);
```

---

## CSV Export Format

```csv
Account,Symbol,Description,Quantity,Price,Value,Day Change,Total Gain/Loss
****5626,VTI,Vanguard Total Stock Market ETF,75,249.20,18690.00,112.35,2690.00
****5626,VTSAX,Vanguard Total Stock Market Index,25,118.45,2961.25,14.80,461.25
...
```

---

## Browser Events

### Click Events
- Table header sorting
- Period selector buttons
- Account filter dropdown
- Download report button
- Navigation links

### Change Events
- Account filter dropdown
- Date range picker

### Hover Events
- Table rows (background color change)
- Navigation links (yellow underline)
- Sort headers (red text color)
- Chart tooltips (data point display)

---

## Performance Optimizations

1. **Chart.js CDN** - No local file, loads from CDN
2. **Inline CSS** - No external stylesheet HTTP request
3. **No dependencies** - Pure vanilla JS (except Chart.js)
4. **Efficient DOM updates** - Only re-render changed rows
5. **Event delegation** - Single listener per table

---

## Accessibility Features

- ✅ Semantic HTML (table, nav, header elements)
- ✅ Keyboard navigation (tab through interactive elements)
- ✅ Color contrast (WCAG AA compliant)
- ✅ Hover states for interactive elements
- ⚠️ Missing: ARIA labels (would add in production)
- ⚠️ Missing: Screen reader announcements (would add in production)

---

## Mobile Responsiveness

- ✅ Tables overflow with horizontal scroll
- ✅ Cards use grid layout (responsive)
- ✅ Charts resize with container
- ⚠️ Optimized for desktop (tablet/mobile would need media queries)

---

## File Sizes

- `accounts-overview.html`: ~16KB
- `balances-holdings.html`: ~17KB
- `portfolio-performance.html`: ~15KB
- `README.md`: ~9KB
- **Total: ~57KB** (excluding Chart.js CDN)

---

## Browser Console Output

When interacting with the portal, you'll see:
```javascript
// On sort
console.log('Sorting by:', columnName);

// On date filter change
console.log('Date filter changed to:', dateValue);
```

---

## Future Enhancements

### High Priority
1. Add ARIA labels for accessibility
2. Add media queries for mobile
3. Add loading states for charts
4. Add error handling for Chart.js load failure

### Medium Priority
5. Add search/filter for holdings table
6. Add multi-column sorting
7. Add chart data export
8. Add print stylesheet

### Low Priority
9. Add dark mode toggle
10. Add chart animation customization
11. Add table row selection
12. Add custom date range picker

---

**Created:** January 31, 2026
**Version:** 1.0.0
**Purpose:** Legacy application demonstration for modernization analysis
