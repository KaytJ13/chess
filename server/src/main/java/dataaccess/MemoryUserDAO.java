package dataaccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {
    private final HashSet<UserData> userDB;

    public MemoryUserDAO() {
        this.userDB = new HashSet<>();
    }

    @Override
    public void clear() {
        userDB.clear();
    }

    @Override
    public void createUser(UserData userData) {
        userDB.add(userData);
    }

    @Override
    public UserData getUser(String username) {
        for (UserData data : userDB) {
            if (data.username() == username) {
                return data;
            }
        }
        return null;
    }
}
