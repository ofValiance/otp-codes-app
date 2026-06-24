package dev.otpcodesapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.otpcodesapp.api.dto.request.UpdateOtpConfigRequest;
import dev.otpcodesapp.api.dto.response.GetOtpConfigResponse;
import dev.otpcodesapp.api.dto.response.GetUsersResponse;
import dev.otpcodesapp.api.exception.CanNotDeleteAdminException;
import dev.otpcodesapp.api.exception.ConfigNotFoundException;
import dev.otpcodesapp.api.exception.UserNotFoundException;
import dev.otpcodesapp.dao.UserDao;
import dev.otpcodesapp.dao.impl.jdbc.OtpConfigDaoImpl;
import dev.otpcodesapp.model.OtpConfig;
import dev.otpcodesapp.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AdminService {

    private final OtpConfigDaoImpl ocd;
    private final UserDao ud;
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public AdminService(OtpConfigDaoImpl otpConfigDao, UserDao userDao) {
        this.ocd = otpConfigDao;
        this.ud = userDao;
        logger.info("AdminService initialized");
    }

    public GetOtpConfigResponse getOtpConfig() throws SQLException {

        logger.debug("Fetching Otp config");
        existsConfig();
        OtpConfig config = ocd.findById(1L).get();
        logger.info("Otp config fetched successfully");
        return new GetOtpConfigResponse(
                config.codeLength(),
                config.ttlSeconds()
        );
    }

    public void updateOtpConfig(UpdateOtpConfigRequest ucr) throws SQLException {

        logger.info("Updating Otp config: codeLength={}, ttlSeconds={}", ucr.codeLength(), ucr.ttlSeconds());
        existsConfig();
        ocd.update(new OtpConfig(
                1L,
                ucr.codeLength(),
                ucr.ttlSeconds())
        );
        logger.info("Otp config updated successfully");
    }

    private void existsConfig() throws SQLException {

        logger.debug("Checking if Otp config exists");
        if (ocd.findById(1L).isEmpty()) {
            logger.warn("Otp config does not exist");
            throw new ConfigNotFoundException("Otp config does not exist");
        }
    }

    public List<GetUsersResponse> getUsers() throws SQLException {

        logger.debug("Fetching all non-admin users");
        List<User> users = ud.findAllNonAdmins();
        List<GetUsersResponse> response = new ArrayList<>();

        for (User user : users) {
            GetUsersResponse thisResponse = new GetUsersResponse(user.id(), user.login());
            response.add(thisResponse);
        }
        logger.info("Fetched {} non-admin users", response.size());
        return response;
    }

    public void deleteUser(Long id) throws SQLException {

        logger.info("Attempting to delete user with id={}", id);
        if (ud.existsById(id)) {
            User user = ud.findById(id).get();
            if (user.role().equals(User.Role.ADMIN)) {
                logger.warn("Attempted to delete an admin user with id={}", id);
                throw new CanNotDeleteAdminException("Admin can not be deleted");
            }
            ud.deleteById(id);
            logger.info("User with id={} deleted successfully", id);
        } else {
            logger.warn("Attempted to delete non-existent user with id={}", id);
            throw new UserNotFoundException("User with id = " + id + " does not exist");
        }
    }
}
