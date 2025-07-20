package handlers;

import exception.ResponseException;
import services.ClearService;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear() throws ResponseException {
        clearService.clear();
    }
}
