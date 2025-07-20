package dataaccess;

import exception.ResponseException;
import model.AuthData;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO () throws ResponseException, DataAccessException {
        //constructor
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authData (
              `username` varchar(255) NOT NULL,
              `authToken` varchar(255) NOT NULL,
              PRIMARY KEY (`username`),
            );
            """
//            `json` TEXT DEFAULT NULL, <== This line was after authToken, but I don't think I need a json here
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
    public AuthData getAuth(String authToken) throws DataAccessException {
        //gets auth
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT FROM authData WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("username"), rs.getString("authToken"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void createAuth(AuthData data) throws ResponseException {
        //inserts an auth
        var statement = "INSERT INTO authData (username, authToken) VALUES (?, ?)";
        executeUpdate(statement, data.username(), data.authToken());
    }

    @Override
    public void deleteAuth(AuthData data) throws ResponseException {
        //removes an auth
        var statement = "DELETE FROM authData WHERE username=?";
        executeUpdate(statement, data.username());
    }

    @Override
    public void clear() throws ResponseException {
        //clears authdata from database
        var statement = "TRUNCATE authData";
        executeUpdate(statement);
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
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
