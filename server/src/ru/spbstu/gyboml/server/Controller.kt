package ru.spbstu.gyboml.server

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server
import com.esotericsoftware.minlog.Log.info
import ru.spbstu.gyboml.core.PlayerType
import ru.spbstu.gyboml.core.net.SessionResponses
import ru.spbstu.gyboml.server.session.Session
import ru.spbstu.gyboml.server.session.Session.Companion.createSession

class Controller(private val server: Server) {
    private var sessions = mutableMapOf<Int, Session>()

    fun addSession(name: String) : Int {
        val session = createSession(name)
        sessions[session.id] = session
        return session.id
    }

    fun getSession(id: Int) = sessions[id]

    fun removeFromSession(id: Int, type: PlayerType) {
        sessions[id]?.remove(type)
        if (sessions[id]?.spaces() == 2) {
            info("Session ${sessions[id]?.name} deleted")
            sessions.remove(id)
        }
    }

    fun notifyAllPlayers() {
        server.sendToAllTCP(getSessionsResponse())
    }

    fun notifySessionPlayers(id: Int) {
        val response = getSessionsResponse()
        sessions[id]?.firstPlayer?.connection?.sendTCP(response)
        sessions[id]?.secondPlayer?.connection?.sendTCP(response)
    }

    fun notifyOne(connection: Connection) {
        connection.sendTCP(getSessionsResponse())
    }

    private fun getSessionsResponse() =
            SessionResponses.TakeSessions(sessions.values.map(Session::toSessionInfo))
}