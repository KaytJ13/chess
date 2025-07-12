package handlers;

import services.GameService;

public class CreateGameHandler {
    private GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
}
