package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {
    String username;
    ChessGame.TeamColor color;

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, String username, ChessGame.TeamColor color) {
        super(commandType, authToken, gameID);
        this.username = username;
        this.color = color;
    }
}
