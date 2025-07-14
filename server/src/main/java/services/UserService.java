package services;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import handlers.RegisterHandler;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {
    // login, logout, createUser
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(RegisterHandler.RegisterRequest registerRequest) throws ResponseException {
        //Check if anything is null ==> Bad Request
        if (registerRequest.email() == null || registerRequest.password() == null || registerRequest.username() == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        //Also, go into Server and edit the Json for the error so that it returns a json and not a string
        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new ResponseException(403, "Error: username already taken");
        }
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(user);
        AuthData auth = new AuthData(registerRequest.username(), generateToken());
        authDAO.createAuth(auth);
        return auth;
    }
}
