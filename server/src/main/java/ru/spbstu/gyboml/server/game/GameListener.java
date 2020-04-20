package ru.spbstu.gyboml.server.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.spbstu.gyboml.core.net.GameRequests;
import ru.spbstu.gyboml.core.net.GameResponses;
import ru.spbstu.gyboml.server.GybomlConnection; import ru.spbstu.gyboml.server.Main;

import static com.esotericsoftware.minlog.Log.*;

@RequiredArgsConstructor
public class GameListener extends Listener {

    @NonNull private Main main;

    @Override
    public void connected(Connection connection) {
    }

    @Override
    public void received(Connection c, Object object) {
        GybomlConnection connection = (GybomlConnection)c;

        try {
            // log
            if (object.getClass() != KeepAlive.class) {
                info("SessionListener received packet " +
                    object.getClass().getSimpleName() +
                    " from " +
                    c.getRemoteAddressTCP());
            }

            if (object instanceof GameRequests.Shoot) {
                shoot(connection, (GameRequests.Shoot)object);
            }
        } catch (Error | Exception ex) {
            error("Error occured in GameListener", ex);
            main.server.close();
            System.exit(2);
        }
    }

    private void shoot(GybomlConnection connection, GameRequests.Shoot object) {
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
            info(connection + ": attempted to shoot not in his turn");
            return;
        }

        // send shoot responses
        GameResponses.Shooted shootResponse = new GameResponses.Shooted();
        shootResponse.ballPositionX = object.ballPositionX;
        shootResponse.ballPositionY = object.ballPositionY;
        shootResponse.ballVelocityX = object.ballVelocityX;
        shootResponse.ballVelocityY = object.ballVelocityY;
        GybomlConnection to = fromFirstPlayer ?
            main.sessionMap
            .get(game.getSecondPlayer().sessionId)
            .getSecondPlayer()
            .getConnection() :
            main.sessionMap
            .get(game.getFirstPlayer().sessionId)
            .getFirstPlayer()
            .getConnection();
        to.sendTCP(shootResponse);

        info(connection + " shooted");

        // send pass turn responses
        GameResponses.PassTurned firstPassedResponse = new GameResponses.PassTurned();
        firstPassedResponse.yourTurn = !fromFirstPlayer;
        GameResponses.PassTurned secondPassedResponse = new GameResponses.PassTurned();
        secondPassedResponse.yourTurn = fromFirstPlayer;
        sendResponses(game, firstPassedResponse, secondPassedResponse);

        game.setCurrentStage(game.getCurrentStage().reverted());
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