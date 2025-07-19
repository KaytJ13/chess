package dataaccess;

import model.UserData;

public class MySqlUserDAO implements UserDAO {

    public MySqlUserDAO() {
        //constructor
    }

    @Override
    public void clear() {
        //clears user data
    }

    @Override
    public UserData getUser(String username) {
        //gets user
    }

    @Override
    public void createUser(UserData userData) {
        //inserts user
    }
}
