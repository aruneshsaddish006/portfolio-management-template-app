# Contributing to Legacy Portfolio System

Thank you for your interest in contributing to the Legacy Portfolio System! This document provides guidelines for contributing to this educational project.

---

## 🎯 Project Goals

This project aims to:

1. **Educate** software engineers about legacy code patterns and technical debt
2. **Demonstrate** real-world financial software architecture
3. **Provide** a safe environment to practice refactoring and modernization
4. **Showcase** common anti-patterns found in 20+ year old enterprise applications

---

## 🤝 Ways to Contribute

### **1. Add More Legacy Patterns**

We welcome contributions that add realistic legacy patterns:

- Database-related anti-patterns
- Concurrency issues
- Memory leaks
- Configuration management problems
- Logging anti-patterns
- Error handling issues

**Requirements:**
- Pattern must be based on real-world legacy systems
- Must include detailed comments explaining the problem
- Must document the impact and modernization approach
- Update `docs/ANTI_PATTERNS.md` with the new pattern

### **2. Improve Documentation**

Help make the project more accessible:

- Clarify existing documentation
- Add diagrams and visual aids
- Create tutorials and guides
- Improve code comments
- Add examples

### **3. Create Refactoring Examples**

Show how to modernize specific anti-patterns:

- Create a separate branch demonstrating the fix
- Document the before/after comparison
- Explain the refactoring steps
- Add tests to verify behavior preservation

### **4. Add Test Cases**

Legacy code often lacks tests:

- Add unit tests for service layer
- Create integration tests for DAOs
- Add end-to-end tests for servlets
- Document test-driven refactoring approaches

### **5. Report Issues**

Help improve the project by reporting:

- Documentation gaps or errors
- Broken links or formatting issues
- Suggestions for new patterns
- Ideas for improvements

---

## 📋 Getting Started

### **Step 1: Fork the Repository**

```bash
# Click "Fork" on GitHub, then clone your fork
git clone https://github.com/YOUR-USERNAME/legacy-portfolio-system.git
cd legacy-portfolio-system
```

### **Step 2: Create a Branch**

```bash
# Create a descriptive branch name
git checkout -b feature/add-caching-antipattern
```

### **Step 3: Make Your Changes**

- Write clear, well-commented code
- Follow existing code style
- Update documentation as needed
- Test your changes

### **Step 4: Commit Your Changes**

```bash
# Add your changes
git add .

# Commit with a descriptive message
git commit -m "Add caching anti-pattern example in service layer"
```

### **Step 5: Push to GitHub**

```bash
git push origin feature/add-caching-antipattern
```

### **Step 6: Create a Pull Request**

1. Go to your fork on GitHub
2. Click "New Pull Request"
3. Select your branch
4. Fill out the PR template
5. Submit for review

---

## 📝 Code Style Guidelines

### **Java Code**

```java
// ✅ Good: Clear comments explaining the anti-pattern
/**
 * Retrieves account balances.
 *
 * <p><strong>ANTI-PATTERN: N+1 Query Problem</strong></p>
 * This method executes a separate query for each account instead of
 * using batch loading. For 100 accounts, this results in 101 database
 * queries instead of 2.
 *
 * <p>Impact: Response time degrades linearly with account count.</p>
 */
public List<Balance> getBalances(String userId) {
    // Implementation
}

// ❌ Bad: No explanation of the problem
public List<Balance> getBalances(String userId) {
    // Implementation without context
}
```

### **Documentation**

- Use clear, concise language
- Include code examples where helpful
- Add diagrams for complex concepts
- Link to related resources

### **Commits**

Good commit messages:

```
✅ Add connection pooling anti-pattern to DAO layer
✅ Update ANTI_PATTERNS.md with caching issues
✅ Fix typo in ARCHITECTURE.md
✅ Improve setup instructions for Windows users
```

Bad commit messages:

```
❌ Update files
❌ Fix stuff
❌ WIP
❌ Changes
```

---

## 🔍 Pull Request Guidelines

### **PR Title Format**

Use clear, descriptive titles:

```
✅ Add: Caching anti-pattern in service layer
✅ Fix: Broken link in SETUP.md
✅ Docs: Improve architecture diagram
✅ Refactor: Extract method in RiskAnalyticsService
```

### **PR Description Template**

```markdown
## Description
Brief description of the changes

## Type of Change
- [ ] New anti-pattern
- [ ] Documentation improvement
- [ ] Refactoring example
- [ ] Bug fix
- [ ] Other (please describe)

## Motivation
Why is this change needed? What problem does it solve?

## Changes Made
- List of specific changes
- Any new files added
- Any files modified or deleted

## Testing
How have you tested these changes?

## Documentation
- [ ] Updated relevant documentation
- [ ] Added code comments
- [ ] Updated ANTI_PATTERNS.md (if applicable)

## Screenshots (if applicable)
Add screenshots for UI changes
```

