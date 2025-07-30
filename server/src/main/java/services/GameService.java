package services;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import requests.JoinRequest;
import model.AuthData;
import model.GameData;
import requests.ListGamesResponse;
import requests.UserFriendlyGameData;

import java.util.Objects;

public class GameService {
    // createGame, joinGame, listGames
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private Boolean matchAuth(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            return false;
        } else {
            return Objects.equals(authData.authToken(), authToken);
        }
    }

    public int createGame(String gameName, String authToken) throws ResponseException, DataAccessException {
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
        ChessGame newGame = new ChessGame();
        newGame.getBoard().resetBoard();
        GameData gameData = new GameData(gameID, null, null, gameName, newGame);
        gameDAO.createGame(gameData);
        return gameID;
    }

    public ListGamesResponse listGames(String authToken)
            throws ResponseException, DataAccessException {
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
        return new ListGamesResponse(finalGameList);
    }

    public void joinGame(JoinRequest joinRequest, String authToken)
            throws ResponseException, DataAccessException {
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
