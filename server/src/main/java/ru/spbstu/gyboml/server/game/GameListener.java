package ru.spbstu.gyboml.server.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.spbstu.gyboml.core.net.GameRequests;
import ru.spbstu.gyboml.core.net.GameResponses;
import ru.spbstu.gyboml.server.GybomlConnection;
import ru.spbstu.gyboml.server.Main;

@RequiredArgsConstructor
public class GameListener extends Listener {

    @NonNull private Main main;

    @Override
    public void connected(Connection connection) {
        System.out.println("GameListener");
    }

    @Override
    public void received(Connection c, Object object) {
        GybomlConnection connection = (GybomlConnection)c;

        if (object instanceof GameRequests.Shoot) {
            Game game = main.sessionMap
                            .get(connection.getSessionId())
                            .getGame();

            boolean fromFirstPlayer;
            if (connection.getPlayerId() == game.getFirstPlayer().id) {
                fromFirstPlayer = true;
            } else if (connection.getPlayerId() == game.getSecondPlayer().id) {
                fromFirstPlayer = false;
            } else return;

            // if it is not player's turn
            if (fromFirstPlayer && game.getCurrentStage() != Game.Stage.FISRT_PLAYER_ATTACK ||
                !fromFirstPlayer && game.getCurrentStage() != Game.Stage.SECOND_PLAYER_ATTACK) {
                return;
            }

            // send shoot responses
            GameResponses.Shooted firstShootedRequest = new GameResponses.Shooted();
            firstShootedRequest.yourShoot = fromFirstPlayer;
            GameResponses.Shooted secondShootedRequest = new GameResponses.Shooted();
            secondShootedRequest.yourShoot = !fromFirstPlayer;
            sendResponses(game, firstShootedRequest, secondShootedRequest);

            // send pass turn responses
            GameResponses.PassTurned firstPassedResponse = new GameResponses.PassTurned();
            firstPassedResponse.yourTurn = !fromFirstPlayer;
            GameResponses.PassTurned secondPassedResponse = new GameResponses.PassTurned();
            secondPassedResponse.yourTurn = fromFirstPlayer;
            sendResponses(game, firstPassedResponse, secondPassedResponse);
        }
    }

    private void sendResponses(Game game, Object firstResponse, Object secondResponse) {
            main.sessionMap
            .get(game.getFirstPlayer().sessionId)
            .getFirstPlayer()
            .getConnection()
            .sendTCP(firstResponse);

            main.sessionMap
            .get(game.getSecondPlayer().sessionId)
            .getSecondPlayer()
            .getConnection()
            .sendTCP(secondResponse);
    }
}