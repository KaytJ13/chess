package server.websocket;

import chess.ChessGame;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(UserGameCommand.class, new CommandTypeAdapter());
        Gson gson = builder.create();

        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(((ConnectCommand) command), session);
            case LEAVE -> leave(((LeaveCommand) command), session);
            case RESIGN -> resign(((ResignCommand) command), session);
            case MAKE_MOVE -> makeMove(((MakeMoveCommand) command), session);
        }
    }

    private boolean matchAuth(String authToken, Session session) {
        try {
            boolean match;
            AuthData authData = authDAO.getAuth(authToken);

            if (authData == null) {
                match = false;
            } else {
                match = Objects.equals(authData.authToken(), authToken);
            }

            if (!match) {
                sendErrorMessage("Error: unauthorized", session);
                return false;
            }

            return true;
        } catch (DataAccessException e) {
            sendErrorMessage("Error: database could not be accessed", session);

            return false;
        }
    }

    public void connect(ConnectCommand command, Session session) {
        if (!matchAuth(command.getAuthToken(), session)) {
            return ;
        }

        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());

            connections.add(authData.username(), session);

            GameData gameData = gameDAO.getGame(command.getGameID());
            var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            connections.sendOneUser(session, loadGame);

            String message;
            if (!gameData.blackUsername().equals(authData.username()) && !gameData.whiteUsername().equals(authData.username())) {
                message = authData.username() + " is now observing the game.";
            } else {
                String colorString = gameData.whiteUsername().equals(authData.username()) ? "white" : "black";
                message = authData.username() + " joined the game as " + colorString;
            }
            var notification = new NotificationMessage(message);
            connections.broadcast(authData.username(), notification);

        } catch (Throwable e) {
            sendErrorMessage("Error: Game not accessible", session);
        }

    }

    public void leave(LeaveCommand command, Session session) {
        try {
            if (!matchAuth(command.getAuthToken(), session)) {
                return ;
            } else if (!session.isOpen()) {
                return ;
            }

            AuthData authData = authDAO.getAuth(command.getAuthToken());
            GameData gameData = gameDAO.getGame(command.getGameID());

            if (gameData.whiteUsername().equals(authData.username()) ||
                    gameData.blackUsername().equals(authData.username())) {
                ChessGame.TeamColor color = gameData.whiteUsername().equals(authData.username()) ?
                        ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                gameDAO.updateGame(command.getGameID(), color, null);
            }

            connections.remove(authData.username());
            String message = authData.username() + " left the game";
            var notification = new NotificationMessage(message);
            connections.broadcast(authData.username(), notification);

        } catch (Throwable e) {
            return ;
        }
    }

    public void resign(ResignCommand command, Session session) {
        if (!matchAuth(command.getAuthToken(), session)) {
            return ;
        }

        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            AuthData authData = authDAO.getAuth(command.getAuthToken());

            gameData.game().setGameOver(true);
            var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            connections.broadcast("", loadGame);

            String message = authData.username() + " has resigned. Game over.";
            var notification = new NotificationMessage(message);
            connections.broadcast("", notification);

        } catch (Exception e) {
            sendErrorMessage("Error: Game not accessible", session);
        }
    }

    public void makeMove(MakeMoveCommand command, Session session) {
        if (!matchAuth(command.getAuthToken(), session)) {
            return ;
        }

        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            AuthData authData = authDAO.getAuth(command.getAuthToken());

            if (gameData == null) {
                sendErrorMessage("Error: invalid game ID", session);
            }

            if (!checkCanMove(authData, gameData, session)) {
                return ;
            }

            gameData.game().makeMove(command.getMove());
            String checkResponse = checkGameOver(gameData);
            gameDAO.madeMove(command.getGameID(), gameData.game());

            String message = authData.username() + " moved " + command.getMove().getStartPosition() + " to " +
                    command.getMove().getEndPosition();
            var notification = new NotificationMessage(message);
            connections.broadcast(authData.username(), notification);

            var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            connections.broadcast("", loadGame);

            if (checkResponse != null) {
                var checkNotification = new NotificationMessage(checkResponse);
                connections.broadcast("", checkNotification);
            }

        } catch (DataAccessException | IOException e) {
            sendErrorMessage("Error: Game not accessible", session);
        } catch (InvalidMoveException e) {
            sendErrorMessage("Error: Move not legal", session);
        } catch (Throwable e) {
            sendErrorMessage("Error: could not make move", session);
        }
    }

    private boolean checkCanMove(AuthData authData, GameData gameData, Session session) {
        try {
            String username = authData.username();
            String whiteUser = gameData.whiteUsername();
            String blackUser = gameData.blackUsername();

            if (!username.equals(whiteUser) && !username.equals(blackUser)) {
                sendErrorMessage("Error: Observers cannot make moves", session);
                return false;
            }

            ChessGame.TeamColor color = username.equals(whiteUser) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            if (gameData.game().getGameOver()) {
                sendErrorMessage("Error: Game is over, no more moves can be made", session);
                return false;
            } else if (color != gameData.game().getTeamTurn()) {
                sendErrorMessage("Error: It's not your turn", session);
                return false;
            }

            return true;
        } catch (Throwable e) {
            sendErrorMessage("Error: Some other error happened", session);
            return false;
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

    private void sendErrorMessage(String message, Session session) {
        try {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    message);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(ServerMessage.class, new ServerMessageTypeAdapter());
            Gson gson = builder.create();

//            connections.sendOneUser(session, errorMessage);
            session.getRemote().sendString(gson.toJson(errorMessage));
        } catch (IOException ex) {
            return ;
        }
    }
}
