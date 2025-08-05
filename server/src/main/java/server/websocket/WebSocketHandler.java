package server.websocket;

import chess.ChessGame;

import chess.ChessMove;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public record AudienceAssignment(int gameID, Session session) {}

    private final HashSet<AudienceAssignment> gameAudiences = new HashSet<>();

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

    private boolean checkAuthInvalid(String authToken, Session session) {
        try {
            boolean match;
            AuthData authData = authDAO.getAuth(authToken);

            if (authData == null) {
                match = true;
            } else {
                match = Objects.equals(authData.authToken(), authToken);
            }

            if (!match) {
                sendErrorMessage("Error: unauthorized", session);
                return true;
            }

            return false;
        } catch (DataAccessException e) {
            sendErrorMessage("Error: database could not be accessed", session);

            return true;
        }
    }

    public void connect(ConnectCommand command, Session session) {
        if (checkAuthInvalid(command.getAuthToken(), session)) {
            return ;
        }

        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());

            connections.add(authData.username(), session);
            gameAudiences.add(new AudienceAssignment(command.getGameID(), session));

            GameData gameData = gameDAO.getGame(command.getGameID());
            var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            connections.sendOneUser(session, loadGame);

            String message;
            if (!authData.username().equals(gameData.blackUsername()) && !authData.username().equals(gameData.whiteUsername())) {
                message = authData.username() + " is now observing the game.";
            } else {
                String colorString = authData.username().equals(gameData.whiteUsername()) ? "white" : "black";
                message = authData.username() + " joined the game as " + colorString;
            }
            var notification = new NotificationMessage(message);

            excludeAndBroadcast(session, command.getGameID(), notification, true);

        } catch (Exception e) {
            sendErrorMessage("Error: Game not accessible", session);
        }

    }

    private void excludeAndBroadcast(Session session, int gameID, ServerMessage notification, boolean excludeSelf)
            throws IOException {
        HashSet<Session> excludeList = new HashSet<>();

        if (excludeSelf) {
            excludeList.add(session);
        }

        for (AudienceAssignment user : gameAudiences) {
            if (user.gameID != gameID) {
                excludeList.add(user.session);
            }
        }
        connections.broadcastGame(excludeList, notification);
    }

    public void leave(LeaveCommand command, Session session) {
        try {
            if (checkAuthInvalid(command.getAuthToken(), session)) {
                return ;
            }

            AuthData authData = authDAO.getAuth(command.getAuthToken());
            GameData gameData = gameDAO.getGame(command.getGameID());
            String username = authData.username();

            if (username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername())) {
                ChessGame.TeamColor color = username.equals(gameData.whiteUsername()) ? ChessGame.TeamColor.WHITE :
                        ChessGame.TeamColor.BLACK;
                gameDAO.updateGame(command.getGameID(), color, null);
            }

            String message = username + " left the game";
            var notification = new NotificationMessage(message);
            excludeAndBroadcast(session, command.getGameID(), notification, true);
            connections.remove(username);
            gameAudiences.remove(new AudienceAssignment(command.getGameID(), session));

        } catch (Throwable e) {
            return ;
        }
    }

    public void resign(ResignCommand command, Session session) {
        if (checkAuthInvalid(command.getAuthToken(), session)) {
            return ;
        }

        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            AuthData authData = authDAO.getAuth(command.getAuthToken());

            if (gameData.game().getGameOver()) {
                sendErrorMessage("Error: Game is already over", session);
                return ;
            } else if (!authData.username().equals(gameData.whiteUsername()) &&
                    !authData.username().equals(gameData.blackUsername())) {
                sendErrorMessage("Error: Observers cannot resign", session);
                return ;
            }

            gameData.game().setGameOver(true);
            gameDAO.madeMove(command.getGameID(), gameData.game());

            String message = authData.username() + " has resigned. Game over.";
            var notification = new NotificationMessage(message);
            excludeAndBroadcast(session, command.getGameID(), notification, false);

        } catch (Exception e) {
            sendErrorMessage("Error: Game not accessible", session);
        }
    }

    public void makeMove(MakeMoveCommand command, Session session) {
        if (checkAuthInvalid(command.getAuthToken(), session)) {
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
            excludeAndBroadcast(session, command.getGameID(), notification, true);

            var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            excludeAndBroadcast(session, command.getGameID(), loadGame, false);

            if (checkResponse != null) {
                var checkNotification = new NotificationMessage(checkResponse);
                excludeAndBroadcast(session, command.getGameID(), checkNotification, false);
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
