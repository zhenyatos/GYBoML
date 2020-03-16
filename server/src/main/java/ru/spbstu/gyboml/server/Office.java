package ru.spbstu.gyboml.server;

import ru.spbstu.gyboml.core.packing.Packet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;

public abstract class Office extends Thread {

    // socket object
    protected DatagramSocket socket;

    // is server running (need for endless loop)
    protected boolean running = true;

    // enum represents office status
    public enum Status {
        OK,
        FAILED
    }

    // current status
    protected Status status = Status.OK;

    // queue of incoming messages
    protected Queue<DatagramPacket> inputQueue;

    // queue of outcoming messages
    protected Queue<DatagramPacket> outputQueue;

    public Status getStatus() {
        return this.status;
    }

    @Override
    public abstract void run();
}
