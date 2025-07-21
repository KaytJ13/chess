package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;

import java.util.ArrayList;


public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() {
        //constructor
        try {
            configureDatabase();
        } catch (ResponseException | DataAccessException e) {
            int i = 0;
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  gameData (
              gameID INT NOT NULL,
              whiteUsername VARCHAR(255) DEFAULT NULL,
              blackUsername VARCHAR(255) DEFAULT NULL,
              gameName VARCHAR(255) NOT NULL,
              jsonGame TEXT NOT NULL,
              PRIMARY KEY (gameID)
            );
            """
    };

    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.configureDatabaseHelper(createStatements);
    }

    @Override
    public void clear() throws ResponseException {
        //clear game data
        var statement = "TRUNCATE gameData";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        //return a game
        try (var conn = DatabaseManager.getConnection()) {
            var statement =
                    "SELECT gameID, whiteUsername, blackUsername, gameName, jsonGame FROM gameData WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame game = new Gson().fromJson(rs.getString("jsonGame"), ChessGame.class);
                        return new GameData(rs.getInt("gameID"),
                                rs.getString("whiteUsername"), rs.getString("blackUsername"),
                                rs.getString("gameName"), game);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public GameData[] listGames() throws ResponseException {
        //list games
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, jsonGame FROM gameData";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(new GameData(rs.getInt("gameID"),
                                rs.getString("whiteUsername"), rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                new Gson().fromJson(rs.getString("jsonGame"), ChessGame.class)));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        GameData[] gamesList = new GameData[result.size()];
        for (GameData game : result) {
            gamesList[game.gameID()-1] = game;
        }
        return gamesList;
    }

    @Override
    public void createGame(GameData data) throws ResponseException {
        //insert a game
        var statement =
                "INSERT INTO gameData (gameID, whiteUsername, blackUsername, gameName, jsonGame) VALUES (?, ?, ?, ?, ?)";
        DatabaseManager.executeUpdate(statement, data.gameID(), data.whiteUsername(), data.blackUsername(),
                data.gameName(), new Gson().toJson(data.game()));
    }

    @Override
    public void updateGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException,
            ResponseException {
        //update a game
        String statement;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            statement = "UPDATE gameData SET whiteUsername = ? WHERE gameID = ?";
        } else if (playerColor == ChessGame.TeamColor.BLACK){
            statement = "UPDATE gameData SET blackUsername = ? WHERE gameID = ?";
        } else {
            throw new ResponseException(400, "Error: bad request");
        }
        DatabaseManager.executeUpdate(statement, username, gameID);

    }
}
