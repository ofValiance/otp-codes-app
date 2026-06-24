package dev.otpcodesapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;

import dev.otpcodesapp.api.handler.AdminHandler;
import dev.otpcodesapp.api.handler.AuthHandler;
import dev.otpcodesapp.api.handler.OtpHandler;
import dev.otpcodesapp.api.middleware.JwtAuthFilter;
import dev.otpcodesapp.scheduler.MarkExpiredTask;
import dev.otpcodesapp.service.AdminService;
import dev.otpcodesapp.service.MailingService;
import dev.otpcodesapp.util.JwtUtil;
import dev.otpcodesapp.dao.CodeDao;
import dev.otpcodesapp.dao.UserDao;
import dev.otpcodesapp.dao.impl.jdbc.CodeDaoImpl;
import dev.otpcodesapp.dao.impl.jdbc.OtpConfigDaoImpl;
import dev.otpcodesapp.dao.impl.jdbc.UserDaoImpl;
import dev.otpcodesapp.model.User;
import dev.otpcodesapp.service.AuthService;
import dev.otpcodesapp.service.OtpService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {

        JwtUtil jwtUtil = new JwtUtil(60);
        JwtAuthFilter UserAuthFilter = new JwtAuthFilter(jwtUtil, User.Role.USER);
        JwtAuthFilter AdminAuthFilter = new JwtAuthFilter(jwtUtil, User.Role.ADMIN);

        UserDao userDao = new UserDaoImpl();
        CodeDao codeDao = new CodeDaoImpl();
        OtpConfigDaoImpl otpConfigDao = new OtpConfigDaoImpl();

        AuthService authService = new AuthService(userDao, jwtUtil);
        MailingService mailingService = new MailingService();
        OtpService otpService = new OtpService(codeDao, otpConfigDao, mailingService);
        AdminService adminService = new AdminService(otpConfigDao, userDao);

        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        server.createContext("/api/auth", new AuthHandler(authService));
        server.createContext("/api/otp", new OtpHandler(otpService)).getFilters().add(UserAuthFilter);
        server.createContext("/api/admin", new AdminHandler(adminService)).getFilters().add(AdminAuthFilter);

        server.start();
        logger.info("Server started on port {}", port);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                new MarkExpiredTask(codeDao),
                60,
                60,
                TimeUnit.SECONDS
        );
        logger.info("MarkExpiredTask is running on separate thread");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdown();
            server.stop(1);
            logger.info("App shutdown");
        }));
    }
}
