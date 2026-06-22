package dev.otpcodesapp.config;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;


public enum ConnectionProvider {
    INSTANCE;

    private final HikariDataSource dataSource;

    ConnectionProvider() {

        HikariConfig config = new HikariConfig();
        Dotenv dotenv = Dotenv.load();
        config.setJdbcUrl(dotenv.get("DB_URL"));
        config.setUsername(dotenv.get("DB_USERNAME"));
        config.setPassword(dotenv.get("DB_PASSWORD"));
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
