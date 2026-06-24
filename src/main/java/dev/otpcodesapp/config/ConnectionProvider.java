package dev.otpcodesapp.config;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;

import java.sql.Connection;
import java.sql.SQLException;


public enum ConnectionProvider {
    INSTANCE;

    private final HikariDataSource dataSource;

    ConnectionProvider() {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(EnvManager.get("DB_URL"));
        config.setUsername(EnvManager.get("DB_USERNAME"));
        config.setPassword(EnvManager.get("DB_PASSWORD"));
        config.setDriverClassName("org.postgresql.Driver");

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
