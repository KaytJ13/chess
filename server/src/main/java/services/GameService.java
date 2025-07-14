package services;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.util.Objects;

public class GameService {
    // createGame, joinGame, listGames
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public int createGame(String gameName, String authToken) throws ResponseException {
        if (gameName == null || authToken == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        } else if (!Objects.equals(authData.authToken(), authToken)) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        int gameID = 1;
        while (gameDAO.getGame(gameID) != null) {
            gameID++;
        }
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDAO.createGame(gameData);
        return gameID;
    }
}
