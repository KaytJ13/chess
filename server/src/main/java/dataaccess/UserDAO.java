package dataaccess;

import model.UserData;

public interface UserDAO {
    //Create, read, update, delete

    public void clear();

    public void createUser(UserData userData);

    public UserData getUser(String username);

}
