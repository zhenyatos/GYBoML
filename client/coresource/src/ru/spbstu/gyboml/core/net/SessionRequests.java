package ru.spbstu.gyboml.core.net;

import ru.spbstu.gyboml.core.Player;

/* All requests utility class
 * Most often, requests send from client to server
 */
public class SessionRequests {

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
    public static class CreateSession {
        public String sessionName;
    }

    /*
     * Connect lobby by id request.
     */
    public static class ConnectSession {
        public Integer sessionId;
    }
    
    /*
     * Get lobby list request.
     */
    public static class GetSessions {
    }
    
    /*
     * Ready message
     */
    public static class Ready {
        public Player player;
    }
    
    /*
     * Exit from room (session) message
     */
    public static class ExitSession {
        public Player player;
    }
}
