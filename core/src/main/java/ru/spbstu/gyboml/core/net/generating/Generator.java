package ru.spbstu.gyboml.core.net.generating;

import ru.spbstu.gyboml.core.net.ControllerInterface;

import java.net.InetAddress;

/**
 * Packet content generator abstract class.
 */
public abstract class Generator {
    public abstract void generate( byte[] content, InetAddress to, int port, ControllerInterface controller );
}
