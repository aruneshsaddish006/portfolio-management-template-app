# Slingshot Analysis Guide

This guide explains how to upload and analyze this legacy backend codebase using Anthropic's Slingshot modernization tool.

## What is Slingshot?

Slingshot is Anthropic's tool for analyzing legacy codebases and generating modernization strategies. It can:

- Identify anti-patterns and technical debt
- Analyze security vulnerabilities
- Recommend modernization paths
- Generate refactoring suggestions
- Create migration roadmaps

## Files to Upload

### Critical Files (Must Upload)

These files contain the main anti-patterns that Slingshot will analyze:

1. **BalanceServiceImpl.java** - N+1 query problem
2. **HoldingsDAOImpl.java** - SQL injection vulnerability
3. **AccountsOverviewServlet.java** - Main servlet showing usage patterns
4. **BalancesHoldingsServlet.java** - Synchronous blocking CSV export
5. **MarketDataServiceImpl.java** - Legacy SOAP client
6. **BalanceAggregationJob.java** - Long-running batch job
7. **schema.sql** - Database schema with stored procedure

### Supporting Files (Recommended)

8. **pom.xml** - Shows legacy dependencies
9. **web.xml** - Servlet configuration
10. **applicationContext.xml** - Spring configuration

### Context Files (Optional but Helpful)

11. Domain models (Account.java, Balance.java, Holding.java, Transaction.java)
12. DAO and Service interfaces
13. This README and SLINGSHOT_GUIDE.md

## How to Upload to Slingshot

### Option 1: Upload Individual Files

1. Open Slingshot (chat interface or web UI)
2. Use the file upload feature
3. Upload files in this order:
   - First: Critical files (1-7 above)
   - Second: Supporting files (8-10)
   - Third: Context files if needed

### Option 2: Upload as ZIP

```bash
cd /Users/omajanda/Downloads/Shepard\ Stable/legacy-vanguard-portal/backend
zip -r vanguard-backend.zip \
  src/main/java/com/vanguard/portal/service/BalanceServiceImpl.java \
  src/main/java/com/vanguard/portal/dao/HoldingsDAOImpl.java \
  src/main/java/com/vanguard/portal/servlet/*.java \
  src/main/java/com/vanguard/portal/service/MarketDataServiceImpl.java \
  src/main/java/com/vanguard/portal/batch/BalanceAggregationJob.java \
  src/main/resources/schema.sql \
  pom.xml \
  src/main/webapp/WEB-INF/*.xml \
  BACKEND_README.md \
  SLINGSHOT_GUIDE.md
```

Then upload `vanguard-backend.zip` to Slingshot.

### Option 3: Copy Directory Path

If using Slingshot's local file system access:
```
/Users/omajanda/Downloads/Shepard Stable/legacy-vanguard-portal/backend
```

## What to Ask Slingshot

### Initial Analysis Prompts

Start with these prompts to get comprehensive analysis:

1. **"Analyze this legacy Java backend and identify the top 5 technical debt issues."**
   - Slingshot will scan all files and identify critical problems

2. **"What security vulnerabilities exist in this codebase?"**
   - Should identify SQL injection in HoldingsDAOImpl
   - Should flag Log4j 1.2.17 vulnerability
   - May identify hardcoded credentials

3. **"What performance bottlenecks do you see?"**
   - Should identify N+1 query problem
   - Should flag synchronous CSV export
   - Should identify batch job issues

4. **"How would you modernize this application to Spring Boot 3.x?"**
   - Will generate migration strategy
   - Suggests dependency updates
   - Provides refactoring roadmap

### Specific Issue Analysis

For deeper analysis of specific anti-patterns:

5. **"Analyze the N+1 query problem in BalanceServiceImpl. How would you fix it?"**
   - Expected: Suggestion to use batch queries or JOINs
   - Expected: Example code using Spring Data JPA

6. **"Fix the SQL injection vulnerability in HoldingsDAOImpl."**
   - Expected: Refactored code using PreparedStatement
   - Expected: Input validation recommendations

7. **"How can I make the CSV export in BalancesHoldingsServlet non-blocking?"**
   - Expected: Async processing with message queue
   - Expected: Streaming response suggestions
   - Expected: WebSocket or polling recommendations

8. **"Suggest alternatives to the SOAP client in MarketDataServiceImpl."**
   - Expected: REST API migration plan
   - Expected: HTTP client examples (RestTemplate, WebClient)
   - Expected: Performance comparison

