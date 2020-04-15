package ru.spbstu.gyboml.server;

import java.util.List;
import java.util.stream.Collectors;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import ru.spbstu.gyboml.core.net.Responses;
import ru.spbstu.gyboml.core.net.SessionInfo;
import ru.spbstu.gyboml.core.net.Requests.ConnectLobby;
import ru.spbstu.gyboml.core.net.Requests.CreateLobby;
import ru.spbstu.gyboml.core.net.Requests.GetLobbies;
import ru.spbstu.gyboml.core.net.Requests.RegisterName;

public class ServerListener extends Listener {

    // reference to main controller object
    Main main;

    public ServerListener( Main main ) {this.main = main;}

    @Override
    public void received(Connection c, Object object)  {
        GybomlConnection connection = (GybomlConnection)c;

        if (object instanceof RegisterName) {
            if (connection.name() != null) { c.sendTCP(new Responses.ServerError("Name was already chosen")); return; }
            String playerName = ((RegisterName)object).playerName.trim();

            if (playerName.length() == 0) { c.sendTCP(new Responses.ServerError("Invalid player name")); return; }
            connection.setName(playerName);
        } else if (object instanceof CreateLobby) {
            if (connection.name() == null) { c.sendTCP(new Responses.ServerError("Choose player name!")); return; }

            String lobbyName = ((CreateLobby)object).lobbyName.trim();
            if (lobbyName.length() == 0) { c.sendTCP(new Responses.ServerError("Invalid lobby name!")); return; }
            Session session = Session.create(lobbyName);
            main.sessionMap.put(session.id(), session);
        } else if (object instanceof ConnectLobby) {
            if (connection.name() == null) { c.sendTCP(new Responses.ServerError("Choose player name!")); return; }

            int lobbyId = ((ConnectLobby)object).lobbyId;
            Session session;
            if ((session = main.sessionMap.get(lobbyId)) == null) {
                c.sendTCP(new Responses.ServerError("There is no lobby with that id"));
                return;
            }

            if (session.spaces() == 0) {
                c.sendTCP(new Responses.ServerError("There is no spaces in that lobby"));
                return;
            }
            
            if (session.isStarted()) {
                c.sendTCP(new Responses.ServerError("Game was already started"));
                return;
            }

            session.add(connection, connection.name());
        } else if (object instanceof GetLobbies) {
            List<SessionInfo> lobbies = main.sessionMap.values().stream()
                                    .map(Session::toSessionInfo)
                                    .collect(Collectors.toList());

            c.sendTCP(new Responses.TakeLobbies(lobbies));
        }
    }
}