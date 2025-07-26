package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import requests.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    // All the endpoints go here
    // login
    public AuthData login(LoginRequest request) throws ResponseException {
        var path = "/session";
        return makeRequest("POST", path, request, AuthData.class, null);
    }

    // logout
    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        makeRequest("DELETE", path, authToken, null, authToken);
    }

    // register user - returns AuthData
    public AuthData register(RegisterRequest request) throws ResponseException {
        var path = "/user";
        return makeRequest("POST", path, request, AuthData.class, null);
    }

    // create game - returns game ID
    public CreateGameResponse createGame(CreateGameRequest request, String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("POST", path, request, CreateGameResponse.class, authToken);
    }

    // list games - returns ListGamesResponse
    public ListGamesResponse listGames(String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("GET", path, null, ListGamesResponse.class, authToken);
    }

    // join game
    public void joinGame(JoinRequest request, String authToken) throws ResponseException {
        var path = "/game";
        makeRequest("PUT", path, request, null, authToken);
    }

    // clear
    public void clear() throws ResponseException {
        var path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken)
            throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (authToken != null) {
                http.setRequestProperty("authorization", authToken);
            }
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException, ResponseException {
        T response = null;
        var status = http.getResponseCode();

        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (status == 200) {
                    if (responseClass != null) {
                        response = new Gson().fromJson(reader, responseClass);
                    }
                } else {
                    System.out.print(reader.toString());
                    ResponseException ex = new Gson().fromJson(reader, ResponseException.class);
                    System.out.print(ex.getMessage() + "\n" + ex.toJson());
                    throw ex;
                    //FACADE ISN'T HANDLING ERRORS RIGHT AND I'M NOT GETTING THE RIGHT MESSAGES
                }

            }
        }
        return response;
    }

}
