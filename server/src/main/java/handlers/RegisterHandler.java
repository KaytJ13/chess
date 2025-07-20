package handlers;

import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import services.UserService;
import com.google.gson.Gson;

public class RegisterHandler {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public record RegisterRequest(String username, String password, String email) { }

    public Object register(String jsonBody) throws ResponseException, DataAccessException {
        RegisterRequest request = new Gson().fromJson(jsonBody, RegisterRequest.class);
        AuthData authData = userService.register(request);
        return new Gson().toJson(authData);
    }
}
