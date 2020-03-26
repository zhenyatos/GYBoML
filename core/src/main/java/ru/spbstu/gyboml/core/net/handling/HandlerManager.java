package ru.spbstu.gyboml.core.net.handling;

import ru.spbstu.gyboml.core.net.ControllerInterface;
import ru.spbstu.gyboml.core.net.packing.Packet;
import ru.spbstu.gyboml.core.net.packing.PacketType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class HandlerManager extends Thread {

    // game controller link
    private ControllerInterface controller;

    private Queue<DatagramPacket> inputQueue;

    // mapping from packet type to its handler (parser)
    Map<PacketType, Handler> handlerMap;

    public HandlerManager( ControllerInterface controller ) {
        this.controller = controller;
        this.inputQueue = controller.getInputQueue();
        this.handlerMap = new HashMap<>();
    }

    public void putHandler( PacketType type, Handler handler ) {
        handlerMap.put(type, handler);
    }

    @Override
    public synchronized void run() {
        while (!isInterrupted()) {
            DatagramPacket datagramPacket = null;
            try {
                while (inputQueue.isEmpty())
                    wait();

                synchronized (inputQueue) {
                    datagramPacket = inputQueue.poll();
                }

                // get datagram packet info
                int length  = datagramPacket.getLength();
                byte[] data = Arrays.copyOf(datagramPacket.getData(), length);
                InetAddress address = datagramPacket.getAddress();
                int port = datagramPacket.getPort();

                // get packet
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
                Packet packet = (Packet)in.readObject();
                PacketType type = packet.getType();
                byte[] content = packet.getContent();

                handlerMap.get(type).handle(content, address, port, controller);
            } catch (InterruptedException error) {
                System.out.println("[HandlerManager] Thread was interrupted");
                break;
            } catch (ClassNotFoundException | IOException error) {
                System.out.println(error.getMessage());
            }
        }
    }
}
