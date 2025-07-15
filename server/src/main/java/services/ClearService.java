package services;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;

import java.util.Objects;

public class ClearService {
    // clear
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClearService that = (ClearService) o;
        return Objects.equals(userDAO, that.userDAO) && Objects.equals(authDAO, that.authDAO) &&
                Objects.equals(gameDAO, that.gameDAO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userDAO, authDAO, gameDAO);
    }
}
