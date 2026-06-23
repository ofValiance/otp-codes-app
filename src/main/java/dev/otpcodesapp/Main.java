package dev.otpcodesapp;

import com.sun.net.httpserver.HttpServer;
import dev.otpcodesapp.api.handler.AdminHandler;
import dev.otpcodesapp.api.handler.AuthHandler;
import dev.otpcodesapp.api.handler.OtpHandler;
import dev.otpcodesapp.api.middleware.JwtAuthFilter;
import dev.otpcodesapp.api.middleware.JwtUtil;
import dev.otpcodesapp.dao.CodeDao;
import dev.otpcodesapp.dao.UserDao;
import dev.otpcodesapp.dao.impl.jdbc.CodeDaoImpl;
import dev.otpcodesapp.dao.impl.jdbc.OtpConfigDaoImpl;
import dev.otpcodesapp.dao.impl.jdbc.UserDaoImpl;
import dev.otpcodesapp.model.OtpConfig;
import dev.otpcodesapp.service.AuthService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;


public class Main {
    public static void main(String[] args) throws IOException {

        JwtUtil jwtUtil = new JwtUtil(60);
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtUtil);

        UserDao userDao = new UserDaoImpl();
        CodeDao codeDao = new CodeDaoImpl();
        OtpConfigDaoImpl otpConfigDao = new OtpConfigDaoImpl();

        AuthService authService = new AuthService(userDao, jwtUtil);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        server.createContext("/api/auth", new AuthHandler(authService));
        server.createContext("/api/otp", new OtpHandler()).getFilters().add(jwtAuthFilter);
        server.createContext("/api/admin", new AdminHandler()).getFilters().add(jwtAuthFilter);

        server.start();
        System.out.println("Server started on port 8080");
    }
}
