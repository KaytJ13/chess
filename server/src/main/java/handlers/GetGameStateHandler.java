package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import requests.GetGameStateRequest;
import services.GameService;

public class GetGameStateHandler {
    private final GameService gameService;

    public GetGameStateHandler(GameService service) {
        this.gameService = service;
    }

    public Object getGameState(String jsonBody, String authToken) throws ResponseException, DataAccessException {
        GetGameStateRequest request = new Gson().fromJson(jsonBody, GetGameStateRequest.class);
        return new Gson().toJson(gameService.getGameState(request.gameID(), authToken));
    }
}
