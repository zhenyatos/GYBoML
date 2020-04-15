package ru.spbstu.gyboml.core.net;

/* All requests utility class
 * Most often, requests send from client to server
 */
public class Requests {

    /*
     * Register player name request.
     * Send once entered lobby menu.
     */
    public static class RegisterName {
        public String playerName;
    }

    /*
     * Create lobby request.
     * After this request, server respond's with LobbyCreated
     */
    public static class CreateLobby {
        public String lobbyName;
    }

    /*
     * Connect lobby by id request.
     */
    public static class ConnectLobby {
        public int lobbyId;
    }
    
    /*
     * Get lobby list request.
     */
    public static class GetLobbies {
    }
}
