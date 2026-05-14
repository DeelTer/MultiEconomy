package ru.deelter.multieconomy.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;

@Getter
public class ConnectionPool {

	private final HikariDataSource dataSource;
	private final DatabaseType type;

	public ConnectionPool(DatabaseConfig cfg, @NonNull DatabaseType type) {
		this.type = type;
		HikariConfig config = new HikariConfig();
		String jdbcUrl = switch (type) {
			case MYSQL -> "jdbc:mysql://" + cfg.host() + ":" + cfg.port() + "/" + cfg.name() +
					"?useSSL=false&characterEncoding=utf8&allowPublicKeyRetrieval=true";
			case SQLITE -> "jdbc:sqlite:" + MultiEconomy.getInstance().getDataFolder() + "/economy.db";
			case H2 ->
					"jdbc:h2:file:" + MultiEconomy.getInstance().getDataFolder() + "/economy;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false";
		};
		config.setJdbcUrl(jdbcUrl);
		if (type == DatabaseType.MYSQL) {
			config.setUsername(cfg.user());
			config.setPassword(cfg.password());
			config.setMaximumPoolSize(cfg.maxPoolSize());
			config.setMinimumIdle(cfg.minIdle());
			config.setMaxLifetime(cfg.maxLifetime());
			config.setKeepaliveTime(cfg.keepaliveTime());
			config.setConnectionTimeout(cfg.connectionTimeout());
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		} else {
			// Для SQLite/H2 - простой пул
			config.setMaximumPoolSize(4);
			config.setMinimumIdle(1);
			config.setConnectionTimeout(5000);
		}
		config.setPoolName("economy-pool");
		this.dataSource = new HikariDataSource(config);
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public void close() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}
}