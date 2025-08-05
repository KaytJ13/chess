package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {
//    String username;
//    ChessGame.TeamColor color;

    public ConnectCommand(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
//        this.username = username;
//        this.color = color;
    }

//    public String getUsername() {
//        return username;
//    }

//    public ChessGame.TeamColor getColor() {
//        return color;
//    }
}
