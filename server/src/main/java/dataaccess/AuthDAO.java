package dataaccess;

import model.AuthData;

public interface AuthDAO {
    //Create, read, update, delete

    public void clear();

    public void createAuth(AuthData data);

    public AuthData getAuth(String authToken);

    public void deleteAuth(AuthData data) throws DataAccessException;

}
