package ru.spbstu.gyboml.server;

import ru.spbstu.gyboml.core.Player;

public class NetPlayer {

    // player object
    private Player player;

    //kryonet connection
    private GybomlConnection connection;

    public NetPlayer(Player player, GybomlConnection connection) {
        this.player = player;
        this.connection = connection;
    }

    // setters
    public void setPlayer(Player player) { this.player = player; }
    public void setConnection(GybomlConnection connection) { this.connection = connection; }

    // getters
    public Player     getPlayer() { return this.player; }
    public GybomlConnection getConnection() { return this.connection; }
}