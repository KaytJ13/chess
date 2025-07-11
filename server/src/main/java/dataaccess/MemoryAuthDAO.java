package dataaccess;

import model.AuthData;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {
    private final HashSet<AuthData> AuthDB;

    public MemoryAuthDAO () {
        this.AuthDB = new HashSet<>();
    };

    @Override
    public void clear() {
        AuthDB.clear();
    };

    @Override
    public void createAuth(AuthData data) {
        AuthDB.add(data);
    };

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData data : AuthDB) {
            if (data.authToken() == authToken) {
                return data;
            }
        }
        return null;
    };

    @Override
    public void deleteAuth(AuthData data) {
        AuthDB.remove(data);
    };
}
