package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class MySqlUserDAOTests {
    private final UserDAO userDAO = new MySqlUserDAO();

    @BeforeEach
    void cleanUp() {
        try {
            userDAO.clear();
        } catch (Exception e) {
            System.out.print("Error: cleanUp failed");
        }
    }

    @Test
    void testClear() {
        try {
            userDAO.createUser(new UserData("username", "password", "email"));
            userDAO.clear();
            assert userDAO.getUser("username") == null;
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testGetUserPositive() {
        try {
            UserData user = new UserData("username", "password", "email");
            userDAO.createUser(user);
            assert Objects.equals(userDAO.getUser("username").username(), user.username()) &&
                    Objects.equals(userDAO.getUser("username").email(), user.email());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testGetUserNegative() {
        try {
            userDAO.createUser(null);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    void testCreateUserPositive() {
        try {
            UserData user = new UserData("username", "password", "email");
            userDAO.createUser(user);
            assert Objects.equals(userDAO.getUser("username").username(), user.username()) &&
                    Objects.equals(userDAO.getUser("username").email(), user.email());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testCreateUserNegative() {
        try {
            userDAO.createUser(null);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }
}
