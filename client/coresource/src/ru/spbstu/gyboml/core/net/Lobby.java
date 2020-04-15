package ru.spbstu.gyboml.core.net;

import java.util.Optional;

import ru.spbstu.gyboml.core.Player;

/**
 * Class repersents lobby
 * Only for serialization and transfer between server and client.
 * */
public class Lobby {

    // id of related server session
    private int sessionId;

    // number of free spaces in lobby
    private int spaces;

    // lobby name
    private String name;

    // players
    private Optional<Player> firstPlayer;
    private Optional<Player> secondPlayer;

    public Lobby(String name, int sessionId, int spaces, Optional<Player> firstPlayer, Optional<Player> secondPlayer) {
        this.name = name;
        this.sessionId = sessionId;
        this.spaces = spaces;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }
}
