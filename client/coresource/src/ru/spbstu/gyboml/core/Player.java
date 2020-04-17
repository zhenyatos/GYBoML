package ru.spbstu.gyboml.core;

/**
 * Class represents player in game.
 * */
public class Player {

    // is now my turn
    public boolean isTurn;

    // current number of points
    public int points;

    // player unique id
    public long id;

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

    // getters
    public boolean isMyTurn() { return isTurn; }
    public String name() { return this.name; }
    public long id() { return this.id; }
    public int sessionid() { return this.sessionId; }
    public boolean ready() {
        return this.ready;
    }

    // setters
    public void setName(String name) { this.name = name; }
    public void setReady(boolean ready) { this.ready = ready; }
    public void setId(long id) { this.id = id; }
    public void setSessionId(int id) { this.sessionId = id; }
}
