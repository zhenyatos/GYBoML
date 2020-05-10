package ru.spbstu.gyboml.lobby;

import android.view.View;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import ru.spbstu.gyboml.GybomlClient;
import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.SessionRequests;
import ru.spbstu.gyboml.core.net.SessionResponses;
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
        SessionRequests.RegisterName request = new SessionRequests.RegisterName();
        request.playerName = lobby.chosenPlayerName;
        connection.sendTCP(request);

        // request session list
        connection.sendTCP(new SessionRequests.GetSessions());
    }

    // called when connection finished
    @Override
    public void disconnected(Connection connection) {
        //TODO: output message in with red border 'Disconneted' to the screen corner
    }

    // called when received object via TCP
    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof SessionResponses.ServerError) { serverError(connection, (SessionResponses.ServerError)object); }
        else if (object instanceof SessionResponses.SessionCreated) { sessionCreated(connection, (SessionResponses.SessionCreated)object); }
        else if (object instanceof SessionResponses.TakeSessions) { takeSessions(connection, (SessionResponses.TakeSessions)object); }
        else if (object instanceof SessionResponses.SessionConnected) { sessionConnected(connection, (SessionResponses.SessionConnected)object); }
        else if (object instanceof SessionResponses.ReadyApproved) { readyApproved(connection, (SessionResponses.ReadyApproved)object); }
        else if (object instanceof SessionResponses.SessionExited) { sessionExited(connection, (SessionResponses.SessionExited)object); }
        else if (object instanceof SessionResponses.SessionStarted) { sessionStarted(connection, (SessionResponses.SessionStarted)object); }
        else if (object instanceof SessionResponses.UpdatePlayer) { GybomlClient.setPlayer(((SessionResponses.UpdatePlayer)object).player); }
    }

    private void sessionStarted(Connection connection, SessionResponses.SessionStarted object) {
        GybomlClient.setPlayer(object.player);
        lobby.gameStartUp();
    }

    private void sessionExited(Connection connection, SessionResponses.SessionExited object) {
        lobby.runOnUiThread(() -> {
            lobby.playerStatus = Lobby.PlayerStatus.FREE;
            lobby.sessionsAdapter.enableTouch();
            lobby.sessionsAdapter.chosenSessionID = null;
            lobby.notInSessionView();
        });

        connection.sendTCP(new SessionRequests.GetSessions());
    }

    private void serverError(Connection connection, SessionResponses.ServerError error) {
        //TODO: output message box with error messsage
        String message = error.message;
    }

    private void sessionCreated(Connection connection, SessionResponses.SessionCreated object) {
        int id = object.sessionId;

        SessionRequests.ConnectSession connectSession = new SessionRequests.ConnectSession();
        connectSession.sessionId = id;
        connection.sendTCP(connectSession);
    }

    private void sessionConnected(Connection connection, SessionResponses.SessionConnected object) {
        Player player = object.player;

        GybomlClient.setPlayer(player);

        lobby.runOnUiThread(() -> {
            lobby.playerStatus = Lobby.PlayerStatus.SESSIONJOINED;
            lobby.sessionsAdapter.chosenSessionID = player.sessionId;
            lobby.inSessionView();
        });
    }

    private void takeSessions(Connection connection, SessionResponses.TakeSessions object) {
        lobby.runOnUiThread(() -> {
            lobby.sessionsAdapter.sessions = object.lobbies;
            lobby.sessionsAdapter.notifyDataSetChanged();
            if (lobby.playerStatus == Lobby.PlayerStatus.SESSIONJOINED) {
                SessionInfo currentSession = lobby.sessionsAdapter.findSessionByID(GybomlClient.getPlayer().sessionId);
                    lobby.firstPlayerName.setText(currentSession.firstPlayer.name);
                    lobby.firstPlayerReady.setVisibility(currentSession.firstPlayer.ready ? View.VISIBLE : View.INVISIBLE);
                    if (currentSession.spaces == 0) {
                        lobby.secondPlayerName.setText(currentSession.secondPlayer.name);
                        lobby.secondPlayerReady.setVisibility(currentSession.secondPlayer.ready ? View.VISIBLE : View.INVISIBLE);
                    }
                    else {
                        lobby.secondPlayerName.setText("None");
                        lobby.secondPlayerReady.setVisibility(View.INVISIBLE);
                    }
            }
        });
    }

    private void readyApproved(Connection connection, SessionResponses.ReadyApproved object) {
        lobby.runOnUiThread(() -> {
            boolean ready = object.isReady;
            GybomlClient.getPlayer().ready = ready;
            lobby.readyButton.setChecked(ready);

            if (ready) lobby.exitButton.setVisibility(View.GONE);
            else lobby.exitButton.setVisibility(View.VISIBLE);
        });
    }
}

