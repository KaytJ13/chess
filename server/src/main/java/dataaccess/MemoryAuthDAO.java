package dataaccess;

import model.AuthData;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {
    private final HashSet<AuthData> authDB;

    public MemoryAuthDAO () {
        this.authDB = new HashSet<>();
    };

    @Override
    public void clear() {
        authDB.clear();
    };

    @Override
    public void createAuth(AuthData data) {
        authDB.add(data);
    };

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData data : authDB) {
            if (data.authToken() == authToken) {
                return data;
            }
        }
        return null;
    };

    @Override
    public void deleteAuth(AuthData data) {
        authDB.remove(data);
    };
}
