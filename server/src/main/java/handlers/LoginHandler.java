package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import requests.LoginRequest;
import services.UserService;

public class LoginHandler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public String login(String jsonBody) throws ResponseException, DataAccessException {
        LoginRequest request = new Gson().fromJson(jsonBody, LoginRequest.class);
        AuthData response = userService.login(request);
        return new Gson().toJson(response);
    }
}
