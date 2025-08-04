package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

    private final ChessMove move;
    private final String username;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, String username, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
        this.username = username;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getUsername() {
        return username;
    }
}
