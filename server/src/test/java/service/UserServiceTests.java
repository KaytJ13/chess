package service;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.RegisterRequest;
import services.UserService;

import java.util.Objects;

public class UserServiceTests {

    @Test
    void registerPositive() {
        UserDAO userDAO = new MemoryUserDAO();
        UserService userService = new UserService(userDAO, new MemoryAuthDAO());

        try {
            userService.register(new RegisterRequest("username1", "password",
                    "email"));
            UserDAO userDAO2 = new MemoryUserDAO();
            userDAO2.createUser(new UserData("username1", "password", "email"));
            UserData userData1 = userDAO.getUser("username1");
            UserData userData2 = userDAO2.getUser("username1");
            assert Objects.equals(userData1.username(), userData2.username()) &&
                    Objects.equals(userData1.email(), userData2.email());
        } catch (ResponseException | DataAccessException e) {
            assert false;
        }
    }

    @Test
    void registerNegative() {
        UserDAO userDAO = new MemoryUserDAO();
        UserService userService = new UserService(userDAO, new MemoryAuthDAO());

        try {
            userService.register(new RegisterRequest("username1", null,
                    "email"));
        } catch (ResponseException e) {
            assert true;
        } catch (DataAccessException e) {
            assert false;
        }
    }

    @Test
    void loginPositive() {
        UserDAO userDAO = new MemoryUserDAO();
        UserService userService = new UserService(userDAO, new MemoryAuthDAO());

        try {
            userDAO.createUser(new UserData("username1", "password", "email"));
            UserDAO userDAO2 = new MemoryUserDAO();
            userDAO2.createUser(new UserData("username1", "password", "email"));

            AuthData authData = userService.login(new LoginRequest("username1",
                    "password"));

            assert Objects.nonNull(authData);
        } catch (ResponseException | DataAccessException e) {
            assert false;
        }
    }

    @Test
    void loginNegative() {
        UserDAO userDAO = new MemoryUserDAO();
        UserService userService = new UserService(userDAO, new MemoryAuthDAO());

        try {
            userDAO.createUser(new UserData("username1", "password", "email"));
            UserDAO userDAO2 = new MemoryUserDAO();
            userDAO2.createUser(new UserData("username1", "password", "email"));

            userService.login(new LoginRequest("username1", "pass"));
        } catch (ResponseException e) {
            assert true;
        } catch (DataAccessException e) {
            assert false;
        }
    }

    @Test
    void logoutPositive() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);

        try {
            userDAO.createUser(new UserData("username1", "password", "email"));
            authDAO.createAuth(new AuthData("username1", "auth1"));
            AuthDAO authDAO2 = new MemoryAuthDAO();

            userService.logout("auth1");

            assert Objects.equals(authDAO2, authDAO);
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void logoutNegative() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);

        try {
            userService.logout("auth1");
        } catch (Exception e) {
            assert true;
        }
    }
}
