package handlers;

import services.UserService;

public class LoginHandler {
    private UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }
}
