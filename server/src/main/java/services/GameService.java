package services;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class GameService {
    // createGame, joinGame, listGames
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserDAO userDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }
}
