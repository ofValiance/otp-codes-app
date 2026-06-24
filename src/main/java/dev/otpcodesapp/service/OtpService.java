package dev.otpcodesapp.service;

import dev.otpcodesapp.api.dto.request.GenerateRequest;
import dev.otpcodesapp.api.dto.request.ValidateRequest;
import dev.otpcodesapp.api.exception.CodeDoesNotExistException;
import dev.otpcodesapp.api.exception.ConfigNotFoundException;
import dev.otpcodesapp.api.exception.ExpiredCodeException;
import dev.otpcodesapp.api.exception.InvalidCodeException;
import dev.otpcodesapp.dao.CodeDao;
import dev.otpcodesapp.dao.impl.jdbc.OtpConfigDaoImpl;
import dev.otpcodesapp.model.Code;
import dev.otpcodesapp.model.OtpConfig;

import java.util.concurrent.ThreadLocalRandom;
import java.sql.SQLException;
import java.time.Instant;


public class OtpService {

    private final CodeDao cd;
    private final OtpConfigDaoImpl ocd;

    public OtpService(CodeDao codeDao, OtpConfigDaoImpl otpConfigDao) {
        this.cd = codeDao;
        this.ocd = otpConfigDao;
    }

    public void generate(Long userId, GenerateRequest gr) throws SQLException {

        if (cd.findActiveByUserAndOperation(userId, gr.operationId()).isPresent()) {
            Code code = cd.findActiveByUserAndOperation(userId, gr.operationId()).get();
            if (code.status().equals(Code.Status.ACTIVE)) {
                Code prevCode = new Code(
                        code.id(),
                        code.userId(),
                        code.operationId(),
                        code.code(),
                        Code.Status.EXPIRED,
                        code.createdAt(),
                        Instant.now(),
                        code.usedAt()
                );
                cd.update(prevCode);
                cd.create(createCode(userId, gr, generateCode()));
            } else {
                cd.create(createCode(userId, gr, generateCode()));
            }
        } else {
            cd.create(createCode(userId, gr, generateCode()));
        }
    }

    private int generateCode() throws SQLException {

        OtpConfig config = getConfig();
        int max = (int) Math.pow(10, config.codeLength());
        int min = max / 10;

        return ThreadLocalRandom.current().nextInt(min, max);
    }

    private Code createCode(Long userId, GenerateRequest gr, int code) throws SQLException {

        OtpConfig config = getConfig();
        return new Code(
                null,
                userId,
                gr.operationId(),
                code,
                Code.Status.ACTIVE,
                Instant.now(),
                Instant.now().plusMillis(config.ttlSeconds() * 1000L),
                null
        );
    }

    private OtpConfig getConfig() throws SQLException {

        if (ocd.findById(1L).isEmpty()) {
            throw new ConfigNotFoundException("Otp config does not exist");
        }
        return ocd.findById(1L).get();
    }

    public void validate(Long userId, ValidateRequest vr) throws SQLException {

        if (cd.findActiveByUserAndOperation(userId, vr.operationId()).isPresent()) {
            Code code = cd.findActiveByUserAndOperation(userId, vr.operationId()).get();
            if (code.expiresAt().isBefore(Instant.now())) {
                Code expiredCode = new Code(
                        code.id(),
                        code.userId(),
                        code.operationId(),
                        code.code(),
                        Code.Status.EXPIRED,
                        code.createdAt(),
                        code.expiresAt(),
                        code.usedAt()
                );
                cd.update(expiredCode);
                throw new ExpiredCodeException("Code is expired");
            }
            if (code.code() == vr.code()) {
                Code validCode = new Code(
                        code.id(),
                        code.userId(),
                        code.operationId(),
                        code.code(),
                        Code.Status.USED,
                        code.createdAt(),
                        code.expiresAt(),
                        Instant.now()
                );
                cd.update(validCode);
            } else {
                throw new InvalidCodeException("Provided code is invalid");
            }
        } else {
            throw new CodeDoesNotExistException("There is no active code for this operation");
        }
    }
}
