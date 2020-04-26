package ru.spbstu.gyboml.core;

/**
 * Class represents player in game.
 * */
public class Player {

    // is now my turn
    public boolean isTurn;

    // current number of points
    public int points;

    // player type
    public PlayerType type;

    // session unique id
    public int sessionId;

    // player name
    public String name;

    // is player ready in session
    public boolean ready;

    // default ctor
    public Player(){}

    /**
     * Class constructor.
     * @param name - name of player
     * @param type - type of player (first or second)
     */
    public Player(String name, PlayerType type) {
        this.name = name;
        this.type = type;
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

    // getters
    public boolean isMyTurn() { return isTurn; }
    public String name() { return this.name; }
    public int sessionid() { return this.sessionId; }
    public boolean ready() {
        return this.ready;
    }
    public PlayerType type() { return this.type; }

    // setters
    public void setName(String name) { this.name = name; }
    public void setReady(boolean ready) { this.ready = ready; }
    public void setSessionId(int id) { this.sessionId = id; }
    public void setType(PlayerType type) { this.type = type; }
}
