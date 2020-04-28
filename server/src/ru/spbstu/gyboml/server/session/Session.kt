package ru.spbstu.gyboml.server.session

import ru.spbstu.gyboml.core.PlayerType
import ru.spbstu.gyboml.core.PlayerType.FIRST_PLAYER
import ru.spbstu.gyboml.core.PlayerType.SECOND_PLAYER
import ru.spbstu.gyboml.core.net.SessionInfo
import ru.spbstu.gyboml.core.net.SessionPlayer
import ru.spbstu.gyboml.server.GybomlConnection
import ru.spbstu.gyboml.server.game.Game

class Session internal constructor(val id: Int, val name: String) {

    var firstPlayer : NetPlayer? = null
    var secondPlayer : NetPlayer? = null
    var game : Game? = null

    companion object {
        private var nextAvailableSessionId = 0
        fun createSession(name: String): Session = Session(nextAvailableSessionId++, name)
    }

    fun add(connection: GybomlConnection, player: SessionPlayer) {
        if (firstPlayer == null) {
            player.type = FIRST_PLAYER
            firstPlayer = NetPlayer(connection, player)
        }
        else if (secondPlayer == null) {
            player.type = SECOND_PLAYER
            secondPlayer = NetPlayer(connection, player)
        }
    }
    fun remove(type: PlayerType) {
        when (type) {
            FIRST_PLAYER -> {
                firstPlayer = secondPlayer
                secondPlayer = null
                firstPlayer?.player?.type = FIRST_PLAYER
            }
            SECOND_PLAYER -> secondPlayer = null
        }
    }
    fun invertReady(type: PlayerType) : Boolean {
        when (type) {
            FIRST_PLAYER -> firstPlayer?.let { that -> that.player?.let { it.ready = !it.ready; return it.ready } }
            SECOND_PLAYER -> secondPlayer?.let { that -> that.player?.let { it.ready = !it.ready; return it.ready } }
        }
        return false;
    }

    fun spaces() = arrayOf(firstPlayer, secondPlayer).filter {it == null}.count()
    fun isStarted(): Boolean = game != null
    fun getPlayer(type: PlayerType) = when(type) {
        FIRST_PLAYER -> firstPlayer
        SECOND_PLAYER -> secondPlayer
    }

    fun toSessionInfo(): SessionInfo =
        SessionInfo(id, spaces(), name, firstPlayer?.player, secondPlayer?.player, isStarted())
}