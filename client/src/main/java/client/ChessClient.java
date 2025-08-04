package client;

import chess.*;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.AuthData;
import requests.*;
import serverfacade.ServerFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import static ui.EscapeSequences.*;


public class ChessClient implements NotificationHandler {
    private final ServerFacade facade;
    private int replLoopNum = 1; // 1 is logged out (Chess Login), 2 is logged in (Chess), 3 is in game (Chess Game)
    private String authToken = null;
    private String username = null;
    private ChessGame currentGame = null;
    private ChessGame.TeamColor team = null;
    private final String serverUrl;
    private WebSocketFacade ws;
    private int currentGameID;

    public ChessClient(String url) {
        serverUrl = url;
        facade = new ServerFacade(url);
    }

    public void run() {
        String result = "";
        System.out.print(SET_TEXT_COLOR_BLUE + WHITE_QUEEN + " Welcome to Chess " + WHITE_QUEEN + "\n");
        System.out.print(SET_TEXT_COLOR_MAGENTA + help() + "\n");

        Scanner scanner = new Scanner(System.in);
        while (!result.equals("quit")) {
            if (replLoopNum == 1) {
                System.out.print("\n" + SET_TEXT_COLOR_LIGHT_GREY + "Chess Login >>> " + SET_TEXT_COLOR_BLUE);
            } else if (replLoopNum == 2) {
                System.out.print("\n" + SET_TEXT_COLOR_LIGHT_GREY + "Chess >>> " + SET_TEXT_COLOR_BLUE);
            } else if (replLoopNum == 3) {
                System.out.print("\n" + SET_TEXT_COLOR_LIGHT_GREY + "Chess Game >>> " + SET_TEXT_COLOR_BLUE);
            }

            String line = scanner.nextLine();

            try {
                result = eval(line);
                if (!result.equals("quit")) {
                    System.out.print(SET_TEXT_COLOR_MAGENTA + "\n" + result + "\n");
                }
            } catch (Throwable e) {
                var message = e.toString();
                System.out.print(message);
            }
        }
        System.out.print("\nGoodbye!");
    }

