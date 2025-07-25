package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.RegisterRequest;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    // All the endpoints go here
    // login
    public AuthData login(LoginRequest request) throws ResponseException {
        var path = "/session";
        return makeRequest("POST", path, request, AuthData.class);
    }

    // logout
    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        makeRequest("DELETE", path, authToken, null);
    }

    // register user - returns AuthData
    public AuthData register(RegisterRequest request) throws ResponseException {
        var path = "/user";
        return makeRequest("POST", path, request, AuthData.class);
    }

    // create game - returns game ID
    // list games - returns ListGamesResponse
    // join game
    public void joinGame(JoinRequest request) throws ResponseException {
        var path = "/game";
        makeRequest("PUT", path, request, null);
    }

    // clear
    public void clear() throws ResponseException {
        var path = "/db";
        makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass)
            throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
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

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw exceptionFromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    private ResponseException exceptionFromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        var status = ((Double)map.get("status")).intValue();
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }
}
