package websocket.commands;

public class LeaveCommand extends UserGameCommand {
    private final String username;

    public LeaveCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, String username) {
        super(commandType, authToken, gameID);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
