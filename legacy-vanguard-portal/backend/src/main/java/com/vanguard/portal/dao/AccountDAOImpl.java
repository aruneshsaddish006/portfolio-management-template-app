package com.vanguard.portal.dao;

import com.vanguard.portal.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository("accountDAO")
public class AccountDAOImpl implements AccountDAO {

    @Autowired
    private DataSource dataSource;

    @Override
    public List<Account> findByCustomerId(String customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? AND status = 'ACTIVE' ORDER BY account_number";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching accounts", e);
        }

        return accounts;
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching account", e);
        }

        return null;
    }

    @Override
    public void save(Account account) {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, account_name, status, open_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getAccountNumber());
            stmt.setString(2, account.getCustomerId());
            stmt.setString(3, account.getAccountType());
            stmt.setString(4, account.getAccountName());
            stmt.setString(5, account.getStatus());
            stmt.setDate(6, new java.sql.Date(account.getOpenDate().getTime()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving account", e);
        }
    }

    @Override
    public void update(Account account) {
        String sql = "UPDATE accounts SET account_name = ?, status = ?, last_updated = ? WHERE account_number = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getAccountName());
            stmt.setString(2, account.getStatus());
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.setString(4, account.getAccountNumber());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating account", e);
        }
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountId(rs.getLong("account_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setCustomerId(rs.getString("customer_id"));
        account.setAccountType(rs.getString("account_type"));
        account.setAccountName(rs.getString("account_name"));
        account.setStatus(rs.getString("status"));
        account.setOpenDate(rs.getDate("open_date"));
        account.setLastUpdated(rs.getTimestamp("last_updated"));
        return account;
    }
}
