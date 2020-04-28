package ru.spbstu.gyboml.server.game

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import ru.spbstu.gyboml.core.PlayerType.*
import ru.spbstu.gyboml.core.net.GameRequests
import ru.spbstu.gyboml.core.net.GameResponses
import ru.spbstu.gyboml.server.Controller
import ru.spbstu.gyboml.server.GybomlConnection
import ru.spbstu.gyboml.server.game.Stage.FIRST_PLAYER_ATTACK
import ru.spbstu.gyboml.server.game.Stage.SECOND_PLAYER_ATTACK

class GameListener(private val controller: Controller) : Listener() {
    override fun disconnected(connection: Connection?) {
        connection as GybomlConnection

        connection.player?.let { player ->
            player.sessionId?.let { sessionId ->
                val session = controller.getSession(sessionId) ?: return
                if (!session.isStarted()) return

                session.getPlayer(player.type.reverted())
                    ?.connection
                    ?.sendTCP(GameResponses.GameExited())
                session.game = null
            }
        }
    }

    override fun received(connection: Connection?, request: Any?) {
        connection as GybomlConnection

        when (request) {
            is GameRequests.Shoot -> shot(connection, request)
            is GameRequests.GameExit -> gameExit(connection)
        }
    }

    private fun shot(connection: GybomlConnection, request: GameRequests.Shoot) {
        connection.player?.let { player ->
            player.sessionId?.let { sessionId ->
                val session = controller.getSession(sessionId) ?: return
                if (!session.isStarted()) return

                if (player.type == FIRST_PLAYER && session.game!!.stage != FIRST_PLAYER_ATTACK ||
                    player.type == SECOND_PLAYER && session.game!!.stage != SECOND_PLAYER_ATTACK)
                    return

                session.getPlayer(player.type.reverted())
                    ?.connection
                    ?.sendTCP(GameResponses.Shooted(request.ballPosition, request.ballVelocity))

                // send pass turn response to both players
                val fromFirst = player.type == FIRST_PLAYER
                session.firstPlayer?.connection?.sendTCP(GameResponses.PassTurned(!fromFirst))
                session.secondPlayer?.connection?.sendTCP(GameResponses.PassTurned(fromFirst))

                // revert game stage
                session.game?.let { it.stage = it.stage.reverted() }
            }
        }
    }
    private fun gameExit(connection: GybomlConnection) {
        connection.player?.let { player ->
            player.sessionId?.let {  sessionId ->
                val session = controller.getSession(sessionId) ?: return
                if (!session.isStarted()) return

                arrayOf(session.firstPlayer, session.secondPlayer).forEach { player ->
                    player?.connection?.sendTCP(GameResponses.GameExited())
                }
            }
        }
    }
}