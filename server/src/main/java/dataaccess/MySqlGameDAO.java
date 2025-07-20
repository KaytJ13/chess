package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

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
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public void clear() throws ResponseException {
        //clear game data
        var statement = "TRUNCATE gameData";
        executeUpdate(statement);
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

    private void executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500,
                    String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
