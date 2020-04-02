package ru.spbstu.gyboml.core.net.packing;

import java.io.Serializable;

/**
 * Class represents packet that sends
 * from client to server or vice versa.
 * */
public class Packet implements Serializable {

    // type of packet
    private PacketType type;

    /* Content of packet.
       Could have any semantic meaning.
     */
    private byte[] content;

    /**
     * Class constructor.
     * @param type - packet type
     * @param content - byte array content
     * */
    public Packet(PacketType type, byte[] content ) {
        this.type = type;
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public PacketType getType() {
        return type;
    }
}
