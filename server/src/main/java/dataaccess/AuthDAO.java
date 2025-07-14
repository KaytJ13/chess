package dataaccess;

import model.AuthData;

public interface AuthDAO {
    //Create, read, update, delete

    void clear();

    void createAuth(AuthData data);

    AuthData getAuth(String authToken);

    void deleteAuth(AuthData data) throws DataAccessException;

}
