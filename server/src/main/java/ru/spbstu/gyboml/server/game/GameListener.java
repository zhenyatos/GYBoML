package ru.spbstu.gyboml.server.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.spbstu.gyboml.core.net.GameRequests;
import ru.spbstu.gyboml.core.net.GameResponses;
import ru.spbstu.gyboml.server.GybomlConnection;
import ru.spbstu.gyboml.server.Main;
import ru.spbstu.gyboml.server.session.NetPlayer;
import ru.spbstu.gyboml.server.session.Session;

import static com.esotericsoftware.minlog.Log.*;
import static java.lang.String.format;

@RequiredArgsConstructor
public class GameListener extends Listener {

    @NonNull private Main main;

    @Override
    public void disconnected(Connection c) {
        GybomlConnection connection = (GybomlConnection)c;

        // if player even not in session
        if (connection.getSessionId() == null) return;

        Session session = main.getSession(connection.getSessionId());

        // handle only if session in game
        // otherwise this case handled in SessionListener
        if (session.isStarted()) {
            info(format("Player %s leaved from running game", connection));

            // send exit message to other game member
            NetPlayer otherPlayer = session.getOtherPlayer(connection.getPlayerId());
            if (otherPlayer == null) {
                error("Player leaved, but his opponent's id is not in same session");
                return;
            }
<<<<<<< HEAD
            otherPlayer.getConnection().sendTCP(new GameResponses.GameExited());

            // set session started to false
            session.setGame(null);
=======
            disconnectPlayerFromSession(otherPlayer.getConnection());

            // remove this session
            main.sessionMap.remove(session.getId());

            info(format("Session %s deleted", session.getId()));
>>>>>>> 31740df23e1df11839b8822a37fcb8e80c21c197
        }
    }

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
            } else if (object instanceof GameRequests.GameExit) {
                gameExit(connection);
            }
        } catch (Error | Exception ex) {
            error("Error occured in GameListener", ex);
            main.server.close();
            System.exit(2);
        }
    }

    private void gameExit(GybomlConnection connection) {
        Session session = main.getSession(connection.getSessionId());

        // if session not exist or not started yet
        if (session == null || !session.isStarted()) return;

        session.getFirstPlayer().getConnection().sendTCP(new GameResponses.GameExited());
        session.getSecondPlayer().getConnection().sendTCP(new GameResponses.GameExited());

        session.setGame(null);
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

<<<<<<< HEAD
=======
    private void disconnectPlayerFromSession(GybomlConnection connection) {
        connection.sendTCP(new GameResponses.GameExited());
        connection.setName(null);
        connection.setPlayerId(null);
        connection.setSessionId(null);
    }
>>>>>>> 31740df23e1df11839b8822a37fcb8e80c21c197
}