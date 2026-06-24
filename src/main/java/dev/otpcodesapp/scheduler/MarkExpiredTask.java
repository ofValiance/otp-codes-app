package dev.otpcodesapp.scheduler;

import dev.otpcodesapp.dao.CodeDao;

import dev.otpcodesapp.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;


public class MarkExpiredTask implements Runnable {

    private final CodeDao cd;
    private static final Logger logger = LoggerFactory.getLogger(MarkExpiredTask.class);

    public MarkExpiredTask(CodeDao codeDao) {
        this.cd = codeDao;
    }

    @Override
    public void run() {
        try {
            cd.markExpiredBatch();
            logger.info("Updated expired codes status");
        } catch (SQLException e) {
            System.out.println("[JDBC Error] " + e.getMessage());
            logger.warn("Error during expired codes status update");
        }
    }
}
