package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import exception.ResponseException;
import model.AuthData;
import requests.*;
import serverfacade.ServerFacade;

import java.util.Scanner;
import static ui.EscapeSequences.*;


public class ChessClient {
    private final ServerFacade facade;
    private int replLoopNum = 1; // 1 is logged out (Chess Login), 2 is logged in (Chess), 3 is in game (Chess Game)
    private String authToken = null;
    private String username = null;
    private ChessGame currentGame = null;
    private ChessGame.TeamColor team = null;

    public ChessClient(String url) {
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
                    case "redraw" -> drawBoard();
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
                    *leave - Leave game view
                    *move <START POSITION> <END POSITION> - Moves a piece
                    *resign - Forfeit the game
                    *highlight <PIECE POSITION> - Highlights legal moves for a piece""";
            // help, redraw, and highlight are local operations
            // leave, move, and resign communicate with the websocket
            // leave exits game view and sends a notification to everyone else
            // move sends an update to everyone, notifies them of the move, and redraws the board
            // resign ends gameplay, notifies everyone, and makes further moves impossible
            // help is finished
            // redraw is finished
            // highlight will need to take the possible moves
                // and basically run redraw but with the squares in the moves highlighted
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
//            throw e;
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
            // find a way to access the ChessGame (probably write new method) and set currentGame
            // Until then:
            currentGame = new ChessGame();
            currentGame.getBoard().resetBoard();

            return "Joined game " + gameID + "\n" + drawBoard();
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

            // find a way to access the ChessGame (probably write new method) and set currentGame
            // Until then:
            currentGame = new ChessGame();
            currentGame.getBoard().resetBoard();
            team = ChessGame.TeamColor.WHITE;

            replLoopNum = 3;
            return "Observing game " + gameID + "\n" + drawBoard();
            // just calls drawBoard from whatever team perspective rn. Will do more in phase 6
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseException(400, "Game ID must be a number");
        }
    }

    private String drawBoard() {
        assert currentGame != null && team != null : "A ChessGame must be in progress";
        // use the current game variable to access the board and draw it
        ChessGame game = currentGame;

        // but until phase 6, we'll just draw a starter board
        StringBuilder out = new StringBuilder();
        ChessBoard board = game.getBoard();

        if (team == ChessGame.TeamColor.WHITE) {
            for (int x = 9; x >= 0; x--) {
                for (int y = 0; y <= 9; y++) {
                    drawSquares(out, board, x, y);
                }
                out.append(RESET_BG_COLOR + "\n");
            }
        } else {
            for (int x = 0; x <= 9; x++) {
                for (int y = 9; y >= 0; y--) {
                    drawSquares(out, board, x, y);
                }
                out.append(RESET_BG_COLOR + "\n");
            }
        }
        return out.toString();
    }

    private void drawSquares(StringBuilder out, ChessBoard board, int x, int y) {
        if (x == 9 || x == 0) {
            out.append(drawHeaders(y));
        } else if (y == 9 || y == 0) {
            out.append(drawBookends(x));
        } else {
            out.append(board.getSquare(new ChessPosition(x, y)).drawSquare());
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

    private String leave() { // A temporary method for testing purposes
        replLoopNum = 2;
        currentGame = null;
        team = null;
        return "Game view exited\n";
    }
}
