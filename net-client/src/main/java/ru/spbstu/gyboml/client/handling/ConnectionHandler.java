package ru.spbstu.gyboml.client.handling;

import ru.spbstu.gyboml.client.Controller;
import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.ControllerInterface;
import ru.spbstu.gyboml.core.net.handling.Handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;

public class ConnectionHandler extends Handler {
    @Override
    public void handle(byte[] content, InetAddress from, int port, ControllerInterface controllerObject) throws IOException {
        Controller controller = (Controller)controllerObject;

        ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(content));
        byte result = oin.readByte();

        if (result != 1) {
            System.out.println("Could not connect to server: there are no spaces");
            return;
        }

        try {
            Player player = (Player) oin.readObject();
            controller.setPlayer(player);
        } catch (ClassNotFoundException error) {
            System.out.println("Bad response from server: class not found");
        }
    }
}
