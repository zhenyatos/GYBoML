package ru.spbstu.gyboml.core.net;

import com.esotericsoftware.kryonet.Server;

import java.util.List;

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

        public ServerError(String message) { this.message = message; }
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
        public int sessionId;
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
