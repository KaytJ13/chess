package dataaccess;

import model.GameData;

public interface GameDAO {
    //Create, read, update, delete

    public void clear();

    public GameData getGame(int GameID);

    public GameData createGame(String gameName);

    public GameData[] listGames(String playerColor, String username); //playerColor will probably actually be an enum somewhere, not a string

}
