package dev.otpcodesapp.dao.impl.jdbc;

import dev.otpcodesapp.config.ConnectionProvider;
import dev.otpcodesapp.dao.Dao;
import dev.otpcodesapp.model.OtpConfig;
import dev.otpcodesapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OtpConfigDaoImpl implements Dao<OtpConfig, Long> {

    private static final String SELECT_BY_ID = """
            SELECT id, code_length, ttl_seconds
            FROM otp_config
            WHERE id = ?
            """;

    private static final String UPDATE = """
            UPDATE otp_config
            SET code_length = ?, ttl_seconds = ?
            WHERE id = ?
            """;

    @Override
    public OtpConfig create(OtpConfig object) throws SQLException {
        return null;
    }

    @Override
    public Optional<OtpConfig> findById(Long id) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(SELECT_BY_ID);
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<OtpConfig> findAll() throws SQLException {
        return List.of();
    }

    @Override
    public boolean update(OtpConfig object) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(UPDATE);
            ps.setInt(1, object.codeLength());
            ps.setInt(2, object.ttlSeconds());
            ps.setLong(3, object.id());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(Long aLong) throws SQLException {
        return false;
    }

    @Override
    public boolean existsById(Long aLong) throws SQLException {
        return false;
    }

    private OtpConfig mapRow(ResultSet rs) throws SQLException {
        return new OtpConfig(
                rs.getLong("id"),
                rs.getInt("code_length"),
                rs.getInt("ttl_seconds")
        );
    }
}
