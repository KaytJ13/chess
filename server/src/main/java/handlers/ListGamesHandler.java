package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import services.GameService;

public class ListGamesHandler {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public record ListGamesResponse(GameService.UserFriendlyGameData[] games) {}

    public Object listGames(String authToken) throws ResponseException, DataAccessException {
        ListGamesResponse response = gameService.listGames(authToken);
        return new Gson().toJson(response);
    }
}
