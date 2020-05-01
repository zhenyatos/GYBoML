package ru.spbstu.gyboml.server.game

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import ru.spbstu.gyboml.core.net.GameMessage.*
import ru.spbstu.gyboml.core.net.GameMessage
import ru.spbstu.gyboml.core.net.GameRequests
import ru.spbstu.gyboml.core.net.GameRequests.GameLoaded
import ru.spbstu.gyboml.core.net.GameResponses.*
import ru.spbstu.gyboml.server.Controller
import ru.spbstu.gyboml.server.GybomlConnection
import ru.spbstu.gyboml.server.session.Session.Companion.extractSessionAndDo

class  GameListener(private val controller: Controller) : Listener() {
    override fun disconnected(connection: Connection?) {
        connection as GybomlConnection

        extractSessionAndDo(controller, connection) { session ->
            session.getOther(connection)?.sendTCP(GameExited())
            session.game = null
        }
    }

    override fun received(connection: Connection?, request: Any?) {
        connection as GybomlConnection

        when (request) {
            is GameRequests.GameExit -> gameExit(connection)
            is CreateBlock -> sendToBoth(connection, request)
            is UpdateBlock -> traceUdpMessage(connection, request)
            is RemoveBlock -> sendToBoth(connection, request)
            is CreateShot -> sendToBoth(connection, request)
            is UpdateShot -> traceUdpMessage(connection, request)
            is RemoveShot -> sendToBoth(connection, request)
            is GameLoaded -> gameLoaded(connection)
        }
    }

    private fun gameExit(connection: GybomlConnection) {
        connection.player?.let { player ->
            player.sessionId?.let {  sessionId ->
                val session = controller.getSession(sessionId) ?: return
                if (!session.isStarted()) return

                arrayOf(session.firstConnection, session.secondConnection).forEach { it?.sendTCP(GameExited()) }
            }
        }
    }
    private fun sendToBoth(connection: GybomlConnection, message: GameMessage) {
        connection.player?.sessionId?.let {
            val session = controller.getSession(it) ?: return
            if (!session.isStarted()) return

            session.firstConnection?.sendTCP(message)
            session.secondConnection?.sendTCP(message)
        }
    }
    private fun gameLoaded(connection: GybomlConnection) = extractSessionAndDo(controller, connection) { session ->
        session.game?.let { game ->
            when (connection) {
                session.firstConnection -> game.firstLoaded = true
                session.secondConnection -> game.secondLoaded = true
            }

            if (game.firstLoaded && game.secondLoaded) {
                session.firstConnection?.sendTCP(GameStarted())
                session.secondConnection?.sendTCP(GameStarted())
            }
        }
    }

    // udp messages doesn't handled by server,
    // server only traces them
    private fun traceUdpMessage(connection: GybomlConnection, message: GameMessage) =
        extractSessionAndDo(controller, connection) { it.getOther(connection)?.sendUDP(message) }
}