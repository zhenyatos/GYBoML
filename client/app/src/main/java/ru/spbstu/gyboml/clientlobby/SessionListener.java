package main.java.ru.spbstu.gyboml.clientlobby;

import android.view.View;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.Requests;
import ru.spbstu.gyboml.core.net.Responses;
import ru.spbstu.gyboml.core.net.SessionInfo;

/**
 * Listener to all TCP events
 */
public class SessionListener extends Listener {

    // reference to lobby activity
    Lobby lobby;

    public SessionListener( Lobby lobby ) { this.lobby = lobby; }

    // called when connection established
    @Override
    public void connected(Connection connection) {
        //TODO: output message in with green border 'Connected' to the screen corner

        // register name
        Requests.RegisterName request = new Requests.RegisterName();
        request.playerName = lobby.chosenPlayerName;
        connection.sendTCP(request);

        // request session list
        connection.sendTCP(new Requests.GetSessions());
    }

    // called when connection finished
    @Override
    public void disconnected(Connection connection) {
        //TODO: output message in with red border 'Disconneted' to the screen corner
    }

    // called when received object via TCP
    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof Responses.ServerError) { serverError(connection, (Responses.ServerError)object); }
        else if (object instanceof Responses.SessionCreated) { sessionCreated(connection, (Responses.SessionCreated)object); }
        else if (object instanceof Responses.TakeSessions) { takeSessions(connection, (Responses.TakeSessions)object); }
        else if (object instanceof Responses.SessionConnected) { sessionConnected(connection, (Responses.SessionConnected)object); }
        else if (object instanceof Responses.ReadyApproved) { readyApproved(connection, (Responses.ReadyApproved)object); }
        else if (object instanceof Responses.SessionExited) { sessionExited(connection, (Responses.SessionExited)object); }
    }

    private void sessionExited(Connection connection, Responses.SessionExited object) {
        lobby.runOnUiThread(() -> {
            lobby.playerStatus = Lobby.PlayerStatus.FREE;
            lobby.sessionsAdapter.enableTouch();
            lobby.sessionsAdapter.chosenSessionID = null;
            lobby.notInSessionView();
        });

        connection.sendTCP(new Requests.GetSessions());
    }

    private void serverError(Connection connection, Responses.ServerError error) {
        //TODO: output message box with error messsage
        String message = error.message;
    }

    private void sessionCreated(Connection connection, Responses.SessionCreated object) {
        int id = object.sessionId;

        Requests.ConnectSession connectSession = new Requests.ConnectSession();
        connectSession.sessionId = id;
        connection.sendTCP(connectSession);
    }

    private void sessionConnected(Connection connection, Responses.SessionConnected object) {
        Player player = object.player;
        lobby.player = player;

        lobby.runOnUiThread(() -> {
            lobby.playerStatus = Lobby.PlayerStatus.SESSIONJOINED;
            lobby.sessionsAdapter.chosenSessionID = player.sessionId;
            lobby.inSessionView();
        });
    }

    private void takeSessions(Connection connection, Responses.TakeSessions object) {
        lobby.runOnUiThread(() -> {
            lobby.sessionsAdapter.sessions = object.lobbies;
            lobby.sessionsAdapter.notifyDataSetChanged();
            if (lobby.playerStatus == Lobby.PlayerStatus.SESSIONJOINED) {
                SessionInfo currentSession = lobby.sessionsAdapter.findSessionByID(lobby.player.sessionId);
                    lobby.firstPlayerName.setText(currentSession.firstPlayer.name);
                    lobby.firstPlayerReady.setVisibility(currentSession.firstPlayer.ready ? View.VISIBLE : View.INVISIBLE);
                    if (currentSession.spaces == 2) {
                        lobby.secondPlayerName.setText(currentSession.secondPlayer.name);
                        lobby.secondPlayerReady.setVisibility(currentSession.secondPlayer.ready ? View.VISIBLE : View.INVISIBLE);
                    }
            }
        });
    }

    private void readyApproved(Connection connection, Responses.ReadyApproved object) {
        lobby.runOnUiThread(() -> {
            lobby.player.ready = lobby.readyButton.isChecked();
            if (lobby.readyButton.isChecked())
                lobby.exitButton.setVisibility(View.GONE);
            else
                lobby.exitButton.setVisibility(View.VISIBLE);
        });
    }
}

