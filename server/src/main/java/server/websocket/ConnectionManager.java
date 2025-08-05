package server.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageTypeAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session) {
        var connection = new Connection(username, session);
        connections.put(username, connection);

//        System.out.print("DEBUG: added a connection for " + username + "\n");
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void sendOneUser(Session session, ServerMessage notification) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ServerMessage.class, new ServerMessageTypeAdapter());
        Gson gson = builder.create();

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.session.equals(session)) {
                    c.send(gson.toJson(notification));
                }
            }
        }
    }

    public void broadcast(String excludeUser, ServerMessage notification) throws IOException {
//        System.out.print("DEBUG: entered broadcast\n");

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ServerMessage.class, new ServerMessageTypeAdapter());
        Gson gson = builder.create();

        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeUser)) {
//                    System.out.print("DEBUG: broadcasting to " + c.username + " with notification " + gson.toJson(notification) + "\n");
                    c.send(gson.toJson(notification));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }
}