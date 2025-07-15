package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO {
    private final HashSet<AuthData> authDB;

    public MemoryAuthDAO () {
        this.authDB = new HashSet<>();
    }

    @Override
    public void clear() {
        authDB.clear();
    }

    @Override
    public void createAuth(AuthData data) {
        authDB.add(data);
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData data : authDB) {
            if (Objects.equals(data.authToken(), authToken)) {
                return data;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData data) throws DataAccessException {
        if (!authDB.contains(data)) {
            throw new DataAccessException("This AuthData is not in the database");
        }
        authDB.remove(data);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryAuthDAO that = (MemoryAuthDAO) o;
        return Objects.equals(authDB, that.authDB);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authDB);
    }
}
