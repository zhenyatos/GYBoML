package ru.spbstu.gyboml.server.session;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.SessionRequests;
import ru.spbstu.gyboml.core.net.SessionResponses;
import ru.spbstu.gyboml.server.GybomlConnection;
import ru.spbstu.gyboml.server.Main;
import ru.spbstu.gyboml.server.game.Game;
import ru.spbstu.gyboml.core.net.SessionInfo;

public class SessionListener extends Listener {

    // reference to main controller object
    Main main;

    public SessionListener( Main main ) {this.main = main;}

    @Override
    public void disconnected(Connection c) {
        GybomlConnection connection = (GybomlConnection)c;

        // remove player from session if he was in session
        if (connection.getSessionId() != null) {
            removeIdFromSession(main.sessionMap.get(connection.getSessionId()), connection.getPlayerId());
        }

        notifyAllPlayers();
    }

    /**
     * All session-side events listener
     */
    @Override
    public void received(Connection c, Object object)  {
        GybomlConnection connection = (GybomlConnection)c;

        // log
        System.out.println("Received " + object.getClass() + " from " + connection.getRemoteAddressTCP());

        if (object instanceof SessionRequests.RegisterName) {
            registerName(connection, (SessionRequests.RegisterName)object);
        } else if (object instanceof SessionRequests.CreateSession) {
            createSession(connection, (SessionRequests.CreateSession)object);
        } else if (object instanceof SessionRequests.ConnectSession) {
            connectSession(connection, (SessionRequests.ConnectSession)object);
        } else if (object instanceof SessionRequests.GetSessions) {
            getSessions(connection, (SessionRequests.GetSessions)object);
        } else if (object instanceof SessionRequests.ExitSession) {
            exitSession(connection, (SessionRequests.ExitSession)object);
        } else if (object instanceof SessionRequests.Ready) {
            ready(connection, (SessionRequests.Ready)object);
        }
    }

    private void sendError(GybomlConnection connection, String message) {
        SessionResponses.ServerError request = new SessionResponses.ServerError();
        request.message = message;
        connection.sendTCP(request);
    }

    /**
     * Called when player inputed his.getName when entered lobby menu
     */
    private void registerName(GybomlConnection connection, SessionRequests.RegisterName object) {
        if (connection.getName() != null) {sendError(connection, "Name was already chosen"); return;}

        String playerName = object.playerName.trim();

        if (playerName.length() == 0) {sendError(connection, "Invalid player.getName"); return;}
        connection.setName(playerName);
    }

    /**
     * Called when player attempts to create lobby
     */
    private void createSession(GybomlConnection connection, SessionRequests.CreateSession object) {
        if (connection.getName() == null) { sendError(connection, "Choose player.getName!"); return; }

        String lobbyName = object.sessionName.trim();
        if (lobbyName.length() == 0) { sendError(connection, "Invalid lobby.getName!"); return; }
        Session session = Session.create(lobbyName);
        main.sessionMap.put(session.getId(), session);

        SessionResponses.SessionCreated response = new SessionResponses.SessionCreated();
        response.sessionId = session.getId();
        connection.sendTCP(response);

        notifyAllPlayers();
    }

    /**
     * Called when player attempt to connect to existing lobby
     */
    private void connectSession(GybomlConnection connection, SessionRequests.ConnectSession object) {
        if (connection.getName() == null) {sendError(connection, "Choose player.getName!"); return;}

        int lobbyId = object.sessionId;
        Session session = main.sessionMap.get(lobbyId);
        if (session == null) {sendError(connection, "There is no lobby with that id"); return;}
        if (session.spaces() == 0) {sendError(connection, "There is no spaces in that lobby"); return;}
        if (session.isStarted()) {sendError(connection, "Game was already started"); return;}

        Player newPlayer = session.add(connection, connection.getName());

        connection.setPlayerId(newPlayer.id);
        connection.setSessionId(newPlayer.sessionId);

        // send approvement
        SessionResponses.SessionConnected response = new SessionResponses.SessionConnected();
        response.player = newPlayer;
        connection.sendTCP(response);


        notifyAllPlayers();
    }

    /**
     * Called when player attempts to get session list
     */
    private void getSessions(GybomlConnection connection, SessionRequests.GetSessions object) {
        if (connection == null) return;

        List<SessionInfo> lobbies = main.sessionMap.values().stream()
                                .map(Session::toSessionInfo)
                                .collect(Collectors.toList());

        SessionResponses.TakeSessions response = new SessionResponses.TakeSessions();
        response.lobbies = lobbies;
        connection.sendTCP(response);
    }

    /**
     * Called when player attempts to leave from session, which contains this player
     */
    private void exitSession(GybomlConnection connection, SessionRequests.ExitSession object) {
        if (connection.getName() == null) {sendError(connection, "Choose player.getName!"); return;}

        Player player = object.player;
        Session session = main.sessionMap.get(player.sessionId);
        if (session == null) {sendError(connection, "Attempt to leave from unexistent session"); return;}

        removeIdFromSession(session, player.id);

        connection.setSessionId(null);
        connection.setPlayerId(null);

        connection.sendTCP(new SessionResponses.SessionExited());
        notifyAllPlayers();
    }

    /**
     * Called when player attempts to set ready flag
     */
    private void ready(GybomlConnection connection, SessionRequests.Ready object) {
        if (connection.getName() == null) {sendError(connection, "Choose player.getName!"); return;}

        // get data
        Player player = object.player;
        Session session = main.sessionMap.get(player.sessionId);
        System.out.println(String.format("Ready request from %s#%s. Attempt to set ready %s", player.name, player.id, !player.ready));

        if (session == null) {sendError(connection, "Attempt to set Ready, but from unexistent session"); return;}

        // main logic
        session.ready(player.id, !player.ready);

        // repspond an approve message
        connection.sendTCP(new SessionResponses.ReadyApproved());
        notifySessionPlayers(session);

        // check if both players are ready
        NetPlayer firstPlayer = session.firstPlayer;
        NetPlayer secondPlayer = session.secondPlayer;
        if (firstPlayer != null && secondPlayer != null &&
            firstPlayer.getPlayer().ready && secondPlayer.getPlayer().ready) {

            SessionResponses.SessionStarted sessionStarted = new SessionResponses.SessionStarted();
            firstPlayer.getConnection().sendTCP(sessionStarted);
            secondPlayer.getConnection().sendTCP(sessionStarted);
            session.game = new Game(firstPlayer.getPlayer(), secondPlayer.getPlayer());
        }
    }

    private void notifySessionPlayers(Session session) {
        // send session info to players in this session
        // TODO: FIX IT AND SEND INFO ABOUT ONLY ONE SESSION, BUT NOT ALL LIST
        Function<NetPlayer, GybomlConnection> connectionSupplier = netPlayer -> {
            return netPlayer != null ? netPlayer.getConnection() : null;
        };
        getSessions(connectionSupplier.apply(session.firstPlayer), null);
        getSessions(connectionSupplier.apply(session.secondPlayer), null);
    }

    private void notifyAllPlayers() {
        List<SessionInfo> lobbies = main.sessionMap.values().stream()
                                .map(Session::toSessionInfo)
                                .collect(Collectors.toList());

        SessionResponses.TakeSessions response = new SessionResponses.TakeSessions();
        response.lobbies = lobbies;
        main.server.sendToAllTCP(response);
    }

    private void removeIdFromSession( Session session, long id ) {
        session.remove(id);
        if (session.spaces() == 2) { main.sessionMap.remove(session.getId()); }
    }
}