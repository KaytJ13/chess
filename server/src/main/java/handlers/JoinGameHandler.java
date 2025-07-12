package handlers;

import services.GameService;

public class JoinGameHandler {
    private GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
}
