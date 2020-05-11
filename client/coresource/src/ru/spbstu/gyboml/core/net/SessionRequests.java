package ru.spbstu.gyboml.core.net;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.spbstu.gyboml.core.Player;

/* All requests utility class
 * Most often, requests send from client to server
 */
public class SessionRequests {

    /*
     * Register player name request.
     * Send once entered lobby menu.
     */
    @RequiredArgsConstructor
    @NoArgsConstructor
    public static class RegisterName {
        public @NonNull String playerName;
    }

    /*
     * Create lobby request.
     * After this request, server respond's with LobbyCreated
     */
    @RequiredArgsConstructor
    @NoArgsConstructor
    public static class CreateSession {
        public @NonNull String sessionName;
    }

    /*
     * Connect lobby by id request.
     */
    @RequiredArgsConstructor
    @NoArgsConstructor
    public static class ConnectSession {
        public @NonNull Integer sessionId;
    }
    
    /*
     * Get lobby list request.
     */
    public static class GetSessions {
    }
    
    /*
     * Ready message
     */
    @RequiredArgsConstructor
    @NoArgsConstructor
    public static class Ready {
        public @NonNull Player player;
    }
    
    /*
     * Exit from room (session) message
     */
    @RequiredArgsConstructor
    @NoArgsConstructor
    public static class ExitSession {
        public @NonNull Player player;
    }
}
