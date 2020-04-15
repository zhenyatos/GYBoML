package ru.spbstu.gyboml.core.net;

import java.util.Optional;

import ru.spbstu.gyboml.core.Player;

/**
 * Class repersents one session lobby menu
 * Only for serialization and transfer between server and client.
 * */
public class SessionInfo {

    // id of related server session
    private int sessionId;

    // number of free spaces in lobby
    private int spaces;

    // lobby name
    private String name;

    // players
    private Optional<Player> firstPlayer;
    private Optional<Player> secondPlayer;

    public SessionInfo(String name, int sessionId, int spaces, Optional<Player> firstPlayer, Optional<Player> secondPlayer) {
        this.name = name;
        this.sessionId = sessionId;
        this.spaces = spaces;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    public String name() {return name;}
    public int sessionId() {return sessionId;}
    public int spaces() {return spaces;}
    public Optional<Player> firstPlayer() {return firstPlayer;}
    public Optional<Player> secondPlayer() {return secondPlayer;}

}
