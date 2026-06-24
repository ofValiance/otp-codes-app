package dev.otpcodesapp.service;

import dev.otpcodesapp.api.dto.request.LoginRequest;
import dev.otpcodesapp.api.dto.response.LoginResponse;
import dev.otpcodesapp.api.dto.request.RegisterRequest;
import dev.otpcodesapp.api.exception.AdminAlreadyExistsException;
import dev.otpcodesapp.api.exception.BusinessException;
import dev.otpcodesapp.api.exception.InvalidCredentialsException;
import dev.otpcodesapp.api.exception.UsernameAlreadyTakenException;
import dev.otpcodesapp.util.JwtUtil;
import dev.otpcodesapp.dao.UserDao;
import dev.otpcodesapp.model.User;
import dev.otpcodesapp.util.HashUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;


public class AuthService {

    private final UserDao ud;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserDao userDao, JwtUtil jwtUtil) {
        this.ud = userDao;
        this.jwtUtil = jwtUtil;
        logger.info("AuthService initialized");
    }

    public void register(RegisterRequest rr) throws SQLException {

        String login = rr.login();
        String passwordHash = HashUtil.hashGenerate(rr.password());
        User.Role role = User.Role.valueOf(rr.role());
        logger.info("Registering new user: login={}, role={}", login, role);

        if (role.equals(User.Role.USER)) {

            if (ud.findByLogin(login).isPresent()) {
                logger.warn("Registration failed: username '{}' is already taken", login);
                throw new UsernameAlreadyTakenException("Username is already taken");
            } else {
                ud.create(new User(null, login, passwordHash, role));
                logger.info("User '{}' registered successfully", login);
            }
        } else if (role.equals(User.Role.ADMIN)) {

            if (ud.existsAdmin()) {
                logger.warn("Registration failed: admin is already registered");
                throw new AdminAlreadyExistsException("Admin is already registered (1 is allowed per server)");
            } else if (ud.findByLogin(login).isPresent()) {
                logger.warn("Registration failed: username '{}' is already taken", login);
                throw new UsernameAlreadyTakenException("Username is already taken");
            } else {
                ud.create(new User(null, login, passwordHash, role));
                logger.info("Admin '{}' registered successfully", login);
            }
        }
    }

    public LoginResponse login(LoginRequest lr) {

        String login = lr.login();
        String password = lr.password();
        logger.info("Login attempt for user: {}", login);

        try {
            if (ud.findByLogin(login).isPresent()) {
                User user = ud.findByLogin(login).get();
                if (HashUtil.checkHash(password, user.passwordHash())) {
                    String token = jwtUtil.generateToken(user.id(), user.login(), user.role().toString());
                    logger.info("User '{}' logged in successfully", login);
                    return new LoginResponse(user.login(), token);
                } else {
                    logger.warn("Login failed for user '{}': invalid password", login);
                    throw new InvalidCredentialsException("Invalid password");
                }
            } else {
                logger.warn("Login failed: user '{}' does not exist", login);
                throw new InvalidCredentialsException("User " + login + " does not exist");
            }
        } catch (SQLException e) {
            logger.error("Database error during login for user: {}", login, e);
            throw new BusinessException("Internal service error");
        }
    }
}
