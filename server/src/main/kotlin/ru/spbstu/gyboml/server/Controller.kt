package ru.spbstu.gyboml.server

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server
import com.esotericsoftware.minlog.Log.info
import ru.spbstu.gyboml.core.net.Network
import ru.spbstu.gyboml.core.net.SessionResponses
import ru.spbstu.gyboml.server.game.GameListener
import ru.spbstu.gyboml.server.session.Session
import ru.spbstu.gyboml.server.session.Session.Companion.createSession
import ru.spbstu.gyboml.server.session.SessionListener

class Controller() {
    private var sessions = mutableMapOf<Int, Session>()
    private val server = object : Server() {
        override fun newConnection() = GybomlConnection()
    }

    init {
        Network.register(server)
        server.addListener(SessionListener(this))
        server.addListener(GameListener(this))
        server.bind(Network.tcpPort, Network.udpPort)
    }

    fun start() = server.start()

    fun addSession(name: String) : Int {
        val session = createSession(name)
        sessions[session.id] = session
        return session.id
    }

    fun getSession(id: Int) = sessions[id]

    fun removeFromSession(id: Int, connection: GybomlConnection) {
        sessions[id]?.remove(connection)
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
        sessions[id]?.firstConnection?.sendTCP(response)
        sessions[id]?.secondConnection?.sendTCP(response)
    }

    fun notifyOne(connection: Connection) {
        connection.sendTCP(getSessionsResponse())
    }

    private fun getSessionsResponse() =
            SessionResponses.TakeSessions(sessions.values.map(Session::toSessionInfo))
}