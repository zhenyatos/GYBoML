package ru.spbstu.gyboml.clientnet.handling;

import ru.spbstu.gyboml.clientnet.Controller;
import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.ControllerInterface;
import ru.spbstu.gyboml.core.net.handling.Handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;

public class PlayerUpdateHandler extends Handler {
    @Override
    public void handle(byte[] content, InetAddress from, int port, ControllerInterface controllerObject) throws IOException {
        Controller controller = (Controller)controllerObject;

        ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(content));
        byte result = oin.readByte();

        if (result != 1) {
            System.out.println("[PlayerUpdateHandler] Could not update player");
            return;
        }

        try {
            Player player = (Player) oin.readObject();
            controller.setPlayer(player);
            System.out.println("[PlayerUpdateHandler] Player updated! " + (player.isMyTurn() ? "Now your turn" : "Now opponent's turn"));
        } catch (ClassNotFoundException error) {
            System.out.println("[PlayerUpdateHandler] Bad response from server: class not found");
        }
    }
}
