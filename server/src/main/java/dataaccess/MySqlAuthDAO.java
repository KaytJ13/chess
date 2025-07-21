package dataaccess;

import exception.ResponseException;
import model.AuthData;


public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO () {
        //constructor
        try {
            configureDatabase();
        } catch (ResponseException | DataAccessException e) {
            int i = 0;
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authData (
              idNum INT NOT NULL AUTO_INCREMENT,
              username VARCHAR(255) NOT NULL,
              authToken VARCHAR(255) NOT NULL,
              PRIMARY KEY (idNum)
            );
            """
};

    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.configureDatabaseHelper(createStatements);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        //gets auth
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM authData WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("username"),
                                rs.getString("authToken"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void createAuth(AuthData data) throws ResponseException {
        //inserts an auth
        var statement = "INSERT INTO authData (username, authToken) VALUES (?, ?)";
        DatabaseManager.executeUpdate(statement, data.username(), data.authToken());
    }

    @Override
    public void deleteAuth(AuthData data) throws ResponseException {
        //removes an auth
        var statement = "DELETE FROM authData WHERE authToken=?";
        DatabaseManager.executeUpdate(statement, data.authToken());
    }

    @Override
    public void clear() throws ResponseException {
        //clears auth data from database
        var statement = "TRUNCATE authData";
        DatabaseManager.executeUpdate(statement);
    }
}
