package ru.spbstu.gyboml.server;

import java.util.List;
import java.util.stream.Collectors;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.Requests;
import ru.spbstu.gyboml.core.net.Responses;
import ru.spbstu.gyboml.core.net.SessionInfo;

public class SessionListener extends Listener {

    // reference to main controller object
    Main main;

    public SessionListener( Main main ) {this.main = main;}

    /**
     * All session-side events listener
     */
    @Override
    public void received(Connection c, Object object)  {
        GybomlConnection connection = (GybomlConnection)c;

        // log
        System.out.println("Received " + object.getClass() + " from " + connection.getRemoteAddressTCP());

        if (object instanceof Requests.RegisterName) {
            registerName(connection, (Requests.RegisterName)object);
        } else if (object instanceof Requests.CreateSession) {
            createSession(connection, (Requests.CreateSession)object);
        } else if (object instanceof Requests.ConnectSession) {
            connectSession(connection, (Requests.ConnectSession)object);
        } else if (object instanceof Requests.GetSessions) {
            getSessions(connection, (Requests.GetSessions)object);
        } else if (object instanceof Requests.ExitSession) {
            exitSession(connection, (Requests.ExitSession)object);
        } else if (object instanceof Requests.Ready) {
            ready(connection, (Requests.Ready)object);
        }
    }

    private void sendError(GybomlConnection connection, String message) {
        Responses.ServerError request = new Responses.ServerError();
        request.message = message;
        connection.sendTCP(request);
    }

    /**
     * Called when player inputed his name when entered lobby menu
     */
    private void registerName(GybomlConnection connection, Requests.RegisterName object) {
        System.out.println("RegisterName request");

        if (connection.name() != null) {sendError(connection, "Name was already chosen"); return;}

        String playerName = object.playerName.trim();

        if (playerName.length() == 0) {sendError(connection, "Invalid player name"); return;}
        connection.setName(playerName);
    }

    /**
     * Called when player attempts to create lobby
     */
    private void createSession(GybomlConnection connection, Requests.CreateSession object) {
        System.out.println("CreateSession request");

        if (connection.name() == null) { sendError(connection, "Choose player name!"); return; }

        String lobbyName = object.sessionName.trim();
        if (lobbyName.length() == 0) { sendError(connection, "Invalid lobby name!"); return; }
        Session session = Session.create(lobbyName);
        main.sessionMap.put(session.id(), session);

        Responses.SessionCreated response = new Responses.SessionCreated();
        response.sessionId = session.id();
        connection.sendTCP(response);
    }

    /**
     * Called when player attempt to connect to existing lobby
     */
    private void connectSession(GybomlConnection connection, Requests.ConnectSession object) {
        System.out.println("Connect session request");
        if (connection.name() == null) {sendError(connection, "Choose player name!"); return;}

        int lobbyId = object.sessionId;
        Session session = main.sessionMap.get(lobbyId);
        if (session == null) {sendError(connection, "There is no lobby with that id"); return;}
        if (session.spaces() == 0) {sendError(connection, "There is no spaces in that lobby"); return;}
        if (session.isStarted()) {sendError(connection, "Game was already started"); return;}

        Player newPlayer = session.add(connection.name());

        // send approvement
        Responses.SessionConnected response = new Responses.SessionConnected();
        response.player = newPlayer;
        connection.sendTCP(response);
    }

    /**
     * Called when player attempts to get session list
     */
    private void getSessions(GybomlConnection connection, Requests.GetSessions object) {
        System.out.println("Get sessions request");
        List<SessionInfo> lobbies = main.sessionMap.values().stream()
                                .map(Session::toSessionInfo)
                                .collect(Collectors.toList());

        Responses.TakeSessions response = new Responses.TakeSessions();
        response.lobbies = lobbies;
        connection.sendTCP(response);
    }

    /**
     * Called when player attempts to leave from session, which contains this player
     */
    private void exitSession(GybomlConnection connection, Requests.ExitSession object) {
        System.out.println("Exit session request");
        if (connection.name() == null) {sendError(connection, "Choose player name!"); return;}

        Player player = object.player;
        Session session = main.sessionMap.get(player.sessionId);
        if (session == null) {sendError(connection, "Attempt to leave from unexistent session"); return;}

        session.remove(player.id);

        // remove session if it become empty
        if (session.spaces() == 2) { main.sessionMap.remove(session.id()); }

        connection.sendTCP(new Responses.SessionExited());
    }

    /**
     * Called when player attempts to set ready flag
     */
    private void ready(GybomlConnection connection, Requests.Ready object) {
        if (connection.name() == null) {sendError(connection, "Choose player name!"); return;}
        Player player = object.player;
        System.out.println(String.format("Ready request from %s#%s. Attempt to set ready %s", player.name, player.id, !player.ready));

        Session session = main.sessionMap.get(player.sessionId);
        if (session == null) {sendError(connection, "Attempt to set Ready, but from unexistent session"); return;}

        session.ready(player.id, !player.ready);

        connection.sendTCP(new Responses.ReadyApproved());
    }
}