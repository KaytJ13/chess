package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{
    private final HashSet<GameData> gameDB;

    public MemoryGameDAO() {
        this.gameDB = new HashSet<>();
    }

    @Override
    public void clear() {
        gameDB.clear();
    }

    @Override
    public GameData getGame(int gameID) {
        for (GameData game : gameDB) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void createGame(GameData data) {
        gameDB.add(data);
    }

    @Override
    public GameData[] listGames() {
        GameData[] gameList = new GameData[gameDB.size()];
        int counter = 0;
        for (GameData game : gameDB) {
            gameList[counter] = game;
            counter++;
        }
        return gameList;
    }

    @Override
    public void updateGame (int gameID, ChessGame.TeamColor playerColor, String username) {
        GameData currentGame = getGame(gameID);
        GameData updatedVersion;
        gameDB.remove(currentGame);
        if (playerColor == ChessGame.TeamColor.WHITE) {
            updatedVersion = new GameData(gameID, username, currentGame.blackUsername(),
                    currentGame.gameName(), currentGame.game());
        } else {
            updatedVersion = new GameData(gameID, currentGame.whiteUsername(), username,
                    currentGame.gameName(), currentGame.game());
        }
        createGame(updatedVersion);
    }
}
