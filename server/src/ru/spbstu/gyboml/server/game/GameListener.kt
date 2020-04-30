package ru.spbstu.gyboml.server.game

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import ru.spbstu.gyboml.core.net.GameRequests
import ru.spbstu.gyboml.core.net.GameResponses.*
import ru.spbstu.gyboml.server.Controller
import ru.spbstu.gyboml.server.GybomlConnection
import ru.spbstu.gyboml.server.game.Stage.FIRST_PLAYER_ATTACK
import ru.spbstu.gyboml.server.game.Stage.SECOND_PLAYER_ATTACK

class  GameListener(private val controller: Controller) : Listener() {
    override fun disconnected(connection: Connection?) {
        connection as GybomlConnection

        connection.player?.let { player ->
            player.sessionId?.let { sessionId ->
                val session = controller.getSession(sessionId) ?: return
                if (!session.isStarted()) return

                session.getOther(connection)?.sendTCP(GameExited())
                session.game = null
            }
        }
    }

    override fun received(connection: Connection?, request: Any?) {
        connection as GybomlConnection

        when (request) {
            is GameRequests.GameExit -> gameExit(connection)
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
}