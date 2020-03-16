package ru.spbstu.gyboml.server.handling;

import ru.spbstu.gyboml.server.Controller;

import java.net.InetAddress;

/**
 * Packet content parser abstract class.
 */
public abstract class Handler {
    public abstract void handle( byte[] content, InetAddress from, Controller controller );
}
