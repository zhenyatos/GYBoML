package ru.spbstu.gyboml.server;

import java.util.Optional;
import java.util.function.Function;

import com.esotericsoftware.kryonet.Connection;

import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.SessionInfo;

public class Session {

    // players
    Optional<Player> firstPlayer;
    Optional<Player> secondPlayer;

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
     * @return true if player was added, false if there are no spaces in session.
     */
    public boolean add(final GybomlConnection connection, final String name) {
        Function<Boolean, Player> create = isTurn -> {
            Player player = new Player(name, 0, isTurn);
            player.setConnection(connection);
            return player;
        };
        if (!firstPlayer.isPresent()) {firstPlayer = Optional.of(create.apply(true)); return true;}
        else if (!secondPlayer.isPresent()) {secondPlayer = Optional.of(create.apply(false)); return true;}
        else {return false;}        
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
    public boolean remove(Connection connection) { 
        if (firstPlayer.get().getConnection().get().equals(connection)) {
            firstPlayer = secondPlayer;
            secondPlayer = Optional.empty();
            return true;
        } else if (secondPlayer.get().getConnection().get().equals(connection)) {
            secondPlayer = Optional.empty();
            return true;
        } else return false;
    }

    /**
     * Convert to session to lobby.
     * Need for transfering between server and client
     * @return
     */
    public SessionInfo toSessionInfo() {
        return new SessionInfo(this.name, this.id, spaces(), firstPlayer, secondPlayer);
    }

    //getters
    public int id() {return this.id;}
    public boolean isStarted() {return this.isStarted;}
}