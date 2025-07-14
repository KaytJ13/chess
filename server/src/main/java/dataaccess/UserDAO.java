package dataaccess;

import model.UserData;

public interface UserDAO {
    //Create, read, update, delete

    void clear();

    void createUser(UserData userData);

    UserData getUser(String username);

}
