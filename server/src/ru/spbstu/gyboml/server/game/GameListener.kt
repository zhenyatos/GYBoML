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

        if (!connection.inSession || connection.player == null) return // even not in session
        val session = controller.getSession(connection.player!!.sessionId) ?: return
        if (!session.isStarted()) return // this case will be handled by SessionListener

        session.getPlayer(connection.player!!.type.reverted())
                ?.connection
                ?.sendTCP(GameResponses.GameExited())
        session.game = null
    }

    override fun received(connection: Connection?, request: Any?) {
        connection as GybomlConnection

        when (request) {
            is GameRequests.Shoot -> { // !! Possible NPE
                if (connection.player == null || !connection.inSession) return
                val session = controller.getSession(connection.player!!.sessionId) ?: return
                if (session.game == null) return

                val from = connection.player!!.type
                if (from == FIRST_PLAYER && session.game!!.stage != FIRST_PLAYER_ATTACK ||
                    from == SECOND_PLAYER && session.game!!.stage != SECOND_PLAYER_ATTACK)
                    return

                val response = GameResponses.Shooted(request.ballPosition, request.ballVelocity)

                // send shoot response to other player
                with (session) {
                    getPlayer(from.reverted())
                        ?.connection
                        ?.sendTCP(response)

                    // send pass turn response to both players
                    val fromFirst = from == FIRST_PLAYER
                    firstPlayer?.connection?.sendTCP(GameResponses.PassTurned(!fromFirst))
                    secondPlayer?.connection?.sendTCP(GameResponses.PassTurned(fromFirst))

                    // revert game stage
                    game!!.stage = game!!.stage.reverted()
                }
            }

            is GameRequests.GameExit -> {
                if (connection.player == null || !connection.inSession) return
                val session = controller.getSession(connection.player!!.sessionId) ?: return
                if (!session.isStarted()) return

                arrayOf(session.firstPlayer, session.secondPlayer).forEach {
                    it?.connection?.sendTCP(GameResponses.GameExited())
                }

                session.game = null
            }
        }
    }
}