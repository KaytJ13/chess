package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import services.GameService;

public class GetGameStateHandler {
    private final GameService gameService;

    public GetGameStateHandler(GameService service) {
        this.gameService = service;
    }

    public Object getGameState(String jsonBody, String authToken) throws ResponseException, DataAccessException {
        int gameID = new Gson().fromJson(jsonBody, int.class);
        return new Gson().toJson(gameService.getGameState(gameID, authToken));
    }
}