---

## ✅ Pull Request Checklist

Before submitting a PR, ensure:

- [ ] Code follows existing style and conventions
- [ ] All new code has clear comments
- [ ] Documentation is updated
- [ ] ANTI_PATTERNS.md is updated (if adding new patterns)
- [ ] No references to proprietary tools or services
- [ ] Commit messages are clear and descriptive
- [ ] PR description is complete
- [ ] Changes are focused on a single concern

---

## 🚫 What NOT to Contribute

Please avoid:

- ❌ Fixes for intentional anti-patterns (they're meant to be broken!)
- ❌ Upgrading dependencies (unless documented as a modernization example)
- ❌ Removing security vulnerabilities (they're intentional for education)
- ❌ Adding external service integrations (keep it self-contained)
- ❌ Large-scale rewrites (create separate branches for examples instead)

---

## 🎓 Educational Best Practices

When adding new patterns:

### **1. Document the "Why"**

```java
// ❌ Bad: Just showing the code
String sql = "SELECT * FROM users WHERE id = " + userId;

// ✅ Good: Explaining the educational purpose
/**
 * ANTI-PATTERN: SQL Injection Vulnerability
 *
 * This code uses string concatenation to build SQL queries, which
 * allows attackers to inject malicious SQL. For example:
 *
 *   userId = "1 OR 1=1; DROP TABLE users;--"
 *
 * Modern approach: Use PreparedStatement with parameterized queries.
 */
String sql = "SELECT * FROM users WHERE id = " + userId;
```

### **2. Include Realistic Context**

Anti-patterns should be embedded in realistic business scenarios:

```java
// ✅ Good: Realistic financial domain context
public RiskMetrics calculateValueAtRisk(Portfolio portfolio) {
    // Monte Carlo simulation with performance issues
}

// ❌ Bad: Contrived "foo/bar" examples
public void processData(String foo) {
    // Anti-pattern without context
}
```

### **3. Provide Modernization Paths**

```java
/**
 * LEGACY IMPLEMENTATION (current)
 * - Problem: N+1 queries
 * - Impact: 2000ms for 50 accounts
 *
 * MODERNIZATION OPTIONS:
 * 1. Batch loading with IN clause
 * 2. JPA with @BatchSize annotation
 * 3. Denormalized view for read performance
 *
 * See docs/ANTI_PATTERNS.md for detailed migration guide.
 */
```

---

## 🐛 Reporting Issues

### **Bug Reports**

Use the bug report template and include:

- Clear description of the issue
- Steps to reproduce
- Expected vs. actual behavior
- Environment details (Java version, OS, etc.)
- Screenshots if applicable

### **Feature Requests**

Use the feature request template and include:

- Description of the proposed feature
- Use case or motivation
- Possible implementation approach
- Any related patterns or examples

---

## 💬 Code Review Process

1. **Submission**: Create PR following guidelines above
2. **Review**: Maintainers review within 3-5 business days
3. **Feedback**: Address any requested changes
4. **Approval**: Once approved, PR will be merged
5. **Thanks**: Your contribution is appreciated! 🎉

---

## 🌟 Recognition

Contributors will be:

- Listed in release notes
- Mentioned in CHANGELOG.md
- Acknowledged in documentation
- Added to contributors list

---

## 📞 Questions?

- **General Questions**: Open a [Discussion](https://github.com/your-username/legacy-portfolio-system/discussions)
- **Bug Reports**: Open an [Issue](https://github.com/your-username/legacy-portfolio-system/issues)
- **Feature Ideas**: Start a [Discussion](https://github.com/your-username/legacy-portfolio-system/discussions)

---

## 📜 Code of Conduct

### **Our Pledge**

We are committed to providing a welcoming and inclusive environment for all contributors.

### **Our Standards**

- ✅ Be respectful and considerate
- ✅ Welcome newcomers and help them learn
- ✅ Accept constructive criticism gracefully
- ✅ Focus on what's best for the community
- ✅ Show empathy towards other contributors

### **Unacceptable Behavior**

- ❌ Harassment or discriminatory language
- ❌ Personal attacks or insults
- ❌ Trolling or inflammatory comments
- ❌ Spam or off-topic posts

---

## 🙏 Thank You!

Thank you for contributing to Legacy Portfolio System! Your efforts help make this a valuable learning resource for the software engineering community.

**Happy coding!** 🚀
