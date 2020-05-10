package ru.spbstu.gyboml.core.net;

import java.util.List;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
    @RequiredArgsConstructor
    @NoArgsConstructor
    public static class SessionCreated {
        public @NonNull Integer sessionId;
    }
    
    /*
     * Error on server response.
     */
    @RequiredArgsConstructor
    @NoArgsConstructor
    public static class ServerError {
        public @NonNull String message;
    }

    /**
     * Name was registered successfully
     */
    public static class NameRegistred {
    }
    
    /*
     * Take lobbies from server response.
     */
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class TakeSessions {
        public @NonNull List<SessionInfo> lobbies;
    }

    /*
     * Approvement that player connected successfully
     * Send after player's SessionConnect
     */
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class SessionConnected {
        public @NonNull Player player;

    }

    /*
     * Approvement ready status
     * Send afrer player's Ready request
     */
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class ReadyApproved {
        public @NonNull boolean isReady;
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
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class SessionStarted {
        // initial player object
        // previous player state is meaningless now and
        // needed only for session operations
        public @NonNull Player player;
    }

    /**
     * Utility message
     * Send whenever it needed
     */
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class UpdatePlayer {
        public @NonNull Player player;
    }
}
