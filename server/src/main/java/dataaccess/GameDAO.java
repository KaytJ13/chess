package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    //Create, read, update, delete

    public void clear();

    public GameData getGame(int gameID);

    public void createGame(GameData data);

    public GameData[] listGames();

    public void updateGame (int gameID, ChessGame.TeamColor playerColor, String username); //playerColor will probably actually be an enum somewhere, not a string

}
