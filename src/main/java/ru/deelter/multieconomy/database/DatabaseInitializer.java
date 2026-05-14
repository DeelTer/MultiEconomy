package ru.deelter.multieconomy.database;

import ru.deelter.multieconomy.MultiEconomy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

	public static void init(ConnectionPool pool) {
		String createTable;
		switch (pool.getType()) {
			case MYSQL -> createTable = """
					CREATE TABLE IF NOT EXISTS economy_accounts (
					    holder_id VARCHAR(36) NOT NULL,
					    currency_id VARCHAR(32) NOT NULL,
					    balance DOUBLE NOT NULL DEFAULT 0,
					    PRIMARY KEY (holder_id, currency_id)
					) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""";
			case SQLITE, H2 -> createTable = """
					CREATE TABLE IF NOT EXISTS economy_accounts (
					    holder_id VARCHAR(36) NOT NULL,
					    currency_id VARCHAR(32) NOT NULL,
					    balance DOUBLE NOT NULL DEFAULT 0,
					    PRIMARY KEY (holder_id, currency_id)
					);
					""";
			default -> throw new IllegalStateException("Unexpected database type: " + pool.getType());
		}
		try (Connection conn = pool.getConnection();
		     Statement stmt = conn.createStatement()) {
			stmt.execute(createTable);
			MultiEconomy.getInstance().getLogger().info("Database table initialized (" + pool.getType() + ")");
		} catch (SQLException e) {
			MultiEconomy.getInstance().getLogger().severe("Failed to create table: " + e.getMessage());
		}
	}
}