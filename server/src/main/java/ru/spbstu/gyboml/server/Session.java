package ru.spbstu.gyboml.server;

import java.util.Optional;
import java.util.function.Function;

import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.SessionInfo;

public class Session {

    // players
    Optional<Player> firstPlayer = Optional.empty();
    Optional<Player> secondPlayer = Optional.empty();

    private final int id;
    private static int nextAvailable = 0;
    private boolean isStarted = false;

    private String name;

    // close constructor
    private Session(int id, String name){
        this.id = id; 
        this.name = name;
    }

    // fabric method
    public static Session create(String name) {
        return new Session(Session.nextAvailable++, name);
    }

    /**
     * Add player to sesion method      
     * @return New instance of Player
     */
    public Player add(final String name) {
        Function<Boolean, Player> create = isTurn -> {
            Player player = new Player(name, 0, isTurn);
            player.name = name;
            player.points = 0;
            player.isTurn = isTurn;
            player.id = Main.nextAvailablePlayerId();
            player.sessionId = id;

            return player;
        };
        if (!firstPlayer.isPresent()) {firstPlayer = Optional.of(create.apply(true)); return firstPlayer.get();}
        else if (!secondPlayer.isPresent()) {secondPlayer = Optional.of(create.apply(false)); return secondPlayer.get();}
        else {return null;}        
    }

    /**
     * Returns number of free spaces in session
     */
    public int spaces() {
        if (!firstPlayer.isPresent()) {return 2;}
        else if (!secondPlayer.isPresent()) {return 1;}
        else {return 0;}
    }

    /**
     * Remove player by connection
     * @param connection to delete by
     * @return true if player removed, false if there is no player with this connection 
     */
    public boolean remove(long playerId) { 
        if (firstPlayer.isPresent() && firstPlayer.get().id == playerId) {
            firstPlayer = secondPlayer;
            secondPlayer = Optional.empty();
            return true;
        } else if (secondPlayer.isPresent() && secondPlayer.get().id == playerId) {
            secondPlayer = Optional.empty();
            return true;
        } else return false;
    }

    public boolean ready(long playerId, boolean isReady) {
        if (firstPlayer.isPresent() && firstPlayer.get().id == playerId) {
            firstPlayer.get().setReady(isReady);
            return true;
        } else if (secondPlayer.isPresent() && secondPlayer.get().id == playerId) {
            secondPlayer.get().setReady(isReady);
            return true;
        } else return false;
    }

    /**
     * Convert to session to lobby.
     * Need for transfering between server and client
     * @return
     */
    public SessionInfo toSessionInfo() {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.name = this.name;
        sessionInfo.sessionId = this.id;
        sessionInfo.spaces = spaces();
        sessionInfo.firstPlayer = firstPlayer.orElse(null);
        sessionInfo.secondPlayer = secondPlayer.orElse(null);

        return sessionInfo;
    }

    //getters
    public int id() {return this.id;}
    public boolean isStarted() {return this.isStarted;}
}