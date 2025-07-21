package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlUserDAO implements UserDAO {

    public MySqlUserDAO() {
        //constructor
        try {
            configureDatabase();
        } catch (ResponseException | DataAccessException e) {
            int i = 0;
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userData (
              username varchar(255) NOT NULL,
              password varchar(255) NOT NULL,
              email varchar(255) NOT NULL,
              PRIMARY KEY (username)
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
        //clears user data
        var statement = "TRUNCATE userData";
        executeUpdate(statement);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        //gets user
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM userData WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"),
                                rs.getString("password"), rs.getString("email"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) throws ResponseException {
        //inserts user
        var statement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        executeUpdate(statement, userData.username(), hashedPassword, userData.email());
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
