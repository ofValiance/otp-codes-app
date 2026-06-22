package dev.otpcodesapp.dao;

import dev.otpcodesapp.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public interface UserDao extends Dao<User, Long> {

    Optional<User> findByLogin(String login) throws SQLException;

    List<User> findAllNonAdmins() throws SQLException;

    boolean existsAdmin() throws SQLException;
}
