package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class MySqlAuthDAOTests {
    private final AuthDAO authDAO = new MySqlAuthDAO();

    @BeforeEach
    void startAuthDAO() {
        try {
            authDAO.clear();
        } catch (Exception e) {
            System.out.print("Error: cleanUp failed");
        }
    }

    @Test
    void testClear() {
        try {
            authDAO.createAuth(new AuthData("Username", "AuThToKeNhErE"));
            authDAO.clear();
            assert authDAO.getAuth("Username") == null;
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testGetAuthPositive() {
        try {
            AuthData data = new AuthData("Username", "AuThToKeNhErE");
            authDAO.createAuth(data);
            assert Objects.equals(authDAO.getAuth("AuThToKeNhErE"), data);
        } catch (Exception e) {
            System.out.printf("Error: an exception was raised: " + e);
            assert false;
        }
    }

    @Test
    void testGetAuthNegative() {
        try {
            assert authDAO.getAuth("authToken") == null;
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    void testCreateAuthPositive() {
        try {
            AuthData data = new AuthData("Username", "AuThToKeNhErE");
            authDAO.createAuth(data);
            assert Objects.equals(authDAO.getAuth("AuThToKeNhErE"), data);
        } catch (Exception e) {
            System.out.printf("Error: an exception was raised: " + e);
            assert false;
        }
    }

    @Test
    void testCreateAuthNegative() {
        try {
            authDAO.createAuth(null);
            assert false;
        } catch (Exception e) {
            System.out.printf("Error: an exception was raised: " + e);
            assert true;
        }
    }

    @Test
    void testDeleteAuthPositive() {
        try {
            AuthData data = new AuthData("Username", "AuThToKeNhErE");
            authDAO.createAuth(data);
            authDAO.deleteAuth(data);
            assert Objects.equals(authDAO.getAuth("AuThToKeNhErE"), null);
        } catch (Exception e) {
            System.out.printf("Error: an exception was raised: " + e);
            assert false;
        }
    }

    @Test
    void testDeleteAuthNegative() {
        try {
            authDAO.deleteAuth(null);
            assert false;
        } catch (Exception e) {
            System.out.printf("Error: an exception was raised: " + e);
            assert true;
        }
    }
}
