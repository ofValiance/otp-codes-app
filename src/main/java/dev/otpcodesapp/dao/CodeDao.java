package dev.otpcodesapp.dao;

import dev.otpcodesapp.model.Code;

import java.sql.SQLException;
import java.util.Optional;

public interface CodeDao extends Dao<Code, Long> {

    Optional<Code> findActiveByUserAndOperation(long userId, long operationId) throws SQLException;

    void markUsed(long id) throws SQLException;

    void markExpiredBatch() throws SQLException;
}
