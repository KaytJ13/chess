package services;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
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

    public AuthData register(RegisterHandler.RegisterRequest registerRequest) {
        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(user);
        AuthData auth = new AuthData(generateToken(), registerRequest.username());
        authDAO.createAuth(auth);
        return auth;
    }
}
