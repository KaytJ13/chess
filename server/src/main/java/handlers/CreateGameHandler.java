package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import requests.CreateGameRequest;
import requests.CreateGameResponse;
import services.GameService;

import java.util.Map;

public class CreateGameHandler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object createGame(String jsonBody, String authToken) throws ResponseException, DataAccessException {
        CreateGameRequest gameRequest = new Gson().fromJson(jsonBody, CreateGameRequest.class);
        int gameID = gameService.createGame(gameRequest.gameName(), authToken);
        return new Gson().toJson(new CreateGameResponse(gameID));
    }
}
