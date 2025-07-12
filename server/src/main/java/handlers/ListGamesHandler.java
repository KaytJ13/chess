package handlers;

import services.GameService;

public class ListGamesHandler {
    private GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }
}
