package dataaccess;

import chess.ChessGame;
import model.GameData;

public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() {
        //constructor
    }

    @Override
    public void clear() {
        //clear game data
    }

    @Override
    public GameData getGame(int gameID) {
        //return a game
        return null;
    }

    @Override
    public GameData[] listGames() {
        //list games
        return null;
    }

    @Override
    public void createGame(GameData data) {
        //insert a game
    }

    @Override
    public void updateGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
        //update a game
    }
}
