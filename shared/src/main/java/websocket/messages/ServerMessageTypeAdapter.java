package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ServerMessageTypeAdapter extends TypeAdapter<ServerMessage> {
    @Override
    public void write(JsonWriter jsonWriter, ServerMessage message) throws IOException {
        Gson gson = new Gson();

        switch(message.getServerMessageType()) {
            case ERROR -> gson.getAdapter(ErrorMessage.class).write(jsonWriter, (ErrorMessage) message);
            case LOAD_GAME -> gson.getAdapter(LoadGameMessage.class).write(jsonWriter, (LoadGameMessage) message);
            case NOTIFICATION -> gson.getAdapter(NotificationMessage.class).write(jsonWriter,
                    (NotificationMessage) message);
        }
    }

    @Override
    public ServerMessage read(JsonReader jsonReader) throws IOException {
        ServerMessage.ServerMessageType serverMessageType = null;
        String message = null;
        String errorMessage = null;
        ChessGame game = null;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "serverMessageType" -> serverMessageType = ServerMessage.ServerMessageType.valueOf(
                        jsonReader.nextString());
                case "message" -> message = jsonReader.nextString();
                case "errorMessage" -> errorMessage = jsonReader.nextString();
                case "game" -> game = new Gson().getAdapter(ChessGame.class).read(jsonReader);
            }
        }
//        System.out.print("DEBUG: parsed game as " + game + "\n");

        jsonReader.endObject();

        if (serverMessageType == null) {
            return null;
        } else {
            return switch (serverMessageType) {
                case ERROR -> new ErrorMessage(serverMessageType, errorMessage);
                case LOAD_GAME -> new LoadGameMessage(serverMessageType, game);
                case NOTIFICATION -> new NotificationMessage(message);
            };
        }
    }
}