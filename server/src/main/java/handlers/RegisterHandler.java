package handlers;

import model.AuthData;
import services.UserService;
import com.google.gson.Gson;

public class RegisterHandler {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public record RegisterRequest(String username, String password, String email) { }

    public AuthData register(String jsonBody) {
        return userService.register(new Gson().fromJson(jsonBody, RegisterRequest.class));
    }
}
