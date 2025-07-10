package dataaccess;

import model.AuthData;

public interface AuthDAO {
    //Create, read, update, delete

    public void clear();

    public AuthData getAuth(String authToken);

    public void deleteAuth();

}
