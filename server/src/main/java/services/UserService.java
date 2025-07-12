package services;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

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
}
