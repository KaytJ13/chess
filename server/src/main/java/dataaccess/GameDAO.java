package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

public interface GameDAO {
    //Create, read, update, delete

    void clear() throws ResponseException;

    GameData getGame(int gameID);

    void createGame(GameData data);

    GameData[] listGames();

    void updateGame (int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException;

}