    private String eval(String userInput) {
        try {
            var tokens = userInput.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            if (replLoopNum == 2) { // Post-login
                return switch (cmd) {
                    case "logout" -> logout();
                    case "create" -> createGame(tokens);
                    case "list" -> listGames();
                    case "join" -> joinGame(tokens);
                    case "observe" -> observeGame(tokens);
                    default -> help();
                };
            } else if (replLoopNum == 3) { // In Game
                return switch (cmd) {
                    case "leave" -> leave();
                    case "redraw" -> drawBoard(false, null);
                    case "highlight" -> highlight(tokens);
                    default -> help();
                };
            } else { // Pre-login
                return switch (cmd) {
                    case "quit" -> quit();
                    case "login" -> login(tokens);
                    case "register" -> register(tokens);
                    default -> help();
                };
            }

        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    private String help() {
        if (replLoopNum == 2) {
            return """
                    Valid commands:
                    help - View valid commands
                    logout - Logout
                    create <GAME NAME> - Create a new chess game
                    list - View current chess games
                    join <GAME ID> <TEAM COLOR (white/black)> - Join an existing game
                    observe <GAME ID> - View an existing game""";
        } else if (replLoopNum == 3) {
            return """
                    Valid commands:
                    help - View valid commands
                    redraw - Redraw the game board
                    leave - Leave the game
                    *move <START POSITION> <END POSITION> - Moves a piece
                    *resign - Forfeit the game
                    highlight <PIECE POSITION> - Highlights legal moves for a piece""";
            // leave, move, and resign communicate with the websocket
            // leave exits game view and sends a notification to everyone else
            // move sends an update to everyone, notifies them of the move, and redraws the board
            // resign ends gameplay, notifies everyone, and makes further moves impossible
            // update observe and join so that they send messages and also so currentGame gets set to the right game
        } else {
            return """
                    Valid commands:
                    help - View valid commands
                    quit - Exit the application
                    login <USERNAME> <PASSWORD> - Login an existing user
                    register <USERNAME> <PASSWORD> <EMAIL> - Register a new user""";
        }
    }

    private String quit() {
        return "quit";
    }

    private String login(String[] params) throws ResponseException {
        if (params.length < 3) {
            throw new ResponseException(400, "Missing username or password");
        }
        try {
            AuthData auth = facade.login(new LoginRequest(params[1], params[2]));
            authToken = auth.authToken();
            username = auth.username();
            replLoopNum = 2;
            return "Welcome, " + username + "!";
        } catch (ResponseException e) {
            throw new ResponseException(401, "Username or password incorrect");
        }
    }

    private String register(String[] params) throws ResponseException {
        if (params.length < 4) {
            throw new ResponseException(400, "Missing username, password, or email");
        }
        try {
            AuthData auth = facade.register(new RegisterRequest(params[1], params[2], params[3]));
            authToken = auth.authToken();
            username = auth.username();
            replLoopNum = 2;
            return "Welcome, " + username + "!";
        } catch (ResponseException e) {
            throw new ResponseException(403, "Username already taken");
        }
    }

    private String logout() throws ResponseException {
        facade.logout(authToken);
        String message = "Goodbye, " + username + "!";

        replLoopNum = 1;
        authToken = null;
        username = null;

        return message;
    }

    private String createGame(String[] params) throws ResponseException {
        if (params.length < 2) {
            throw new ResponseException(400, "Missing game name");
        }
        try {
            CreateGameResponse response = facade.createGame(new CreateGameRequest(params[1]), authToken);
            return "Created game number " + response.gameID() + ": " + params[1];
        } catch (ResponseException e) {
            throw new ResponseException(401, "Not logged in");
        }

    }

    private String listGames() throws ResponseException {
        try {
            StringBuilder message = new StringBuilder();
            ListGamesResponse response = facade.listGames(authToken);
            for (UserFriendlyGameData game : response.games()) {
                message.append("\n").append(game.gameID()).append(". Name: ").append(game.gameName());
                message.append("   White team: ").append(game.whiteUsername());
                message.append("   Black team: ").append(game.blackUsername()).append("\n");
            }
            return message.toString();
        } catch (ResponseException e) {
            throw new ResponseException(401, "Not logged in");
        }
    }

    private String joinGame(String[] params) throws ResponseException{
        if (params.length < 3) {
            throw new ResponseException(400, "Missing Game ID or team color");
        } else if (!params[2].equals("white") && !params[2].equals("black")) {
            throw new ResponseException(400, "Team color must be \"white\" or \"black\"");
        }

        try {
            ChessGame.TeamColor color = params[2].equals("white") ? ChessGame.TeamColor.WHITE :
                    ChessGame.TeamColor.BLACK;
            int gameID = Integer.parseInt(params[1]);
            facade.joinGame(new JoinRequest(color, gameID), authToken);

            replLoopNum = 3;
            team = color;
            currentGameID = gameID;

            ws = new WebSocketFacade(serverUrl, this);
            ws.sendConnect(authToken, gameID, username, color);

            return "Joined game " + gameID + "\n";

        } catch (ResponseException e) {
            if (e.getStatusCode() == 403) {
                // already taken (403)
                throw new ResponseException(403, "Team already filled");
            } else {
                // no game with id (400)
                throw new ResponseException(400, "No game exists with that ID");
            }
        } catch (Exception e) {
            throw new ResponseException(400, "Game ID must be a number");
        }
    }

    private String observeGame(String[] params) throws ResponseException {
        if (params.length < 2) {
            throw new ResponseException(400, "Missing Game ID");
        }

        try {
            ListGamesResponse gamesList = facade.listGames(authToken);
            int gameID = Integer.parseInt(params[1]);
            boolean found = false;

            for (UserFriendlyGameData game : gamesList.games()) {
                if (game.gameID() == gameID) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ResponseException(400, "No game with that Game ID");
            }

            team = ChessGame.TeamColor.WHITE; // This is purely for drawing the board
            replLoopNum = 3;
            currentGameID = gameID;

            ws = new WebSocketFacade(serverUrl, this);
            ws.sendConnect(authToken, gameID, username, null);
            //Not finding current game and not updating

            return "Observing game " + gameID + "\n";
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseException(400, "Game ID must be a number");
        }
    }

    private String drawBoard(boolean highlight, ChessPosition startPosition) {
        assert currentGame != null && team != null : "A ChessGame must be in progress";
        assert !highlight || startPosition != null;

        Collection<ChessPosition> highlightedSquares = highlight ? highlightSquares(startPosition) : null;

        StringBuilder out = new StringBuilder();
        ChessBoard board = currentGame.getBoard();

        if (team == ChessGame.TeamColor.WHITE) {
            for (int x = 9; x >= 0; x--) {
                for (int y = 0; y <= 9; y++) {
                    drawSquares(out, board, x, y, highlightedSquares);
                }
                out.append(RESET_BG_COLOR + "\n");
            }
        } else {
            for (int x = 0; x <= 9; x++) {
                for (int y = 9; y >= 0; y--) {
                    drawSquares(out, board, x, y, highlightedSquares);
                }
                out.append(RESET_BG_COLOR + "\n");
            }
        }
        return out.toString();
    }

    private Collection<ChessPosition> highlightSquares(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = currentGame.validMoves(new ChessPosition(startPosition.getRow(),
                startPosition.getColumn()));

        if (validMoves == null) {
            return null;
        }

        Collection<ChessPosition> highlightedSquares = new HashSet<>();
        for (ChessMove move : validMoves) {
            if (move.getStartPosition().getRow() == startPosition.getRow() &&
                    move.getStartPosition().getColumn() == startPosition.getColumn()) {
                highlightedSquares.add(move.getEndPosition());
            }
        }
        return highlightedSquares;
    }

    private void drawSquares(StringBuilder out, ChessBoard board, int x, int y,
                             Collection<ChessPosition> highlightedSquares) {
        boolean validMove = false;
        if (highlightedSquares != null) {
            for (ChessPosition position : highlightedSquares) {
                if (position.getRow() == x && position.getColumn() == y) {
                    validMove = true;
                    break;
                }
            }
        }

        if (x == 9 || x == 0) {
            out.append(drawHeaders(y));
        } else if (y == 9 || y == 0) {
            out.append(drawBookends(x));
        } else {
            out.append(board.getSquare(new ChessPosition(x, y)).drawSquare(validMove));
        }
    }

    private String drawHeaders(int yPos) {
        char[] headers = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        String out = SET_BG_COLOR_WHITE;
        if (yPos == 0 || yPos == 9) {
            out += SET_TEXT_COLOR_DARK_GREY + EMPTY;
        } else {
            out += SET_TEXT_COLOR_BLACK + " " + headers[yPos-1] + " ";
        }
        return out;
    }

    private String drawBookends(int xPos) {
        int[] bookends = {0, 1, 2, 3, 4, 5, 6, 7, 8, 0};
        return SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + " " + bookends[xPos] + " ";
    }

    private String highlight(String[] params) throws ResponseException {
        if (params.length < 2) {
            throw new ResponseException(400, "Missing position");
        } else if (currentGame.getGameOver()) {
            throw new ResponseException(400, "Game is over");
        }

        int[] validY = {1, 2, 3, 4, 5, 6, 7, 8};
        String validX = "abcdefgh";
        char[] positioning = params[1].toCharArray();

        if (positioning.length < 2) {
            throw new ResponseException(400, "Position must be <COLUMN><ROW>");
        }

        try {
            int row = positioning[1] - '0';
            int column = validY[validX.indexOf(positioning[0])];

            ChessPosition startPosition = new ChessPosition(row, column);
            return drawBoard(true, startPosition);
        } catch (Exception ex){
            throw new ResponseException(400, ex.getMessage());
        } catch (Throwable e) {
            throw new ResponseException(400, "Column must be a letter a-h and row must be a number 1-8");
        }
    }

    private String leave() throws ResponseException { // Still the Phase 5 version
        ws.sendLeave(authToken, currentGameID, username, team);

        replLoopNum = 2;
        currentGame = null;
        team = null;
        currentGameID = 0;

        return "Game view exited\n";
    }

    public void updateGame(ChessGame game) {
        currentGame = game;
    }

    public void notify(ServerMessage notification) {
//        System.out.print(SET_TEXT_COLOR_BLUE + "DEBUG: caught a notification in notify: " + notification + "\n");
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            System.out.print(SET_TEXT_COLOR_BLUE + ((NotificationMessage) notification).getMessage() + "\n");
        } else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            System.out.print(SET_TEXT_COLOR_BLUE + ((ErrorMessage) notification).getErrorMessage() + "\n");
        } else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            updateGame(((LoadGameMessage) notification).getGame());
//            System.out.print(SET_TEXT_COLOR_BLUE + "DEBUG: current game reset = " + (currentGame != null) + "\n");
            System.out.print(SET_TEXT_COLOR_BLUE + "\n" + drawBoard(false, null) + "\n");
        }
        System.out.print("\n" + SET_TEXT_COLOR_LIGHT_GREY + "Chess Game >>> " + SET_TEXT_COLOR_BLUE);
    }
}
