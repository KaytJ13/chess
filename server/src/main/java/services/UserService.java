package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.ResponseException;
import handlers.LoginHandler.LoginRequest;
import handlers.RegisterHandler.RegisterRequest;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    // login, logout, createUser
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(RegisterRequest registerRequest) throws ResponseException {
        //Check if anything is null ==> Bad Request
        if (registerRequest.email() == null || registerRequest.password() == null ||
                registerRequest.username() == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new ResponseException(403, "Error: username already taken");
        }
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(user);
        AuthData auth = new AuthData(registerRequest.username(), generateToken());
        authDAO.createAuth(auth);
        return auth;
    }

    public AuthData login(LoginRequest loginRequest) throws ResponseException {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        UserData userData = userDAO.getUser(loginRequest.username());
        if (userData == null || !Objects.equals(userData.password(), loginRequest.password())) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        AuthData auth = new AuthData(loginRequest.username(), generateToken());
        authDAO.createAuth(auth);
        return auth;
    }

    public void logout(String authToken) throws ResponseException, DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        authDAO.deleteAuth(authData);
    }
}
