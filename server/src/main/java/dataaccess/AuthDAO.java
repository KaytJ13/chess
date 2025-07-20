package dataaccess;

import exception.ResponseException;
import model.AuthData;

public interface AuthDAO {
    //Create, read, update, delete

    void clear() throws ResponseException;

    void createAuth(AuthData data) throws ResponseException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(AuthData data) throws DataAccessException, ResponseException;

}
