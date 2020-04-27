package ru.spbstu.gyboml.server.session

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import ru.spbstu.gyboml.core.Player
import ru.spbstu.gyboml.core.PlayerType.FIRST_PLAYER
import ru.spbstu.gyboml.core.PlayerType.SECOND_PLAYER
import ru.spbstu.gyboml.core.net.SessionRequests
import ru.spbstu.gyboml.core.net.SessionResponses
import ru.spbstu.gyboml.core.net.SessionResponses.NameRegistred
import ru.spbstu.gyboml.server.Controller
import ru.spbstu.gyboml.server.GybomlConnection
import ru.spbstu.gyboml.server.game.Game

class SessionListener(private val controller: Controller) : Listener() {
    override fun disconnected(connection: Connection?) {
        connection as GybomlConnection

        connection.player?.let {
            if (connection.inSession) {
                controller.removeFromSession(it.sessionId, it.type)
                controller.notifyAllPlayers()
            }
        }
    }

    override fun received(connection: Connection?, request: Any?) {
        connection as GybomlConnection

        if (connection.player == null && request !is SessionRequests.RegisterName) return

        when (request) {
            is SessionRequests.RegisterName ->
                registerName(connection, request.playerName)
            is SessionRequests.GetSessions ->
                controller.notifyOne(connection)
            is SessionRequests.ConnectSession ->
                connectSession(connection, controller.getSession(request.sessionId) ?: return)
            is SessionRequests.ExitSession ->
                exitSession(connection, request.player)
            is SessionRequests.CreateSession ->
                createSession(connection, request.sessionName)
            is SessionRequests.Ready ->
                setReady(connection, controller.getSession(request.player.sessionId) ?: return, request.player.ready)
        }
    }

    private fun registerName(connection: GybomlConnection, name: String) {
        with (connection) {
            player = Player(name, FIRST_PLAYER)
            sendTCP(NameRegistred())
        }
    }
    private fun connectSession(connection: GybomlConnection, session: Session) = with (connection) {
        if (session.spaces() == 0) return@with
        player?.let {
            it.ready = false
            it.setSessionId(session.id)
            inSession = true
            session.add(connection, it)
            sendTCP(SessionResponses.SessionConnected(it))
            controller.notifyAllPlayers()
        }
    }
    private fun exitSession(connection: GybomlConnection, player: Player) = with (connection) {
        inSession = false
        controller.removeFromSession(player.sessionId, player.type)
        sendTCP(SessionResponses.SessionExited())
        controller.notifyAllPlayers()
    }
    private fun createSession(connection: GybomlConnection, name: String) = with (connection) {
        connection.sendTCP(SessionResponses.SessionCreated(controller.addSession(name)))
        controller.notifyAllPlayers()
    }
    private fun setReady(connection: GybomlConnection, session: Session, ready: Boolean) = with(connection) {
        player?.let {
            session.setReady(it.type, !ready)
            sendTCP(SessionResponses.ReadyApproved(!ready))
            controller.notifySessionPlayers(it.sessionId)

            startGameIfReady(session.firstPlayer ?: return@let, session.secondPlayer ?: return@let, session)
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
                firstPlayer.connection.sendTCP(SessionResponses.SessionStarted(first))
                secondPlayer.connection.sendTCP(SessionResponses.SessionStarted(second))

                game = Game(first, second)
            }
        }}
    }
}