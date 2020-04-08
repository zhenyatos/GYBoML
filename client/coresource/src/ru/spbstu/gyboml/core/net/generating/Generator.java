package ru.spbstu.gyboml.core.net.generating;

import ru.spbstu.gyboml.core.net.ControllerInterface;

import java.net.InetAddress;

/**
 * Packet content generator abstract class.
 */
public abstract class Generator {

    static public final int port = 4445;
    /**
     * Generate datagram packet, put it in output queue
     * and notify output office
     * @param content - content of packet (NOT data - which is part of datagram packet)
     * @param to - receiver address
     * @param port - receiver port
     * @param controller - controller object
     */
    public abstract void generate( byte[] content, InetAddress to, int port, ControllerInterface controller );
}
