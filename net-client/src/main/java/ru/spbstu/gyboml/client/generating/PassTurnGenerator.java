package ru.spbstu.gyboml.client.generating;

import ru.spbstu.gyboml.client.Controller;
import ru.spbstu.gyboml.core.net.ControllerInterface;
import ru.spbstu.gyboml.core.net.generating.Generator;
import ru.spbstu.gyboml.core.net.office.OfficeOutput;
import ru.spbstu.gyboml.core.net.packing.Packet;
import ru.spbstu.gyboml.core.net.packing.PacketType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Queue;

public class PassTurnGenerator extends Generator {

    /**
     * Generate datagram packet, put it in output queue
     * and notify output office
     * @param content - content of packet (NOT data - which is part of datagram packet)
     * @param to - receiver address
     * @param port - receiver port
     * @param controllerObject - controller object
     */
    @Override
    public void generate(byte[] content, InetAddress to, int port, ControllerInterface controllerObject) {

        Controller controller = (Controller)controllerObject;

        Queue<DatagramPacket> outputQueue = controller.getOutputQueue();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        try (ObjectOutputStream oout = new ObjectOutputStream(bout)) {
            Packet packet = new Packet(PacketType.PASS_TURN, null);
            oout.writeObject(packet);

            byte[] buffer = bout.toByteArray();
            oout.close();

            DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, buffer.length, to, port);

            // put in queue
            synchronized (outputQueue) {
                outputQueue.add(datagramPacket);
            }

            // awake office
            OfficeOutput office = controller.getOfficeOutput();
            synchronized (office) {
                controller.getOfficeOutput().notify();
            }
        } catch (IOException error) {
            System.out.println(error);
        }
    }
}
