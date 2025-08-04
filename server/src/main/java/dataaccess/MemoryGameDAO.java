package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;
import java.util.Objects;

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
        for (GameData game : gameDB) {
            gameList[game.gameID()-1] = game;
        }
        return gameList;
    }

    @Override
    public void updateGame (int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
        GameData currentGame = getGame(gameID);
        if (currentGame == null) {
            throw new DataAccessException("This game does not exist");
        }
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

    @Override
    public void madeMove(int gameID, ChessGame game) throws DataAccessException {
        GameData currentGame = getGame(gameID);
        if (currentGame == null) {
            throw new DataAccessException("This game does not exist");
        }
        gameDB.remove(currentGame);
        GameData updatedVersion = new GameData(gameID, currentGame.whiteUsername(), currentGame.blackUsername(),
                    currentGame.gameName(), game);
        createGame(updatedVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryGameDAO that = (MemoryGameDAO) o;
        return Objects.equals(gameDB, that.gameDB);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(gameDB);
    }
}
