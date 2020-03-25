package ru.spbstu.gyboml.server.handling;

import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.ControllerInterface;
import ru.spbstu.gyboml.core.net.handling.Handler;
import ru.spbstu.gyboml.server.Controller;
import ru.spbstu.gyboml.server.generating.ConnectionGenerator;

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
            controller.createFirstPlayer(from);
            System.out.printf("First player connected from %s:%s\n", from, port);

            // generate response
            generateResponseSuccess(controller.getFirstPlayer(), from, port, controller);
        } else if (controller.getSecondPlayer() == null) {
            controller.createSecondPlayer(from);
            System.out.printf("Second player connected from %s:%s\n", from, port);

            // generate response
            generateResponseSuccess(controller.getSecondPlayer(), from, port, controller);
        } else {
            System.out.printf("Attempt to connect from %s:%s\n", from, port);
            generateResponseFail(from, port, controller);
        }
    }

    private void generateResponseSuccess( Player player, InetAddress to, int port, Controller controller ) {
        // generate response
        ConnectionGenerator generator = new ConnectionGenerator();
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
        ConnectionGenerator generator = new ConnectionGenerator();
        generator.generate(new byte[]{0}, to, port, controller);
    }
}
