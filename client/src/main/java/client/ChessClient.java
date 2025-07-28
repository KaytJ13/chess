package client;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import requests.*;
import server.ServerFacade;

import java.util.Scanner;
import static ui.EscapeSequences.*;


public class ChessClient {
    private final ServerFacade facade;
    private final String serverUrl;
    private int replLoopNum = 1; // 1 is logged out (Chess Login), 2 is logged in (Chess), 3 is in game (Chess Game)
    private String authToken = null;
    private String username = null;
    private ChessGame currentGame = null;
    private ChessGame.TeamColor team = null;

    public ChessClient(String url) {
        serverUrl = url;
        facade = new ServerFacade(serverUrl);
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
                    case "draw" -> drawBoard(currentGame, team);
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
                    leave - Temporary exit command to leave game view
                    other commands coming soon!""";
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
                message.append(game.gameID()).append(". Name: ").append(game.gameName());
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
            // find a way to access the ChessGame (probably write new method) and set currentGame
            // Until then:
            currentGame = new ChessGame();
            currentGame.getBoard().resetBoard();

            return "Joined game " + gameID + "\n" + drawBoard(currentGame, color);
        } catch (ResponseException e) {
            if (e.getStatusCode() == 403) {
                // already taken (403)
                throw new ResponseException(403, "Team already filled");
            } else if (e.getStatusCode() == 400) {
                // no game with id (400)
                throw new ResponseException(400, "No game exists with that ID");
            }
            throw new ResponseException(400, "Something happened . . .");
        } catch (Exception e) {
            throw new ResponseException(400, "Game ID must be a number");
        }
    }

    private String observeGame(String[] params) throws ResponseException {
        if (params.length < 2) {
            throw new ResponseException(400, "Missing Game ID");
        }

        try {
            int gameID = Integer.parseInt(params[1]);

            // find a way to access the ChessGame (probably write new method) and set currentGame
            // Until then:
            currentGame = new ChessGame();
            currentGame.getBoard().resetBoard();

            replLoopNum = 3;
            return "Observing game " + gameID + "\n" + drawBoard(currentGame, ChessGame.TeamColor.WHITE);
            // just calls drawBoard from whatever team perspective rn. Will do more in phase 6
        } catch (Exception e) {
            throw new ResponseException(400, "Game ID must be a number");
        }
    }

    private String drawBoard(ChessGame game, ChessGame.TeamColor team) {
        assert game != null && team != null : "A ChessGame must be in progress";
        // use the current game variable to access the board and draw it

        // but until phase 6, we'll just draw a starter board
        StringBuilder out = new StringBuilder();
        char[] headers = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int[] bookends = {1, 2, 3, 4, 5, 6, 7, 8};
        ChessBoard board = game.getBoard();

        if (team == ChessGame.TeamColor.WHITE) {
            out.append(" WHITE PERSPECTIVE CHESS BOARD WILL GO HERE ");
        } else {
            out.append(" BLACK PERSPECTIVE CHESS BOARD WILL GO HERE ");
        }
        return out.toString();
    }

    private String leave() { // A temporary method for testing purposes
        replLoopNum = 2;
        currentGame = null;
        team = null;
        return "Game view exited\n";
    }
}
