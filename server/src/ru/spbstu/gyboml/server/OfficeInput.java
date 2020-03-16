package ru.spbstu.gyboml.server;

import ru.spbstu.gyboml.server.handling.HandlerManager;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;

/**
 * Class handles incoming and outcoming packets.
 * Works in separate thread since it blocking.
 */
public class OfficeInput extends Office {

    private static final int bufSize = 200;
    private HandlerManager handlerManager;

    /**
     * Class constructor.
     * @param socket - socket to read from
     * @param handlerManager - handler manager link
     * @param inputQueue - queue of incoming packages
     */
    public OfficeInput(DatagramSocket socket, HandlerManager handlerManager, Queue<DatagramPacket> inputQueue ) {
        this.socket = socket;
        this.inputQueue = inputQueue;
        this.handlerManager = handlerManager;
    }

    @Override
    public void run() {
        if (status == Status.FAILED) {
            return;
        }

        while (running && !isInterrupted() && status != Status.FAILED) {
            handleIncomingPacket();
        }
    }

    private void handleIncomingPacket() {
        byte[] buf = new byte[bufSize];
        DatagramPacket datagramPacket = new DatagramPacket(buf, bufSize);

        try {
            // listen socket
            socket.receive(datagramPacket);

            System.out.printf("[OfficeInput] Received packet from %s\n", datagramPacket.getAddress());

            // insert packet in queue
            synchronized (inputQueue) {
                inputQueue.add(datagramPacket);
            }

            synchronized (handlerManager) {
                handlerManager.notify();
            }

        } catch (IOException error) {
            status = Status.FAILED;
            System.out.println("[OfficeInput] Socket receive failed / interrupted");
        }
    }
}
