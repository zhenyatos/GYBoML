package ru.spbstu.gyboml.core.packing;

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
    CONNECTION_RESPONSE(1),

    /**
     * Change player turn message
     *
     * It's semantic depends on whom this package is delivered (client->server or server->client)
     */
    CHANGE_TURN(2);

    // identifier for serialization convenience
    private int id;

    PacketType( int id ) {
        this.id = id;
    }
}
