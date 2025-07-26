package client;

import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.*;
import requests.*;
import server.Server;
import server.ServerFacade;

import java.util.Objects;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:8080");
    }

    @AfterAll
    static void stopServer() {
        try {
            facade.clear();
        } catch (ResponseException e) {
            return;
        }
        server.stop();
    }


    @Test
    public void testRegisterPositive() {
        try {
            AuthData auth = facade.register(new RegisterRequest("ger", "ger", "ger@ld.com"));
            auth = facade.login(new LoginRequest("ger", "ger"));
            assert auth != null;
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    public void testRegisterNegative() {
        try {
            facade.register(new RegisterRequest(null, "ger", "ger"));
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    public void testLoginPositive() {
        try {
            AuthData auth = facade.register(new RegisterRequest("Amy", "march", "artist@com"));
            facade.login(new LoginRequest("Amy", "march"));
            assert true;
        } catch (Exception e) {
            assert false : e.getMessage();
        }
    }

    @Test
    public void testLoginNegative() {
        try {
            facade.login(new LoginRequest("Amy", "march"));
            assert true;
        } catch (Exception e) {
            assert false : e.getMessage();
        }
    }

    @Test
    public void testClear() {
        try {
            AuthData auth = facade.register(new RegisterRequest("ger", "ger", "ger@ld.com"));
            facade.createGame(new CreateGameRequest("Gerald"), auth.authToken());
            facade.clear();
            ListGamesResponse expected = new ListGamesResponse(new UserFriendlyGameData[0]);
            assert Objects.equals(facade.listGames(auth.authToken()), expected);
            auth = facade.login(new LoginRequest("ger", "ger"));
            assert false : auth.username();
        } catch (ResponseException e) {
            assert true : e.getMessage();
        }
    }

}
