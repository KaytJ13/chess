package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
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
    public void onMessage(Session session, String message) throws IOException, ResponseException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class); // I'll actually need a type adapter here
        switch (command.getCommandType()) {
            case CONNECT -> connect(((ConnectCommand) command), session);
            case LEAVE -> leave();
            case RESIGN -> resign();
            case MAKE_MOVE -> makeMove();
        }
    }

    public void connect(ConnectCommand command, Session session) throws IOException, ResponseException {
        connections.add(command.getUsername(), session);
        String message;
        if (command.getColor() == null) {
            message = command.getUsername() + " is now observing the game.";
        } else {
            String colorString = command.getColor() == ChessGame.TeamColor.WHITE ? "white" : "black";
            message = command.getUsername() + " joined the game as " + colorString;
        }
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getUsername(), notification);


        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            connections.broadcast("", loadGame);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Game not accessible");
        }
    }

    public void leave() {

    }

    public void resign() {

    }

    public void makeMove() {

    }
}
