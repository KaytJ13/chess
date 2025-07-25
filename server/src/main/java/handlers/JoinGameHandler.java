package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import requests.JoinRequest;
import services.GameService;

public class JoinGameHandler {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void joinGame(String jsonBody, String authToken) throws ResponseException, DataAccessException {
        JoinRequest joinRequest = new Gson().fromJson(jsonBody, JoinRequest.class);
        gameService.joinGame(joinRequest, authToken);
    }
}
