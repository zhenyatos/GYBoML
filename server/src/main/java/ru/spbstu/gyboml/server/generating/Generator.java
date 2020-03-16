package ru.spbstu.gyboml.server.generating;

import ru.spbstu.gyboml.core.packing.PacketType;
import ru.spbstu.gyboml.server.Controller;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Queue;

/**
 * Packet content generator abstract class.
 */
public abstract class Generator {
    public abstract void generate( byte[] content, InetAddress to, Controller controller );
}
