package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import services.ClearService;

public class ClearServiceTests {

    @Test
    void testClear() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

        userDAO.createUser(new UserData("username1", "password", "email"));
        userDAO.createUser(new UserData("username2", "password", "email"));
        userDAO.createUser(new UserData("username3", "password", "email"));

        authDAO.createAuth(new AuthData("username1", "authToken1"));
        authDAO.createAuth(new AuthData("username2", "authToken2"));
        authDAO.createAuth(new AuthData("username3", "authToken3"));

        gameDAO.createGame(new GameData(1, null, null, "game1", new ChessGame()));
        gameDAO.createGame(new GameData(2, null, null, "game2", new ChessGame()));
        gameDAO.createGame(new GameData(3, null, null, "game3", new ChessGame()));

        clearService.clear();
        assert clearService.equals(new ClearService(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO()));
    }
}
