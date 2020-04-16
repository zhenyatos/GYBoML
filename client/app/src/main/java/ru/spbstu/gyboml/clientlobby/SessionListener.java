package main.java.ru.spbstu.gyboml.clientlobby;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.FileOutputStream;

import ru.spbstu.gyboml.core.net.Requests;
import ru.spbstu.gyboml.core.net.Responses;

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
        lobby.playerStatus = Lobby.PlayerStatus.FREE;
        lobby.sessionsAdapter.enableTouch();
        lobby.sessionsAdapter.chosenSessionID = null;
        lobby.notInSessionView();
    }

    private void serverError(Connection connection, Responses.ServerError error) {
        //TODO: output message box with error messsage
        String message = error.message;
    }

    private void sessionCreated(Connection connection, Responses.SessionCreated object) {
        lobby.playerStatus = Lobby.PlayerStatus.SESSIONJOINED;
        lobby.sessionsAdapter.disableTouch();
        lobby.inSessionView();
    }

    private void sessionConnected(Connection connection, Responses.SessionConnected object) {
        lobby.playerStatus = Lobby.PlayerStatus.SESSIONJOINED;
        lobby.sessionsAdapter.disableTouch();
        lobby.inSessionView();
    }

    private void takeSessions(Connection connection, Responses.TakeSessions object) {
        lobby.sessionsAdapter.sessions = object.lobbies;
    }

    private void readyApproved(Connection connection, Responses.ReadyApproved object) {
        lobby.exitButton.setEnabled(object.isReady);
    }
}
