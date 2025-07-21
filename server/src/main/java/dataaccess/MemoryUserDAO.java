package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Objects;

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
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        userDB.add(new UserData(userData.username(), hashedPassword, userData.email()));
    }

    @Override
    public UserData getUser(String username) {
        for (UserData data : userDB) {
            if (Objects.equals(data.username(), username)) {
                return data;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryUserDAO that = (MemoryUserDAO) o;
        return Objects.equals(userDB, that.userDB);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userDB);
    }
}
