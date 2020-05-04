package ru.spbstu.gyboml.server.session

import com.esotericsoftware.kryonet.Connection
import ru.spbstu.gyboml.core.net.SessionInfo
import ru.spbstu.gyboml.server.Controller
import ru.spbstu.gyboml.server.GybomlConnection
import ru.spbstu.gyboml.server.game.Game

class Session internal constructor(val id: Int, val name: String) {

    var firstConnection: GybomlConnection? = null
    var secondConnection : GybomlConnection? = null
    var game : Game? = null

    companion object {
        private var nextAvailableSessionId = 0
        fun createSession(name: String): Session = Session(nextAvailableSessionId++, name)
        fun extractSessionAndDo(controller: Controller, connection: GybomlConnection, reaction: (Session) -> Unit) {
            connection.player?.let { player ->
                player.sessionId?.let { sessionId ->
                    val session = controller.getSession(sessionId) ?: return
                    if (!session.isStarted()) return

                    reaction(session)
                }
            }
        }
    }

    fun add(connection: GybomlConnection) {
        if (firstConnection == null) firstConnection = connection
        else if (secondConnection == null) secondConnection = connection
    }
    fun remove(connection: GybomlConnection) {
        if (connection == firstConnection) firstConnection = secondConnection
        else if (connection != secondConnection) return

        secondConnection = null
    }
    fun invertReady(connection: GybomlConnection) {
        if (connection == firstConnection) firstConnection?.let { it.player?.let { p -> p.ready = !p.ready }}
        else if (connection == secondConnection) secondConnection?.let { it.player?.let { p -> p.ready = !p.ready }}
    }
    fun getOther(connection: Connection): GybomlConnection? = when (connection) {
        firstConnection -> secondConnection
        secondConnection -> firstConnection
        else -> null
    }

    fun spaces() = arrayOf(firstConnection, secondConnection).filter {it == null}.count()
    fun isStarted(): Boolean = game != null
    fun toSessionInfo(): SessionInfo =
        SessionInfo(id, spaces(), name, firstConnection?.player, secondConnection?.player, isStarted())
}