package ru.spbstu.gyboml.core.net;

import java.util.List;
import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.PlayerType;

/* All responses utility class
 * Most often, responses send from server to client
 */
public class SessionResponses {

    /*
     * Lobby created response.
     * Sent after client's CreateLobby response.
     */
    public static class SessionCreated {
        public Integer sessionId;
        public SessionCreated(){}
        public SessionCreated(Integer sessionId){ this.sessionId = sessionId  ; }
    }
    
    /*
     * Error on server response.
     */
    public static class ServerError {
        public String message;

        public String message() { return this.message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * Name was registered successfully
     */
    public static class NameRegistred {
    }
    
    /*
     * Take lobbies from server response.
     */
    public static class TakeSessions {
        public List<SessionInfo> lobbies;

        public TakeSessions(){}
        public TakeSessions(List<SessionInfo> lobbies) {
            this.lobbies = lobbies;
        }
    }

    /*
     * Approvement that player connected successfully
     * Send after player's SessionConnect
     */
    public static class SessionConnected {
        public Player player;

        public SessionConnected(){}
        public SessionConnected(Player player){ this.player = player; }
    }

    /*
     * Approvement ready status
     * Send afrer player's Ready request
     */
    public static class ReadyApproved {
        public boolean isReady;

        public ReadyApproved(){}
        public ReadyApproved(boolean isReady){ this.isReady = isReady; }
    }

    /*
     * Approvement player exit from session
     * Send afrer player's ExitSession request
     */
    public static class SessionExited {
    }

    /*
     * Game started message
     * Sent after both players connected to session and ready
     */
    public static class SessionStarted {
        // initial player object
        // previous player state is meaningless now and
        // needed only for session operations
        public Player player;

        public SessionStarted(){}
        public SessionStarted(Player player){ this.player = player; }
    }

    /**
     * Utility message
     * Send whenever it needed
     */
    public static class UpdatePlayer {
        public Player player;

        public UpdatePlayer(){}
        public UpdatePlayer(Player player){ this.player = player; }
    }
}
