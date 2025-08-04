package websocket.commands;

public class ResignCommand extends UserGameCommand {
    String username;

    public ResignCommand(CommandType commandType, String authToken, Integer gameID, String username) {
        super(commandType, authToken, gameID);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
