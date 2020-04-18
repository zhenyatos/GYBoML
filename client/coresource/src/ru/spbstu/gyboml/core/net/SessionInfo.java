package ru.spbstu.gyboml.core.net;

import ru.spbstu.gyboml.core.Player;

/**
 * Class repersents one session lobby menu
 * Only for serialization and transfer between server and client.
 * */
public class SessionInfo {

    // id of related server session
    public Integer sessionId;

    // number of free spaces in lobby
    public Integer spaces;

    // lobby name
    public String name;

    // players
    public Player firstPlayer;
    public Player secondPlayer;
}
