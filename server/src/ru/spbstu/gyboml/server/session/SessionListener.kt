package ru.spbstu.gyboml.server.session

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.minlog.Log.*
import ru.spbstu.gyboml.core.Player
import ru.spbstu.gyboml.core.PlayerType.FIRST_PLAYER
import ru.spbstu.gyboml.core.PlayerType.SECOND_PLAYER
import ru.spbstu.gyboml.core.net.SessionRequests
import ru.spbstu.gyboml.core.net.SessionResponses
import ru.spbstu.gyboml.server.Controller
import ru.spbstu.gyboml.server.GybomlConnection
import ru.spbstu.gyboml.server.game.Game

class SessionListener(private val controller: Controller) : Listener() {
    override fun disconnected(connection: Connection?) {
        connection as GybomlConnection

        // if player in session
        if (connection.player != null && connection.inSession) {
            controller.removeFromSession(connection.player!!.sessionId, connection.player!!.type)
            controller.notifyAllPlayers()
        }
    }

    override fun received(connection: Connection?, request: Any?) {
        connection as GybomlConnection

        when (request) {

            is SessionRequests.RegisterName -> {
                if (connection.player != null) return
                val name = request.playerName.trim()

                if (name.isEmpty()) return
                connection.player = Player(name, FIRST_PLAYER)
                info("Player ${connection.player!!.name} created")
                connection.sendTCP(SessionResponses.NameRegistred())
            }

            is SessionRequests.GetSessions -> controller.notifyOne(connection)

            is SessionRequests.ConnectSession -> {
                if (connection.player == null || connection.inSession) return
                val session = controller.getSession(request.sessionId) ?: return
                if (session.spaces() == 0) return

                connection.player!!.ready = false
                session.add(connection, connection.player!!)
                connection.inSession = true
                connection.player!!.setSessionId(request.sessionId)

                info("Player ${connection.player!!.name} connected to session ${session.id}")
                if (connection.player == null)
                    assert(true)
                connection.sendTCP(SessionResponses.SessionConnected(connection.player))
                controller.notifyAllPlayers()
            }

            is SessionRequests.ExitSession -> {
                if (connection.player == null) return

                // TODO: REMOVE `player` field FROM SessionRequests.ExitSession
                controller.removeFromSession(request.player.sessionId, request.player.type)
                connection.inSession = false

                info("Player ${connection.player!!.name} exited from session")
                connection.sendTCP(SessionResponses.SessionExited())
                controller.notifyAllPlayers()
            }

            is SessionRequests.CreateSession -> {
                if (connection.player == null) return

                val name = request.sessionName
                if (name.trim().isEmpty()) return

                val id = controller.addSession(name)
                info("Player ${connection.player!!.name} created session $name")
                connection.sendTCP(SessionResponses.SessionCreated(id))

                controller.notifyAllPlayers()
            }

            is SessionRequests.Ready -> {
                if (connection.player == null) return
                val session: Session = controller.getSession(request.player.sessionId) ?: return
                session.setReady(connection.player!!.type, !request.player.ready)

                info("Player ${connection.player!!.name} ready = ${!request.player.ready}")
                connection.sendTCP(SessionResponses.ReadyApproved(!request.player.ready))

                controller.notifySessionPlayers(connection.player!!.sessionId)

                with (session) {
                    if (firstPlayer != null && secondPlayer != null &&
                        firstPlayer!!.player!!.ready && secondPlayer!!.player!!.ready) {
                        info("Session $id starting...")

                        firstPlayer!!.player!!.type = FIRST_PLAYER
                        secondPlayer!!.player!!.type = SECOND_PLAYER
                        firstPlayer!!.player!!.ready = false
                        secondPlayer!!.player!!.ready = false
                        firstPlayer!!.connection.sendTCP(SessionResponses.SessionStarted(firstPlayer!!.player))
                        secondPlayer!!.connection.sendTCP(SessionResponses.SessionStarted(secondPlayer!!.player))

                        session.game = Game(firstPlayer!!.player!!, secondPlayer!!.player!!)
                    }
                }
            }
        }
    }
}