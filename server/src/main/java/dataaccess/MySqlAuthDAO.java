package dataaccess;

import model.AuthData;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO () {
        //constructor
    }

    @Override
    public AuthData getAuth(String authToken) {
        //gets auth
    }

    @Override
    public void createAuth(AuthData data) {
        //inserts an auth
    }

    @Override
    public void deleteAuth(AuthData data) throws DataAccessException {
        //removes an auth
    }

    @Override
    public void clear() {
        //clears authdata from database
    }
}
