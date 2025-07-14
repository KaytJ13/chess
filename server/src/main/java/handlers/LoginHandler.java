package handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import services.UserService;

public class LoginHandler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public record LoginRequest(String username, String password) {}

    public String login(String jsonBody) throws ResponseException {
        LoginRequest request = new Gson().fromJson(jsonBody, LoginRequest.class);
        AuthData response = userService.login(request);
        return new Gson().toJson(response);
    }
}
