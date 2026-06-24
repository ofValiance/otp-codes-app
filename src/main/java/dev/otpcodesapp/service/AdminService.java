package dev.otpcodesapp.service;

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

    public AdminService(OtpConfigDaoImpl otpConfigDao, UserDao userDao) {
        this.ocd = otpConfigDao;
        this.ud = userDao;
    }

    public GetOtpConfigResponse getOtpConfig() throws SQLException {

        existsConfig();
        OtpConfig config = ocd.findById(1L).get();
        return new GetOtpConfigResponse(
                config.codeLength(),
                config.ttlSeconds()
        );
    }

    public void updateOtpConfig(UpdateOtpConfigRequest ucr) throws SQLException {

        existsConfig();
        ocd.update(new OtpConfig(
                1L,
                ucr.codeLength(),
                ucr.ttlSeconds())
        );
    }

    private void existsConfig() throws SQLException {

        if (ocd.findById(1L).isEmpty()) {
            throw new ConfigNotFoundException("Otp config does not exist");
        }
    }

    public List<GetUsersResponse> getUsers() throws SQLException {

        List<User> users = ud.findAllNonAdmins();
        List<GetUsersResponse> response = new ArrayList<>();

        for (User user : users) {
            GetUsersResponse thisResponse = new GetUsersResponse(user.id(), user.login());
            response.add(thisResponse);
        }

        return response;
    }

    public void deleteUser(Long id) throws SQLException {

        if (ud.existsById(id)) {

            User user = ud.findById(id).get();
            if (user.role().equals(User.Role.ADMIN)) {
                throw new CanNotDeleteAdminException("Admin can not be deleted");
            }
            ud.deleteById(id);

        } else {
            throw new UserNotFoundException("User with id = " + id + " does not exist");
        }
    }
}
