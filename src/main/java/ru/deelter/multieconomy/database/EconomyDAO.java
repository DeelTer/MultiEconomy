package ru.deelter.multieconomy.database;

import org.jspecify.annotations.NonNull;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.EconomyAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class EconomyDAO {

    private final ConnectionPool pool;

    public EconomyDAO(ConnectionPool pool) {
        this.pool = pool;
    }

    public Optional<EconomyAccount> load(@NonNull UUID holderId, String currencyId) {
        String sql = "SELECT holder_id, currency_id, balance FROM economy_accounts WHERE holder_id = ? AND currency_id = ?";
        try (Connection conn = pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, holderId.toString());
            ps.setString(2, currencyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new EconomyAccount(
                        UUID.fromString(rs.getString("holder_id")),
                        rs.getString("currency_id"),
                        rs.getDouble("balance")
                ));
            }
        } catch (SQLException e) {
            MultiEconomy.getInstance().getLogger().severe("Load error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void saveOrUpdate(@NonNull UUID holderId, String currencyId, double balance) {
        String sql = "INSERT INTO economy_accounts (holder_id, currency_id, balance) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE balance = ?";
        try (Connection conn = pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, holderId.toString());
            ps.setString(2, currencyId);
            ps.setDouble(3, balance);
            ps.setDouble(4, balance);
            ps.executeUpdate();
        } catch (SQLException e) {
            MultiEconomy.getInstance().getLogger().severe("Save error: " + e.getMessage());
        }
    }

    public void batchSaveOrUpdate(@NonNull Map<UUID, Map<String, Double>> updates) {
        String sql = "INSERT INTO economy_accounts (holder_id, currency_id, balance) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE balance = ?";
        try (Connection conn = pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Map.Entry<UUID, Map<String, Double>> entry : updates.entrySet()) {
                UUID holder = entry.getKey();
                for (Map.Entry<String, Double> curEntry : entry.getValue().entrySet()) {
                    ps.setString(1, holder.toString());
                    ps.setString(2, curEntry.getKey());
                    ps.setDouble(3, curEntry.getValue());
                    ps.setDouble(4, curEntry.getValue());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            MultiEconomy.getInstance().getLogger().severe("Batch save error: " + e.getMessage());
            try (Connection conn = pool.getConnection()) {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                // ignore
            }
        }
    }

    public Map<UUID, Double> getTopBalances(String currencyId, int limit) {
        String sql = "SELECT holder_id, balance FROM economy_accounts WHERE currency_id = ? ORDER BY balance DESC LIMIT ?";
        Map<UUID, Double> top = new LinkedHashMap<>();
        try (Connection conn = pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currencyId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                top.put(UUID.fromString(rs.getString("holder_id")), rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            MultiEconomy.getInstance().getLogger().severe("Top balances error: " + e.getMessage());
        }
        return top;
    }

    public boolean exists(@NonNull UUID holderId, String currencyId) {
        String sql = "SELECT 1 FROM economy_accounts WHERE holder_id = ? AND currency_id = ? LIMIT 1";
        try (Connection conn = pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, holderId.toString());
            ps.setString(2, currencyId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            MultiEconomy.getInstance().getLogger().severe("Exists error: " + e.getMessage());
            return false;
        }
    }
}