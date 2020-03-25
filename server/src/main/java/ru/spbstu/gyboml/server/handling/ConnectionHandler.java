package ru.spbstu.gyboml.server.handling;

import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.ControllerInterface;
import ru.spbstu.gyboml.core.net.handling.Handler;
import ru.spbstu.gyboml.server.Controller;
import ru.spbstu.gyboml.server.generating.PlayerUpdateGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

/**
 * Parser handles connection requests.
 * */
public class ConnectionHandler extends Handler {

    @Override
    public void handle(byte[] content, InetAddress from, int port, ControllerInterface controllerObject ) {
        Controller controller = (Controller)controllerObject;
        if (controller.getFirstPlayer() == null) {
            controller.createFirstPlayer(from, port);
            System.out.printf("[ConnectionHandler] First player connected from %s:%s\n", from, port);

            // generate response
            generateResponseSuccess(controller.getFirstPlayer(), from, port, controller);
        } else if (controller.getSecondPlayer() == null && (from != controller.getFirstAddress() || port != controller.getFirstPort())) {
            controller.createSecondPlayer(from, port);
            System.out.printf("[ConnectionHandler] Second player connected from %s:%s\n", from, port);

            // generate response
            generateResponseSuccess(controller.getSecondPlayer(), from, port, controller);
        } else {
            System.out.printf("[ConnectionHandler] Attempt to connect from %s:%s failed\n", from, port);
            generateResponseFail(from, port, controller);
        }
    }

    private void generateResponseSuccess( Player player, InetAddress to, int port, Controller controller ) {
        // generate response
        PlayerUpdateGenerator generator = new PlayerUpdateGenerator();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (ObjectOutputStream oout = new ObjectOutputStream(bout)) {
            oout.write(1);
            oout.writeObject(player);
            generator.generate(bout.toByteArray(), to, port, controller);
            bout.close();
        } catch (IOException error) {
            System.out.println(error.getMessage());
        }
    }

    private void generateResponseFail( InetAddress to, int port, Controller controller ) {
        PlayerUpdateGenerator generator = new PlayerUpdateGenerator();
        generator.generate(new byte[]{0}, to, port, controller);
    }
}
