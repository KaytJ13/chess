package server;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import handlers.*;
import model.AuthData;
import services.*;
import spark.*;

public class Server {
    private final ClearHandler clearHandler;
    private final CreateGameHandler createGameHandler;
    private final JoinGameHandler joinGameHandler;
    private final ListGamesHandler listGamesHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final RegisterHandler registerHandler;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(userDAO, authDAO, gameDAO);
        UserService userService = new UserService(userDAO, authDAO, gameDAO);

        this.clearHandler = new ClearHandler(new ClearService(userDAO, authDAO, gameDAO));
        this.createGameHandler = new CreateGameHandler(gameService);
        this.joinGameHandler = new JoinGameHandler(gameService);
        this.listGamesHandler = new ListGamesHandler(gameService);
        this.loginHandler = new LoginHandler(userService);
        this.logoutHandler = new LogoutHandler(userService);
        this.registerHandler = new RegisterHandler(userService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res) {
        clearHandler.clear();
        res.status(200);
        return "";
    }

    private String exceptionHandler(ResponseException ex, Response res) {
        res.status(ex.getStatusCode());
        return ex.toJson();
    }

    private Object register(Request req, Response res) {
        AuthData authResult;
        try {
            authResult = registerHandler.register(req.body());
        } catch (ResponseException e) {
            return exceptionHandler(e, res);
        }
        res.status(200);
        return new Gson().toJson(authResult);
    }

    private Object login(Request req, Response res) {
        String authResult;
        try {
            authResult = loginHandler.login(req.body());
        } catch (ResponseException e) {
            return exceptionHandler(e, res);
        }
        res.status(200);
        return authResult;
    }

    private Object logout(Request req, Response res) {
        try {
            logoutHandler.logout(req.headers("authorization"));
        } catch (ResponseException e) {
            return exceptionHandler(e, res);
        } catch (DataAccessException e) {
            return exceptionHandler(new ResponseException(500, "Error: " + e.getMessage()), res);
        }
        res.status(200);
        return "";
    }

    private Object createGame(Request req, Response res) {
        Object gameResponse;
        try {
            gameResponse = createGameHandler.createGame(req.body(), req.headers("authorization"));
        } catch (ResponseException e) {
            return exceptionHandler(e, res);
        }
        res.status(200);
        return gameResponse;
    }

    private Object listGames(Request req, Response res) {
        Object gameList;
        try {
            gameList = listGamesHandler.listGames(req.headers("authorization"));
        } catch (ResponseException e) {
            return exceptionHandler(e, res);
        }
        res.status();
        return gameList;
    }

    private Object joinGame(Request req, Response res) {
        try {
            joinGameHandler.joinGame(req.body(), req.headers("authorization"));
        } catch (ResponseException e) {
            return exceptionHandler(e, res);
        } catch (DataAccessException e) {
            return exceptionHandler(new ResponseException(500, "Error: " + e.getMessage()), res);
        }
        res.status();
        return "";
    }
}
