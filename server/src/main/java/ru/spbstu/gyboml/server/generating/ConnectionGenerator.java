package ru.spbstu.gyboml.server.generating;

import ru.spbstu.gyboml.core.net.ControllerInterface;
import ru.spbstu.gyboml.core.net.generating.Generator;
import ru.spbstu.gyboml.core.net.packing.Packet;
import ru.spbstu.gyboml.core.net.packing.PacketType;
import ru.spbstu.gyboml.server.Controller;
import ru.spbstu.gyboml.core.net.office.OfficeOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Queue;

public class PlayerUpdateGenerator extends Generator {

    @Override
    public void generate( byte[] content, InetAddress to, int port, ControllerInterface controllerObject ) {
        Controller controller = (Controller)controllerObject;
        Queue<DatagramPacket> outputQueue = controller.getOutputQueue();

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (ObjectOutputStream oout = new ObjectOutputStream(bout)) {

            // serialize data
            Packet packet = new Packet(PacketType.PLAYER_UPDATE, content);
            oout.writeObject(packet);
            byte[] buffer = bout.toByteArray();
            oout.close();

            // create datagram packet
            DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, buffer.length, to, port);

            // put it in queue
            synchronized (outputQueue) {
                outputQueue.add(datagramPacket);
            }

            // awake office
            OfficeOutput office = controller.getOfficeOutput();
            synchronized (office) {
                controller.getOfficeOutput().notify();
            }
        } catch (IOException error) {
            System.out.println(error.getMessage());
        }
    }
}
