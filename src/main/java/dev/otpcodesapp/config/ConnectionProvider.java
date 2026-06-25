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
        config.setJdbcUrl("jdbc:postgresql://" + EnvManager.getString("DB_HOST") + ":" + EnvManager.getInt("DB_PORT") + "/" + EnvManager.getString("DB_NAME"));
        config.setUsername(EnvManager.getString("DB_USER"));
        config.setPassword(EnvManager.getString("DB_PASSWORD"));
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
