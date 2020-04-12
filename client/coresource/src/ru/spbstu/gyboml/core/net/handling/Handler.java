package ru.spbstu.gyboml.core.net.handling;

import ru.spbstu.gyboml.core.net.ControllerInterface;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Packet content parser abstract class.
 */
public abstract class Handler {
    public abstract void handle( byte[] content, InetAddress from, int port, ControllerInterface controller ) throws IOException;
}
