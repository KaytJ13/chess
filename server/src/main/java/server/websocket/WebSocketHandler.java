package server.websocket;

import chess.ChessGame;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.GameData;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;

    public WebSocketHandler(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResponseException, DataAccessException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(UserGameCommand.class, new CommandTypeAdapter());
        Gson gson = builder.create();

        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(((ConnectCommand) command), session);
            case LEAVE -> leave(((LeaveCommand) command), session);
            case RESIGN -> resign();
            case MAKE_MOVE -> makeMove(((MakeMoveCommand) command), session);
        }
    }

    public void connect(ConnectCommand command, Session session) throws IOException, ResponseException {
        connections.add(command.getUsername(), session);

        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            connections.broadcast("", loadGame);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Game not accessible");
        }

        String message;
        if (command.getColor() == null) {
            message = command.getUsername() + " is now observing the game.";
        } else {
            String colorString = command.getColor() == ChessGame.TeamColor.WHITE ? "white" : "black";
            message = command.getUsername() + " joined the game as " + colorString;
        }
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getUsername(), notification);

    }

    public void leave(LeaveCommand command, Session session) throws IOException, ResponseException, DataAccessException {
        gameDAO.updateGame(command.getGameID(), command.getColor(), null);

        connections.remove(command.getUsername());
        String message = command.getUsername() + " left the game";
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getUsername(), notification);
    }

    public void resign() {

    }

    public void makeMove(MakeMoveCommand command, Session session) throws ResponseException {
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            gameData.game().makeMove(command.getMove());
            String checkResponse = checkGameOver(gameData);
            gameDAO.madeMove(command.getGameID(), gameData.game());

            String message = command.getUsername() + " moved " + command.getMove().getStartPosition() + " to " +
                    command.getMove().getEndPosition();
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(command.getUsername(), notification);

            var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            connections.broadcast("", loadGame);

            if (checkResponse != null) {
                var checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkResponse);
                connections.broadcast("", checkNotification);
            }

        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: Game not accessible");
            try {
                connections.broadcast("", errorMessage);
            } catch (IOException ex) {
                throw new ResponseException(500, "Game not accessible");
            }
        } catch (InvalidMoveException e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: Move not legal");
            try {
                connections.broadcast("", errorMessage);
            } catch (IOException ex) {
                throw new ResponseException(500, "Game not accessible");
            }
        } catch (IOException e) {
            throw new ResponseException(500, "Game not accessible");
        }
    }

    private String checkGameOver(GameData gameData) {
        ChessGame game = gameData.game();

        String message = null;
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            message = "White team is in checkmate. Black team wins!";
            game.setGameOver(true);
        } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            message = "Black team is in checkmate. White team wins!";
            game.setGameOver(true);
        } else if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
            message = "Stalemate. Game over.";
            game.setGameOver(true);
        } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            message = "White team is in check.";
        } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            message = "Black team is in check.";
        }

        return message;
    }
}
