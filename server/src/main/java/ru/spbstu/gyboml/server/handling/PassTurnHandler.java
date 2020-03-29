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

public class PassTurnHandler extends Handler {

    @Override
    public void handle(byte[] content, InetAddress from, int port, ControllerInterface controllerObject ) {
        Controller controller = (Controller)controllerObject;

        Player firstPlayer = controller.getFirstPlayer();
        Player secondPlayer = controller.getSecondPlayer();

        if (firstPlayer == null || secondPlayer == null) {
            System.out.println("[PassTurnHandler] Not enough players to pass turn");
            return;
        }

        byte success = 1;
        if (from.equals(controller.getFirstAddress()) && port == controller.getFirstPort()) {
            firstPlayer.passTurn(secondPlayer);
            System.out.println("[PassTurnHandler] Pass turned from first player to second player");
        } else if (from.equals(controller.getSecondAddress()) && port == controller.getSecondPort()) {
            secondPlayer.passTurn(firstPlayer);
            System.out.println("[PassTurnHandler] Pass turned from second player to first player");
        } else success = 0;

        generatePlayersResponse(controller,
                (byte)(success * ((firstPlayer == null || secondPlayer == null) ? 0 : 1)));

    }

    private void generatePlayersResponse( Controller controller, byte success ) {
        // kystyn commented it out because it is not used
        //Player firstPlayer = controller.getFirstPlayer();
        //Player secondPlayer = controller.getSecondPlayer();

        generatePlayerResponse(controller, controller.getFirstPlayer(), controller.getFirstAddress(), controller.getFirstPort(), success);
        generatePlayerResponse(controller, controller.getSecondPlayer(), controller.getSecondAddress(), controller.getSecondPort(), success);
    }

    private void generatePlayerResponse( Controller controller, Player player, InetAddress to, int port, byte success ) {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        try(ObjectOutputStream oout = new ObjectOutputStream(bout)) {

            oout.write(success);
            if (success == 1) {
                oout.writeObject(player);
            }

            byte[] content = bout.toByteArray();
            PlayerUpdateGenerator generator = new PlayerUpdateGenerator();
            generator.generate(content, to, port, controller);
        } catch (IOException error) {
            System.out.println(error);
        }
    }

}
