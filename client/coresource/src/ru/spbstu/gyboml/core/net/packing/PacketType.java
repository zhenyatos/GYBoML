package ru.spbstu.gyboml.core.net.packing;

/*
* Packet message type enum
* Need to parse UDP packet contents
* */
public enum PacketType {

    /**
     * Connect request message
     *
     * Can be sent only from client to server
     * */
    CONNECTION_REQUEST(0),

    /**
     * Connect response message
     *
     * Can be sent only from server to client
     * */
    PLAYER_UPDATE(1),

    /**
     * Pass  turn message.
     *
     * Can be sent only from client to server.
     */
    PASS_TURN(2);

    // identifier for serialization convenience
    private int id;

    PacketType( int id ) {
        this.id = id;
    }
}
