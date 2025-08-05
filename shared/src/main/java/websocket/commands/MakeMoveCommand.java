package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

    private final ChessMove move;
//    private final String username;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
//        this.username = username;
    }

    public ChessMove getMove() {
        return move;
    }

//    public String getUsername() {
//        return username;
//    }

}
