package dev.otpcodesapp.dao.impl.jdbc;

import dev.otpcodesapp.dao.UserDao;
import dev.otpcodesapp.model.User;

import dev.otpcodesapp.config.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UserDaoImpl implements UserDao {

    private static final String INSERT = """
            INSERT INTO users (login, password_hash, role)
            VALUES (?, ?, ?)
            RETURNING id, login, password_hash, role
            """;

    private static final String SELECT_BY_ID = """
            SELECT id, login, password_hash, role
            FROM users
            WHERE id = ?
            """;

    private static final String SELECT_BY_LOGIN = """
            SELECT id, login, password_hash, role
            FROM users
            WHERE login = ?
            """;

    private static final String EXISTS_BY_ID = """
            SELECT 1
            FROM users
            WHERE id = ?
            """;

    private static final String UPDATE = """
            UPDATE users
            SET login = ?, password_hash = ?, role = ?
            WHERE id = ?
            """;

    private static final String EXISTS_ADMIN = """
            SELECT 1
            FROM users
            WHERE role = 'ADMIN'
            LIMIT 1
            """;

    private static final String SELECT_ALL = """
            SELECT id, login, password_hash, role
            FROM users
            """;

    private static final String SELECT_NON_ADMINS = """
            SELECT id, login, password_hash, role
            FROM users
            WHERE role = 'USER'
            """;

    private static final String DELETE_BY_ID = """
            DELETE FROM users
            WHERE id = ?
            """;

    @Override
    public Optional<User> findByLogin(String login) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(SELECT_BY_LOGIN);
            ps.setString(1, login);

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
    public List<User> findAllNonAdmins() throws SQLException {
        return queryList(SELECT_NON_ADMINS);
    }

    @Override
    public boolean existsAdmin() throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(EXISTS_ADMIN);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
            return false;
        }
    }

    @Override
    public User create(User object) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(INSERT);
            ps.setString(1, object.login());
            ps.setString(2, object.passwordHash());
            ps.setString(3, object.role().name());

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
    public Optional<User> findById(Long id) throws SQLException {
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
    public List<User> findAll() throws SQLException {
        return queryList(SELECT_ALL);
    }

    @Override
    public boolean update(User object) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(UPDATE);
            ps.setString(1, object.login());
            ps.setString(2, object.passwordHash());
            ps.setString(3, object.role().name());
            ps.setLong(4, object.id());

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

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("login"),
                rs.getString("password_hash"),
                User.Role.valueOf(rs.getString("role"))
        );
    }

    private List<User> queryList(String sql) throws SQLException {
        try (Connection c = ConnectionProvider.INSTANCE.getConnection()) {

            PreparedStatement ps = c.prepareStatement(sql);
            List<User> users = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
                return users;
            }
        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
            return null;
        }
    }
}
