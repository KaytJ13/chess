package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import services.UserService;

public class LogoutHandler {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public void logout(String authToken) throws ResponseException, DataAccessException {
        userService.logout(authToken);
    }
}
