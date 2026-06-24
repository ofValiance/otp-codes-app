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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.sql.SQLException;
import java.time.Instant;


public class OtpService {

    private final CodeDao cd;
    private final OtpConfigDaoImpl ocd;
    private final MailingService mailingService;
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);


    public OtpService(CodeDao codeDao, OtpConfigDaoImpl otpConfigDao, MailingService mailingService) {
        this.cd = codeDao;
        this.ocd = otpConfigDao;
        this.mailingService = mailingService;
        logger.info("OtpService initialized");
    }

    public void generate(Long userId, GenerateRequest gr) throws SQLException {

        logger.info("Generating code for userId={}, operationId={}", userId, gr.operationId());
        if (cd.findActiveByUserAndOperation(userId, gr.operationId()).isPresent()) {
            Code code = cd.findActiveByUserAndOperation(userId, gr.operationId()).get();
            if (code.status().equals(Code.Status.ACTIVE)) {
                logger.debug("Found existing ACTIVE code for operationId={}. Marking as EXPIRED.", gr.operationId());
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
            }
        }
        int code = generateCode();
        cd.create(createCode(userId, gr, code));
        logger.info("Code generated and saved to database for userId={}, operationId={}", userId, gr.operationId());

        mailingService.sendCode(gr.channel(), gr.destination(), code);
        logger.info("Code sent via channel={} to destination={}", gr.channel(), gr.destination());
    }

    private int generateCode() throws SQLException {

        logger.debug("Generating random code combination based on current config");
        OtpConfig config = getConfig();
        int max = (int) Math.pow(10, config.codeLength());
        int min = max / 10;

        int code = ThreadLocalRandom.current().nextInt(min, max);
        logger.debug("Code combination generated (length={}, ttlSeconds={})", config.codeLength(), config.ttlSeconds());
        return code;
    }

    private Code createCode(Long userId, GenerateRequest gr, int code) throws SQLException {

        logger.debug("Creating code object for userId={}, operationId={}", userId, gr.operationId());
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

        logger.debug("Fetching Otp config");
        if (ocd.findById(1L).isEmpty()) {
            logger.warn("Otp config does not exist");
            throw new ConfigNotFoundException("Otp config does not exist");
        }
        logger.debug("Otp config fetched successfully");
        return ocd.findById(1L).get();
    }

    public void validate(Long userId, ValidateRequest vr) throws SQLException {

        logger.info("Validating code for userId={}, operationId={}", userId, vr.operationId());
        if (cd.findActiveByUserAndOperation(userId, vr.operationId()).isPresent()) {
            Code code = cd.findActiveByUserAndOperation(userId, vr.operationId()).get();
            if (code.expiresAt().isBefore(Instant.now())) {
                logger.warn("Code for userId={}, operationId={} is expired", userId, vr.operationId());
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
                logger.info("Code for userId={}, operationId={} is valid. Marking as USED.", userId, vr.operationId());
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
                logger.warn("Invalid code provided for operationId={}", vr.operationId());
                throw new InvalidCodeException("Provided code is invalid");
            }
        } else {
            logger.warn("No active OTP code found for userId={}, operationId={}", userId, vr.operationId());
            throw new CodeDoesNotExistException("There is no active code for this operation");
        }
    }
}
