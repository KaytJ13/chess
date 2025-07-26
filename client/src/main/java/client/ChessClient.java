package client;

import exception.ResponseException;
import model.AuthData;
import requests.LoginRequest;
import server.ServerFacade;

import java.util.Objects;
import java.util.Scanner;
import static ui.EscapeSequences.*;


public class ChessClient {
    private final ServerFacade facade;
    private final String serverUrl;
    private int replLoopNum = 1; // 1 is logged out (Chess Login), 2 is logged in (Chess), 3 is in game (Chess Game)
    private String authToken = null;
    private String username = null;

    public ChessClient(String url) {
        serverUrl = url;
        facade = new ServerFacade(serverUrl);
    }

    public void run() {
        String result = "";
        System.out.print("Welcome to Chess");
        System.out.print(help());

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
                    System.out.print(SET_TEXT_COLOR_GREEN + result);
                }
            } catch (Throwable e) {
                var message = e.toString();
                System.out.print(message);
            }
        }
        System.out.print("\nGoodbye!");
    }

    public String eval(String userInput) {
        try {
            var tokens = userInput.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            if (replLoopNum == 2) { // Post-login
                return switch (cmd) {
                    default -> help();
                };
            } else if (replLoopNum == 3) { // In Game
                return switch (cmd) {
                    case "help" -> help();
                    default -> help();
                };
            } else { // Pre-login
                return switch (cmd) {
                    case "quit" -> quit();
                    case "login" -> login(tokens);
                    default -> help();
                };
            }

        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String help() {
        if (replLoopNum == 2) {
            return """
                    Valid commands:
                    help - View valid commands
                    logout - Logout
                    create game <GAME NAME> - Create a new chess game
                    list games - View current chess games
                    play game <GAME ID> <TEAM COLOR> - Join an existing game
                    observe game - View an existing game
                    """;
        } else if (replLoopNum == 3) {
            return """
                    Valid commands:
                    help - View valid commands
                    other commands coming soon!
                    """;
        } else {
            return """
                    Valid commands:
                    help - View valid commands
                    quit - Exit the application
                    login <USERNAME> <PASSWORD> - Login an existing user
                    register <USERNAME> <PASSWORD> <EMAIL> - Register a new user
                    """;
        }
    }

    public String quit() {
        return "quit";
    }

    public String login(String[] params) throws ResponseException {
        if (params.length < 3) {
            throw new ResponseException(403, "Missing username or password");
        }
        AuthData auth = facade.login(new LoginRequest(params[1], params[2]));
        authToken = auth.authToken();
        username = auth.username();
        replLoopNum = 2;
        return "Welcome, " + username + "!";
    }
}
