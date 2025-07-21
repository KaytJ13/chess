package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

public interface GameDAO {
    //Create, read, update, delete

    void clear() throws ResponseException;

    GameData getGame(int gameID) throws DataAccessException;

    void createGame(GameData data) throws ResponseException;

    GameData[] listGames() throws ResponseException;

    void updateGame (int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException, ResponseException;

}
