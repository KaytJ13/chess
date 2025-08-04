package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class CommandTypeAdapter extends TypeAdapter<UserGameCommand> {
    @Override
    public void write(JsonWriter jsonWriter, UserGameCommand command) throws IOException {
        Gson gson = new Gson();

        switch(command.getCommandType()) {
            case MAKE_MOVE -> gson.getAdapter(MakeMoveCommand.class).write(jsonWriter, (MakeMoveCommand) command);
            case CONNECT -> gson.getAdapter(ConnectCommand.class).write(jsonWriter, (ConnectCommand) command);
            case RESIGN -> gson.getAdapter(UserGameCommand.class).write(jsonWriter, (UserGameCommand) command);
            case LEAVE -> gson.getAdapter(LeaveCommand.class).write(jsonWriter, (LeaveCommand) command);
        }
    }

    @Override
    public UserGameCommand read(JsonReader jsonReader) throws IOException {
        UserGameCommand.CommandType commandType = null;
        String authToken = null;
        int gameID = 0;
        ChessGame.TeamColor color = null;
        String username = null;
        ChessMove move = null;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "commandType" -> commandType = UserGameCommand.CommandType.valueOf(jsonReader.nextString());
                case "authToken" -> authToken = jsonReader.nextString();
                case "gameID" -> gameID = jsonReader.nextInt();
                case "color" -> color = ChessGame.TeamColor.valueOf(jsonReader.nextString());
                case "username" -> username = jsonReader.nextString();
                case "move" -> move = new Gson().getAdapter(ChessMove.class).read(jsonReader);
            }
        }

        jsonReader.endObject();

        if (commandType == null) {
            return null;
        } else {
            return switch (commandType) {
                case CONNECT -> new ConnectCommand(commandType, authToken, gameID, username, color);
                case MAKE_MOVE -> new MakeMoveCommand(commandType, authToken, gameID, username, move);
                case RESIGN -> new UserGameCommand(commandType, authToken, gameID);
                case LEAVE -> new LeaveCommand(commandType, authToken, gameID, username, color);
            };
        }
    }
}