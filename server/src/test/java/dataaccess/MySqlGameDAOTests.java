package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MySqlGameDAOTests {
    private final GameDAO gameDAO = new MySqlGameDAO();

    @BeforeEach
    void cleanUp() {
        try {
            gameDAO.clear();
        } catch (ResponseException e) {
            System.out.print("Error: cleanUp failed");
        }
    }

    @Test
    void testClear() {
        try {
            gameDAO.createGame(new GameData(1, null, null,
                    "Name", new ChessGame()));
            gameDAO.clear();
            assert gameDAO.getGame(1) == null;
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testGetGamePositive() {
        try {
            GameData game = new GameData(1, null, null,
                    "Name", new ChessGame());
            gameDAO.createGame(game);
            assertEquals (gameDAO.getGame(1).gameName(), game.gameName());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testGetGameNegative() {
        try {
            GameData game = new GameData(1, null, null,
                    null, new ChessGame());
            gameDAO.createGame(game);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    void testCreateGamePositive() {
        try {
            GameData game = new GameData(1, "Amy", "Meg",
                    "Name", new ChessGame());
            gameDAO.createGame(game);
            assertEquals (gameDAO.getGame(1), game);
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testCreateGameNegative() {
        try {
            GameData game = new GameData(1, "Amy", "Meg",
                    null, null);
            gameDAO.createGame(game);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    void testListGamesPositive() {
        try {
            GameData game1 = new GameData(1, "Amy", "Meg",
                    "game1", new ChessGame());
            gameDAO.createGame(game1);
            GameData game2 = new GameData(2, "Beth", "Joe",
                    "game2", new ChessGame());
            gameDAO.createGame(game2);

            GameData[] expected = new GameData[2];
            expected[0] = game1;
            expected[1] = game2;

            assert Arrays.equals(gameDAO.listGames(), expected);
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testListGamesNegative() {
        try {
            GameData game1 = new GameData(1, "Amy", "Meg",
                    "game1", new ChessGame());
            gameDAO.createGame(game1);
            GameData game2 = new GameData(0, "Beth", "Jo",
                    "game2", new ChessGame());
            gameDAO.createGame(game2);

            gameDAO.listGames();

            assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    void testUpdateGamePositive() {
        try {
            GameData game = new GameData(1, null, null, "Game",
                    new ChessGame());
            gameDAO.createGame(game);
            gameDAO.updateGame(1, ChessGame.TeamColor.WHITE, "Amy");
            assert Objects.equals(gameDAO.getGame(1), new GameData(1, "Amy", null,
                    "Game", new ChessGame()));
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testUpdateGameNegative() {
        try {
            GameData game = new GameData(1, "Jo", null, "Game",
                    new ChessGame());
            gameDAO.createGame(game);
            gameDAO.updateGame(1, null, "Amy");
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }
}
