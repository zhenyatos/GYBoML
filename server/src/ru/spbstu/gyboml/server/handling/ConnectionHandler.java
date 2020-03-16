package ru.spbstu.gyboml.server.handling;

import ru.spbstu.gyboml.core.Player;
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
    public void handle(byte[] content, InetAddress from, Controller controller ) {
        if (controller.getFirstPlayer() == null) {
            controller.createFirstPlayer(from);
            System.out.printf("First player connected from %s", from);

            // generate response
            generateResponseSuccess(controller.getFirstPlayer(), from, controller);
        } else if (controller.getSecondPlayer() == null) {
            controller.createSecondPlayer(from);
            System.out.printf("Second player connected from %s", from);

            // generate response
            generateResponseSuccess(controller.getSecondPlayer(), from, controller);
        } else {
            System.out.printf("Attempt to connect from %s", from);
            generateResponseFail(from, controller);
        }
    }

    private void generateResponseSuccess( Player player, InetAddress to, Controller controller ) {
        // generate response
        ConnectionGenerator generator = new ConnectionGenerator();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (ObjectOutputStream oout = new ObjectOutputStream(bout)) {
            oout.write(1);
            oout.writeObject(player);
            generator.generate(bout.toByteArray(), to, controller);
            bout.close();
        } catch (IOException error) {
            System.out.println(error.getMessage());
        }
    }

    private void generateResponseFail( InetAddress to, Controller controller ) {
        ConnectionGenerator generator = new ConnectionGenerator();
        generator.generate(new byte[]{0}, to, controller);
    }
}
