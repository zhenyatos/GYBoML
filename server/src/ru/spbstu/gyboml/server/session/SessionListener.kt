package ru.spbstu.gyboml.server.session

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.minlog.Log
import ru.spbstu.gyboml.core.PlayerType.FIRST_PLAYER
import ru.spbstu.gyboml.core.PlayerType.SECOND_PLAYER
import ru.spbstu.gyboml.core.net.SessionPlayer
import ru.spbstu.gyboml.core.net.SessionRequests
import ru.spbstu.gyboml.core.net.SessionResponses
import ru.spbstu.gyboml.core.net.SessionResponses.NameRegistred
import ru.spbstu.gyboml.server.Controller
import ru.spbstu.gyboml.server.GybomlConnection
import ru.spbstu.gyboml.server.game.Game

class SessionListener(private val controller: Controller) : Listener() {
    override fun disconnected(connection: Connection?) {
        connection as GybomlConnection

        // if player and session id are not null
        connection.player?.let { player -> player.sessionId?.let{ id ->
            controller.removeFromSession(id, player.type)
        }}
    }

    override fun received(connection: Connection?, request: Any?) {
        connection as GybomlConnection

        Log.info("[SessionListener] Received $request from $connection")

        if (connection.player == null && request !is SessionRequests.RegisterName) return

        when (request) {
            is SessionRequests.RegisterName -> registerName(connection, request.name)
            is SessionRequests.GetSessions -> controller.notifyOne(connection)
            is SessionRequests.ConnectSession -> connectSession(connection, controller.getSession(request.sessionId) ?: return)
            is SessionRequests.ExitSession -> exitSession(connection)
            is SessionRequests.CreateSession -> createSession(connection, request.sessionName)
            is SessionRequests.Ready -> ready(connection)
        }
    }

    private fun ready(connection: GybomlConnection) {
        connection.player?.let { player ->
            player.sessionId?.let {  sessionId ->
                setReady(connection, controller.getSession(sessionId) ?: return)
            }
        }
    }

    private fun registerName(connection: GybomlConnection, name: String) {
        with (connection) {
            player = SessionPlayer(name = name)
            sendTCP(NameRegistred())
        }
    }

    private fun connectSession(connection: GybomlConnection, session: Session) = with (connection) {
        if (session.spaces() == 0) return@with
        player?.let {
            it.ready = false
            it.sessionId = session.id
            session.add(connection, it)

            // send
            sendTCP(SessionResponses.SessionConnected(session.id))
            controller.notifyAllPlayers()
        }
    }

    private fun exitSession(connection: GybomlConnection) = with (connection) {
        player?.let { player ->
            player.sessionId?.let { sessionId ->
                controller.removeFromSession(sessionId, player.type)
            }
            player.sessionId = null
        }
        sendTCP(SessionResponses.SessionExited())
        controller.notifyAllPlayers()
    }
    private fun createSession(connection: GybomlConnection, name: String) = with (connection) {
        connection.sendTCP(SessionResponses.SessionCreated(controller.addSession(name)))
        controller.notifyAllPlayers()
    }
    private fun setReady(connection: GybomlConnection, session: Session) = with(connection) {
        player?.let {
            val ready = session.invertReady(it.type)
            sendTCP(SessionResponses.ReadyApproved(ready))
            it.sessionId?.let { id -> controller.notifySessionPlayers(id) }

            startGameIfReady(session.firstPlayer ?: return@let,
                session.secondPlayer ?: return@let, session)
        }
    }
    private fun startGameIfReady(firstPlayer: NetPlayer, secondPlayer: NetPlayer, session: Session) = with(session) {
        firstPlayer.player?.let {first ->
        secondPlayer.player?.let {second ->
            if (first.ready && second.ready) {
                first.type = FIRST_PLAYER
                second.type = SECOND_PLAYER
                first.ready = false
                second.ready = false
                firstPlayer.connection.sendTCP(SessionResponses.SessionStarted())
                secondPlayer.connection.sendTCP(SessionResponses.SessionStarted())

                game = Game(session)
            }
        }}
    }
}