9. **"How can I optimize the BalanceAggregationJob batch process?"**
   - Expected: Spring Batch framework recommendation
   - Expected: Chunked processing strategy
   - Expected: Parallelization suggestions

10. **"How do I move the business logic from the stored procedure to Java?"**
    - Expected: Application-layer refactoring
    - Expected: Transaction management suggestions
    - Expected: Testing strategy

### Modernization Planning

11. **"Create a prioritized modernization roadmap for this codebase."**
    - Expected: Phase 1 (critical security fixes)
    - Expected: Phase 2 (performance optimizations)
    - Expected: Phase 3 (framework upgrades)

12. **"What are the risks of migrating this to Spring Boot 3.x?"**
    - Expected: Breaking changes analysis
    - Expected: Migration complexity assessment
    - Expected: Testing requirements

13. **"Estimate the effort required to modernize this application."**
    - Expected: LOC analysis
    - Expected: Complexity metrics
    - Expected: Time and resource estimates

## Expected Slingshot Findings

Based on the anti-patterns in this codebase, Slingshot should identify:

### Security Issues
- ✅ SQL injection in `HoldingsDAOImpl.searchHoldings()`
- ✅ Outdated Log4j version with CVEs
- ✅ Hardcoded credentials in MarketDataServiceImpl

### Performance Issues
- ✅ N+1 query problem in `BalanceServiceImpl.calculateAccountBalance()`
- ✅ Synchronous blocking in `BalancesHoldingsServlet.exportHoldingsToCSV()`
- ✅ Long-running batch job without parallelization

### Technical Debt
- ✅ Legacy SOAP instead of REST
- ✅ Business logic in database stored procedure
- ✅ Spring 4.x (3 versions behind)
- ✅ No caching layer
- ✅ Limited error handling

### Code Quality
- ✅ No unit tests
- ✅ Tight coupling to legacy APIs
- ✅ Large methods (200+ lines in some places)
- ✅ Hardcoded configuration values

## Sample Slingshot Session

Here's an example interaction with Slingshot:

```
You: Analyze this legacy Java backend for modernization opportunities.

Slingshot: I've analyzed your legacy Vanguard Portal backend. Here are the top findings:

1. CRITICAL - SQL Injection Vulnerability
   Location: HoldingsDAOImpl.java, line 145
   Risk: High - Could expose customer financial data
   Fix: Use PreparedStatement with parameter binding

2. HIGH - N+1 Query Performance Issue
   Location: BalanceServiceImpl.java, lines 67-86
   Impact: 7x slower than optimal (21 queries vs 3)
   Fix: Implement batch fetching or use JPA with @EntityGraph

3. HIGH - Synchronous Blocking Export
   Location: BalancesHoldingsServlet.java, exportHoldingsToCSV()
   Impact: Thread exhaustion, 503 errors under load
   Fix: Implement async export with RabbitMQ or AWS SQS

4. MEDIUM - Legacy SOAP Integration
   Location: MarketDataServiceImpl.java
   Impact: 10x slower than REST (800ms vs 80ms)
   Fix: Migrate to modern REST API with JSON

5. MEDIUM - Long-Running Batch Job
   Location: BalanceAggregationJob.java
   Impact: 6-8 hour runtime, no restart capability
   Fix: Implement Spring Batch with chunk processing

Would you like detailed refactoring suggestions for any of these issues?
```

## Tips for Best Results

1. **Upload critical files first** - Slingshot can analyze incrementally
2. **Provide context** - Mention it's a financial services app from 2013-2018
3. **Ask specific questions** - Target individual anti-patterns for deep analysis
4. **Request code examples** - Ask for refactored code snippets
5. **Iterate** - Start broad, then drill into specific issues

## Next Steps After Analysis

Once Slingshot provides recommendations:

1. **Prioritize fixes** by risk (security > performance > maintainability)
2. **Create a backlog** of modernization tasks
3. **Estimate effort** for each modernization phase
4. **Plan incremental migration** - Don't try to fix everything at once
5. **Start with security** - Fix SQL injection and update Log4j first

## Additional Resources

- Spring Boot Migration Guide: https://spring.io/guides/gs/spring-boot/
- OWASP SQL Injection Prevention: https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html
- Spring Batch Documentation: https://spring.io/projects/spring-batch

---

**Note:** This is a demonstration codebase. All anti-patterns are intentional and documented for modernization training purposes.
