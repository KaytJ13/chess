package services;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import handlers.JoinGameHandler;
import handlers.ListGamesHandler;
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

    private Boolean matchAuth(String authToken) {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            return false;
        } else return Objects.equals(authData.authToken(), authToken);
    }

    public int createGame(String gameName, String authToken) throws ResponseException {
        if (gameName == null || authToken == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (!matchAuth(authToken)) {
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

    public record UserFriendlyGameData(int gameID, String whiteUsername, String blackUsername, String gameName) {}

    public ListGamesHandler.ListGamesResponse listGames(String authToken) throws ResponseException {
        if (authToken == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (!matchAuth(authToken)) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        GameData[] gameList = gameDAO.listGames();
        UserFriendlyGameData[] finalGameList = new UserFriendlyGameData[gameList.length];
        for (int i = 0; i < gameList.length; i++) {
            GameData data = gameList[i];
            UserFriendlyGameData updatedData = new UserFriendlyGameData(data.gameID(), data.whiteUsername(),
                    data.blackUsername(), data.gameName());
            finalGameList[i] = updatedData;
        }
        return new ListGamesHandler.ListGamesResponse(finalGameList);
    }

    public void joinGame(JoinGameHandler.JoinRequest joinRequest, String authToken) throws ResponseException, DataAccessException {
        if (authToken == null || joinRequest.playerColor() == null || joinRequest.gameID() == 0) {
            throw new ResponseException(400, "Error: bad request");
        }
        AuthData authData = authDAO.getAuth(authToken);
        if (!matchAuth(authToken)) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        GameData gameData = gameDAO.getGame(joinRequest.gameID());
        if (gameData == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (joinRequest.playerColor() == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null) {
            throw new ResponseException(403, "Error: already taken");
        } else if (joinRequest.playerColor() == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null) {
            throw new ResponseException(403, "Error: already taken");
        }
        gameDAO.updateGame(joinRequest.gameID(), joinRequest.playerColor(), authData.username());
    }
}
