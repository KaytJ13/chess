package client.websocket;

import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage notification);
}