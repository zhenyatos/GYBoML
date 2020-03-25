package ru.spbstu.gyboml.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;

public class OfficeOutput extends Office {

    public OfficeOutput( DatagramSocket socket, Queue<DatagramPacket> outputQueue ) {
        this.socket = socket;
        this.outputQueue = outputQueue;
    }

    @Override
    public void run() {
        if (status == Status.FAILED) {
            return;
        }

        while (running && !isInterrupted() && status != Status.FAILED) {
            try {
                handleOutcomingPacket();
            } catch (InterruptedException error) {
                System.out.println("[OfficeOutput] Thread was interrupted");
                break;
            } catch (IOException error) {
                System.out.println(error.getMessage());
            }
        }
    }

    private synchronized void handleOutcomingPacket() throws InterruptedException, IOException {

        while (outputQueue.isEmpty()) {
            wait();
        }

        DatagramPacket datagramPacket = null;
        synchronized (outputQueue) {
            datagramPacket = outputQueue.poll();
        }

        socket.send(datagramPacket);
    }
}
