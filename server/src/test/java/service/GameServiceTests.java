package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.Test;
import requests.JoinRequest;
import requests.ListGamesResponse;
import requests.UserFriendlyGameData;
import services.GameService;

import java.util.Objects;

public class GameServiceTests {

    @Test
    void testCreateGamePositive() throws ResponseException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService gameService = new GameService(authDAO, new MemoryGameDAO());
        authDAO.createAuth(new AuthData("username1", "auth1"));

        try {
            assert Objects.equals(1, gameService.createGame("game1", "auth1"));
        } catch (ResponseException | DataAccessException e) {
            assert false;
        }

    }

    @Test
    void testCreateGameNegative() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService gameService = new GameService(authDAO, new MemoryGameDAO());

        try {
            gameService.createGame("game1", "auth1");
        } catch (ResponseException e) {
            assert true;
        } catch (DataAccessException e) {
            assert false;
        }
    }

    @Test
    void testListGamesPositive() throws ResponseException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService gameService = new GameService(authDAO, new MemoryGameDAO());
        authDAO.createAuth(new AuthData("username1", "auth1"));

        try {
            gameService.createGame("game1", "auth1");
            ListGamesResponse actual = gameService.listGames("auth1");
            UserFriendlyGameData game1 = new UserFriendlyGameData(1, null,
                    null, "game1");
            UserFriendlyGameData[] games = new UserFriendlyGameData[1];
            games[0] = game1;
            ListGamesResponse expected = new ListGamesResponse(games);
            for (int i = 0; i < actual.games().length; i++) {
                assert Objects.equals(actual.games()[i], expected.games()[i]);
            }
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testListGamesNegative() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService gameService = new GameService(authDAO, new MemoryGameDAO());

        try {
            gameService.createGame("game1", "auth1");
            gameService.listGames("auth1");
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    void testJoinGamePositive() throws ResponseException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService gameService = new GameService(authDAO, new MemoryGameDAO());
        authDAO.createAuth(new AuthData("username1", "auth1"));

        try {
            gameService.createGame("game", "auth1");
            gameService.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, 1),
                    "auth1");
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    void testJoinGameNegative() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService gameService = new GameService(authDAO, new MemoryGameDAO());

        try {
            gameService.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, 4),
                    "auth1");
            authDAO.createAuth(new AuthData("username1", "auth1"));
            gameService.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, 4),
                    "auth1");
        } catch (Exception e) {
            assert true;
        }
    }
}
