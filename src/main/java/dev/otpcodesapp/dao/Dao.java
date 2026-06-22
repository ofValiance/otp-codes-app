package dev.otpcodesapp.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<T, ID> {

    T create(T object) throws SQLException;

    Optional<T> findById(ID id) throws SQLException;

    List<T> findAll() throws SQLException;

    boolean update(T object) throws SQLException;

    boolean deleteById(ID id) throws SQLException;

    boolean existsById(ID id) throws SQLException;
}
