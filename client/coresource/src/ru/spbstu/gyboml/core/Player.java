package ru.spbstu.gyboml.core;

import com.esotericsoftware.kryonet.Connection;

import java.util.Optional;

/**
 * Class represents player in game.
 * */
public class Player {

    // is now my turn
    private boolean isTurn;

    // current number of points
    private int points;

    // kryonet connection
    private Optional<Connection> connection = Optional.empty();
    
    // player name
    private String name;

    // is player ready in session
    private boolean ready;

    /**
     * Class constructor.
     * @param initialPoints - initial number of points
     * @param isTurn - initial turn
     * */
    public Player(String name, int initialPoints, boolean isTurn) {
        points = initialPoints;
        this.isTurn = isTurn;
        this.name = name;
    }

    /**
     * Pass turn to other player.
     * @param other - link to other player
     * @return true if turn passed, false otherwise
     * */
    public boolean passTurn(Player other) {
        if (isTurn) {
            isTurn = false;
            other.isTurn = true;
            return true;
        }
        return false;
    }

    /**
     * @return true if it is my turn, false otherwise
     * */
    public boolean isMyTurn() {
        return isTurn;
    }

    public Optional<Connection> getConnection() {
        return this.connection;
    }
    
    public String name() { return this.name; }

    public void setConnection(Connection conn) {
        this.connection = Optional.of(conn);
    }
    
    public boolean ready() {
        return this.ready;
    }
    
    public void setName(String name) { this.name = name; }
    public void setReady(boolean ready) { this.ready = ready; }
}
