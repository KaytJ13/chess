package handlers;

import services.UserService;

public class RegisterHandler {
    private UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }
}
