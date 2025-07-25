package client;

import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.*;
import requests.CreateGameRequest;
import requests.ListGamesResponse;
import requests.RegisterRequest;
import requests.UserFriendlyGameData;
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
        server.stop();
    }


    @Test
    public void testClear() {
        try {
            AuthData auth = facade.register(new RegisterRequest("ger", "ger", "ger@ld.com"));
            facade.createGame(new CreateGameRequest("Gerald"), auth.authToken());
            facade.clear();
            ListGamesResponse expected = new ListGamesResponse(new UserFriendlyGameData[0]);
            assert Objects.equals(facade.listGames(auth.authToken()), expected);
        } catch (ResponseException e) {
            assert false : e.getMessage();
        }
    }

}
