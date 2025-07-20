package dataaccess;

import exception.ResponseException;
import model.UserData;

public interface UserDAO {
    //Create, read, update, delete

    void clear() throws ResponseException;

    void createUser(UserData userData) throws ResponseException;

    UserData getUser(String username) throws DataAccessException;

}
