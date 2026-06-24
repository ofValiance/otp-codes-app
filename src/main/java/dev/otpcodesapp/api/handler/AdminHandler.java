package dev.otpcodesapp.api.handler;

import com.sun.net.httpserver.HttpExchange;
import dev.otpcodesapp.api.dto.request.UpdateOtpConfigRequest;
import dev.otpcodesapp.api.dto.response.GetOtpConfigResponse;
import dev.otpcodesapp.api.dto.response.GetUsersResponse;
import dev.otpcodesapp.api.exception.InvalidCredentialsException;
import dev.otpcodesapp.service.AdminService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AdminHandler extends BaseHandler {

    private final AdminService adminService;

    public AdminHandler(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    protected void dispatch(HttpExchange exchange) throws Exception {

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (method.equals("PUT")) {
            switch (path) {
                case "/api/admin/otp-config":
                    adminService.updateOtpConfig(parseBody(exchange, UpdateOtpConfigRequest.class));
                    sendSuccess(exchange, 200, "Resource Updated");
                    break;
                default:
                    sendError(exchange, 404, "Not Found");
                    break;
            }
        } else if (method.equals("GET")) {
            switch (path) {
                case "/api/admin/otp-config":
                    GetOtpConfigResponse cResponse = adminService.getOtpConfig();
                    sendJson(exchange, 200, cResponse);
                    break;
                case "/api/admin/users":
                    List<GetUsersResponse> uResponse = adminService.getUsers();
                    sendJson(exchange, 200, uResponse);
                    break;
                default:
                    sendError(exchange, 404, "Not Found");
                    break;
            }
        } else if (method.equals("DELETE")) {

            String[] pathArray = path.split("/");
            List<String> pathList = new ArrayList<>(Arrays.asList(pathArray));
            long id = 0L;
            if (pathList.size() == 5) {
                try {
                    id = Long.parseLong(pathList.get(4));
                } catch (NumberFormatException e) {
                    throw new InvalidCredentialsException("Incorrect user id format");
                }
                pathList.remove(4);
                path = String.join("/", pathList);
            }
            switch (path) {
                // /api/admin/users/{id}
                case "/api/admin/users":
                    adminService.deleteUser(id);
                    sendSuccess(exchange, 200, "User Deleted");
                    break;
                default:
                    sendError(exchange, 404, "Not Found");
                    break;
            }
        }
    }
}
