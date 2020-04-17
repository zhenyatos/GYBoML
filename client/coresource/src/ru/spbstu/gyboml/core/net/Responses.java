package ru.spbstu.gyboml.core.net;

import java.util.List;
import ru.spbstu.gyboml.core.Player;

/* All responses utility class
 * Most often, responses send from server to client
 */
public class Responses {

    /*
     * Lobby created response.
     * Sent after client's CreateLobby response.
     */
    public static class SessionCreated {
        public int sessionId;
    }
    
    /*
     * Error on server response.
     */
    public static class ServerError {
        public String message;

        public String message() { return this.message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /*
     * Take lobbies from server response.
     */
    public static class TakeSessions {
        public List<SessionInfo> lobbies;
    }

    /*
     * Approvement that player connected successfully
     * Send after player's SessionConnect
     */
    public static class SessionConnected {
        public Player player;
    }

    /*
     * Approvement ready status
     * Send afrer player's Ready request
     */
    public static class ReadyApproved {
        public boolean isReady;
    }

    /*
     * Approvement player exit from session
     * Send afrer player's ExitSession request
     */
    public static class SessionExited {
    }
}
