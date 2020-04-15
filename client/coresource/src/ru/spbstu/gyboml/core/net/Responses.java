package ru.spbstu.gyboml.core.net;

import java.util.List;

/* All responses utility class
 * Most often, responses send from server to client
 */
public class Responses {

    /*
     * Lobby created response.
     * Sent after client's CreateLobby response.
     */
    public static class LobbyCreated {
        public int lobbyId;

        public LobbyCreated(int lobbyId) { this.lobbyId = lobbyId; }
    }
    
    /*
     * Error on server response.
     */
    public static class ServerError {
        public String message;

        public ServerError(String message) { this.message = message; }
    }
    
    /*
     * Take lobbies from server response.
     */
    public static class TakeLobbies {
        List<Lobby> lobbies;

        public TakeLobbies(List<Lobby> lobbies) {
            this.lobbies = lobbies;
        }
    }
}
