package handlers;

import services.UserService;

public class LogoutHandler {
    private UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public record LogoutRequest(String authToken) {}

    public void logout(String jsonBody) {

    }
}
