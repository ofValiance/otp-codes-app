package dev.otpcodesapp.service;

import dev.otpcodesapp.api.dto.request.LoginRequest;
import dev.otpcodesapp.api.dto.response.LoginResponse;
import dev.otpcodesapp.api.dto.request.RegisterRequest;
import dev.otpcodesapp.api.exception.AdminAlreadyExistsException;
import dev.otpcodesapp.api.exception.BusinessException;
import dev.otpcodesapp.api.exception.InvalidCredentialsException;
import dev.otpcodesapp.api.exception.UsernameAlreadyTakenException;
import dev.otpcodesapp.api.middleware.JwtUtil;
import dev.otpcodesapp.dao.UserDao;
import dev.otpcodesapp.model.User;

import java.sql.SQLException;


public class AuthService {

    private final UserDao ud;
    private final JwtUtil jwtUtil;

    public AuthService(UserDao userDao, JwtUtil jwtUtil) {
        this.ud = userDao;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequest rr) throws SQLException {

        String login = rr.login();
        String password = rr.password();
        User.Role role = User.Role.valueOf(rr.role());

        if (role.equals(User.Role.USER)) {

            if (ud.findByLogin(login).isPresent()) {
                throw new UsernameAlreadyTakenException("Username is already taken");
            } else {
                ud.create(new User(null, login, password, role));
            }
        } else if (role.equals(User.Role.ADMIN)) {

            if (ud.existsAdmin()) {
                throw new AdminAlreadyExistsException("Admin is already registered (1 is allowed per server)");
            } else if (ud.findByLogin(login).isPresent()) {
                throw new UsernameAlreadyTakenException("Username is already taken");
            } else {
                ud.create(new User(null, login, password, role));
            }
        }
    }

    public LoginResponse login(LoginRequest lr) {

        String login = lr.login();
        String password = lr.password();

        try {
            if (ud.findByLogin(login).isPresent()) {
                User user = ud.findByLogin(login).get();
                if (password.equals(user.passwordHash())) {
                    String token = jwtUtil.generateToken(user.id(), user.login(), user.role().toString());
                    return new LoginResponse(user.login(), token);
                } else {
                    throw new InvalidCredentialsException("Invalid password");
                }
            } else {
                throw new InvalidCredentialsException("User " + login + " does not exist");
            }
        } catch (SQLException e) {
            throw new BusinessException("Internal service error");
        }
    }
}
