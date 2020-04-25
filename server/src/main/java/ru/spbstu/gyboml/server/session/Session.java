package ru.spbstu.gyboml.server.session;

import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;
import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.SessionInfo;
import ru.spbstu.gyboml.server.GybomlConnection;
import ru.spbstu.gyboml.server.Main;
import ru.spbstu.gyboml.server.game.Game;

import static com.esotericsoftware.minlog.Log.*;

@Getter
@Setter
public class Session {

    // players
    NetPlayer firstPlayer;
    NetPlayer secondPlayer;

    Game game;

    private final int id;
    private static int nextAvailable = 0;

    private String name;

    // close constructor
    private Session(int id, String name){
        this.id = id;
        this.name = name;

        info("Session #" + id + " was created");
    }

    // fabric method
    public static Session create(String name) {
        return new Session(Session.nextAvailable++, name);
    }

    /**
     * Add player to sesion method
     * @return New instance of Player
     */
    public Player add(GybomlConnection connection, final String name) {
        Function<Boolean, NetPlayer> create = isTurn -> {
            Player player = new Player(name, 0, isTurn);
            player.name = name;
            player.points = 0;
            player.isTurn = isTurn;
            player.id = Main.nextAvailablePlayerId();
            player.sessionId = id;

            info("Player " + player + " was created");
            return new NetPlayer(player, connection);
        };
        if (firstPlayer == null) {
            firstPlayer = create.apply(true); return firstPlayer.getPlayer();
        }
        else if (secondPlayer == null) {
            secondPlayer = create.apply(false);
            return secondPlayer.getPlayer();
        }
        else {
            return null;
        }
    }

    /**
     * Returns number of free spaces in session
     */
    public int spaces() {
        if (firstPlayer == null) {return 2;}
        else if (secondPlayer == null) {return 1;}
        else {return 0;}
    }

    /**
     * Remove player by connection
     * @param connection to delete by
     * @return true if player removed, false if there is no player with this connection
     */
    public boolean remove(long playerId) {
        if (firstPlayer != null && firstPlayer.getPlayer().id == playerId) {
            info("Player " + firstPlayer.getPlayer() + " is leaving");
            firstPlayer = secondPlayer;
            secondPlayer = null;
            return true;
        } else if (secondPlayer != null && secondPlayer.getPlayer().id == playerId) {
            info("Player " + secondPlayer.getPlayer() + " is leaving");
            secondPlayer = null;
            return true;
        } else return false;
    }

    public boolean ready(long playerId, boolean isReady) {
        if (firstPlayer != null && firstPlayer.getPlayer().id == playerId) {
            firstPlayer.getPlayer().setReady(isReady);
            info("Player " + firstPlayer.getPlayer() + " ready is: " + isReady);
            return true;
        } else if (secondPlayer != null && secondPlayer.getPlayer().id == playerId) {
            secondPlayer.getPlayer().setReady(isReady);
            info("Player " + secondPlayer.getPlayer() + " ready is: " + isReady);
            return true;
        } else return false;
    }

    /**
     * Convert to session to lobby.
     * Need for transfering between server and client
     * @return
     */
    public SessionInfo toSessionInfo() {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.name = this.name;
        sessionInfo.sessionId = this.id;
        sessionInfo.spaces = spaces();
        sessionInfo.firstPlayer = firstPlayer != null ? firstPlayer.getPlayer() : null;
        sessionInfo.secondPlayer = secondPlayer != null ? secondPlayer.getPlayer() : null;
        sessionInfo.isStarted = isStarted();

        return sessionInfo;
    }

    public boolean isStarted() { return game != null; }

    public NetPlayer getOtherPlayer( long playerId ) {
        if (firstPlayer.getPlayer().id() == playerId) {
            return secondPlayer;
        } else if (secondPlayer.getPlayer().id() == playerId) {
            return secondPlayer;
        } else return null;
    }
}