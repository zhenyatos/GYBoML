package ru.spbstu.gyboml.server.handling;

import ru.spbstu.gyboml.core.packing.Packet;
import ru.spbstu.gyboml.core.packing.PacketType;
import ru.spbstu.gyboml.server.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class HandlerManager extends Thread {

    // game controller link
    private Controller controller;

    private Queue<DatagramPacket> inputQueue;

    // mapping from packet type to its handler (parser)
    static final Map<PacketType, Handler> handlerMap;

    static {
        handlerMap = new HashMap<>();
        handlerMap.put(PacketType.CONNECTION_REQUEST, new ConnectionHandler());
    }

    public HandlerManager( Controller controller ) {
        this.controller = controller;
        this.inputQueue = controller.getInputQueue();
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
                byte[] data = datagramPacket.getData();
                InetAddress address = datagramPacket.getAddress();
                int length  = datagramPacket.getLength();

                // get packet
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
                Packet packet = (Packet)in.readObject();
                PacketType type = packet.getType();
                byte[] content = packet.getContent();

                handlerMap.get(type).handle(content, address, controller);
            } catch (InterruptedException error) {
                System.out.println("[HandlerManager] Thread was interrupted");
                break;
            } catch (ClassNotFoundException | IOException error) {
                System.out.println(error.getMessage());
            }
        }
    }
}
