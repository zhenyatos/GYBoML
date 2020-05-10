package ru.spbstu.gyboml.core;

import java.awt.Event;
import java.lang.reflect.Method;

import ru.spbstu.gyboml.core.event.Events;

/**
 * Class represents player in game.
 * */
public class Player {
    public static final int MAX_SCORE = 9999;

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

    public Player(String name, int points) {
        this.name = name;
        this.points = points;
    }

    // getters
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

    public boolean spentPoints(int points) {
        if (this.points - points < 0)
            return false;
        this.points -= points;
        return true;
    }

    public boolean gotPoints(int points) {
        if (this.points + points > MAX_SCORE)
            return false;
        this.points += points;
        Method thisMethod = Events.get().find(Player.class, "gotPoints", int.class);
        Events.get().emit(this, thisMethod, this.points);
        return true;
    }
}
