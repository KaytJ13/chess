package handlers;

import services.ClearService;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    //Need to update so it returns a { } Json? Or do I?
    public void clear() {
        clearService.clear();
    }
}
