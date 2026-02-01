package com.vanguard.portal.dao;

import com.vanguard.portal.model.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("balanceDAO")
public class BalanceDAOImpl implements BalanceDAO {

    @Autowired
    private DataSource dataSource;

    @Override
    public Balance findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM balances WHERE account_number = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToBalance(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching balance", e);
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> getRecentTransactions(String accountNumber, int days) {
        List<Map<String, Object>> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? " +
                     "AND transaction_date >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                     "ORDER BY transaction_date DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            stmt.setInt(2, days);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("transactionId", rs.getLong("transaction_id"));
                transaction.put("amount", rs.getBigDecimal("amount"));
                transaction.put("status", rs.getString("status"));
                transaction.put("transactionDate", rs.getDate("transaction_date"));
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transactions", e);
        }

        return transactions;
    }

    @Override
    public void update(Balance balance) {
        String sql = "UPDATE balances SET current_balance = ?, available_balance = ?, " +
                     "pending_balance = ?, last_updated = ? WHERE account_number = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, balance.getCurrentBalance());
            stmt.setBigDecimal(2, balance.getAvailableBalance());
            stmt.setBigDecimal(3, balance.getPendingBalance());
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setString(5, balance.getAccountNumber());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating balance", e);
        }
    }

    @Override
    public void save(Balance balance) {
        String sql = "INSERT INTO balances (account_number, current_balance, available_balance, pending_balance) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, balance.getAccountNumber());
            stmt.setBigDecimal(2, balance.getCurrentBalance());
            stmt.setBigDecimal(3, balance.getAvailableBalance());
            stmt.setBigDecimal(4, balance.getPendingBalance());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving balance", e);
        }
    }

    private Balance mapResultSetToBalance(ResultSet rs) throws SQLException {
        Balance balance = new Balance();
        balance.setBalanceId(rs.getLong("balance_id"));
        balance.setAccountNumber(rs.getString("account_number"));
        balance.setCurrentBalance(rs.getBigDecimal("current_balance"));
        balance.setAvailableBalance(rs.getBigDecimal("available_balance"));
        balance.setPendingBalance(rs.getBigDecimal("pending_balance"));
        balance.setLastUpdated(rs.getTimestamp("last_updated"));
        return balance;
    }
}
