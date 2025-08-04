package client.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import exception.ResponseException;
import websocket.commands.CommandTypeAdapter;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private NotificationHandler notificationHandler;
    private Session session;
    private final Gson gson;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

//            System.out.print("DEBUG: notification handler (WSFacade) = " + notificationHandler + "\n");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //Register type adapter
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(UserGameCommand.class, new CommandTypeAdapter());
            gson = builder.create();

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(ServerMessage.class, new ServerMessageTypeAdapter());
                    Gson gson = builder.create();

                    ServerMessage notification = gson.fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | URISyntaxException | IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // Join Game/Observe (Connect)
    public void sendConnect(String authToken, int gameID, String username, ChessGame.TeamColor color)
            throws ResponseException {
        try {
            ConnectCommand command = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID,
                    username, color);
            this.session.getBasicRemote().sendText(gson.toJson(command));

//            System.out.print("DEBUG: sent connect through notification handler (" + notificationHandler + ")\n");
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    // Make Move

    // Leave
    public void sendLeave(String authToken, int gameID, String username, ChessGame.TeamColor color) throws ResponseException {
        // This doesn't work yet, obviously
        try {
            LeaveCommand command = new LeaveCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, username, color);
            this.session.getBasicRemote().sendText(gson.toJson(command));
            this.session.close();
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    // Resign

}
