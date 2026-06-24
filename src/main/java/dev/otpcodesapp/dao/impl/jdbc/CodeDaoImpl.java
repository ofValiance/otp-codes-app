package dev.otpcodesapp.dao.impl.jdbc;

import dev.otpcodesapp.config.ConnectionProvider;
import dev.otpcodesapp.dao.CodeDao;
import dev.otpcodesapp.model.Code;
import dev.otpcodesapp.model.Code.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CodeDaoImpl implements CodeDao {

    private static final String INSERT = """
            INSERT INTO codes (user_id, operation_id, code, expires_at)
            VALUES (?, ?, ?, ?)
            RETURNING id, user_id, operation_id, code, status, created_at, expires_at, used_at
            """;

    private static final String SELECT_BY_ID = """
            SELECT id, user_id, operation_id, code, status, created_at, expires_at, used_at
            FROM codes
            WHERE id = ?
            """;

    private static final String SELECT_ALL = """
            SELECT id, user_id, operation_id, code, status, created_at, expires_at, used_at
            FROM codes
            """;

    private static final String SELECT_ACTIVE_BY_USER_AND_OPERATION = """
            SELECT id, user_id, operation_id, code, status, created_at, expires_at, used_at
            FROM codes
            WHERE user_id = ?
              AND operation_id = ?
              AND status = 'ACTIVE'
            """;

    private static final String UPDATE = """
            UPDATE codes
            SET user_id = ?, operation_id = ?, code = ?, status = ?, expires_at = ?, used_at = ?
            WHERE id = ?
            """;

    private static final String DELETE_BY_ID = """
            DELETE FROM codes
            WHERE id = ?
            """;

    private static final String EXISTS_BY_ID = """
            SELECT 1
            FROM codes
            WHERE id = ?
            """;

    private static final String MARK_USED = """
            UPDATE codes
            SET status = 'USED', used_at = NOW()
            WHERE id = ?
            """;

    private static final String MARK_EXPIRED = """
            UPDATE codes
            SET status = 'EXPIRED'
            WHERE status = 'ACTIVE' AND expires_at < NOW()
            """;

    @Override
    public Code create(Code object) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(INSERT);
            ps.setLong(1, object.userId());
            ps.setLong(2, object.operationId());
            ps.setInt(3, object.code());
            ps.setTimestamp(4, Timestamp.from(object.expiresAt()));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<Code> findById(Long id) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(SELECT_BY_ID);
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Code> findActiveByUserAndOperation(long userId, long operationId) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(SELECT_ACTIVE_BY_USER_AND_OPERATION);
            ps.setLong(1, userId);
            ps.setLong(2, operationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Code> findAll() throws SQLException {
        return queryList(SELECT_ALL);
    }

    @Override
    public boolean update(Code object) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(UPDATE);
            ps.setLong(1, object.userId());
            ps.setLong(2, object.operationId());
            ps.setInt(3, object.code());
            ps.setString(4, object.status().name());
            ps.setTimestamp(5, Timestamp.from(object.expiresAt()));
            ps.setTimestamp(6, object.usedAt() != null ? Timestamp.from(object.usedAt()) : null);
            ps.setLong(7, object.id());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(DELETE_BY_ID);
            ps.setLong(1, id);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existsById(Long id) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(EXISTS_BY_ID);
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
            return false;
        }
    }

    @Override
    public void markUsed(long id) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(MARK_USED);
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
        }
    }

    @Override
    public void markExpiredBatch() throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(MARK_EXPIRED);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
        }
    }

    private List<Code> queryList(String sql) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(sql);
            List<Code> codes = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    codes.add(mapRow(rs));
                }
                return codes;
            }
        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
            return null;
        }
    }

    private Code mapRow(ResultSet rs) throws SQLException {
        Timestamp usedAt = rs.getTimestamp("used_at");
        return new Code(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("operation_id"),
                rs.getInt("code"),
                Status.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("expires_at").toInstant(),
                usedAt != null ? usedAt.toInstant() : null
        );
    }
}
