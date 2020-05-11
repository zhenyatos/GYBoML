package ru.spbstu.gyboml.server

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server
import ru.spbstu.gyboml.core.net.Network
import ru.spbstu.gyboml.server.game.GameListener
import ru.spbstu.gyboml.server.session.SessionListener

fun main(args: Array<String>) {
    val server = object : Server(){
        override fun newConnection(): Connection {
            return GybomlConnection()
        }
    }
    val controller = Controller(server)

    Network.register(server)
    server.addListener(SessionListener(controller))
    server.addListener(GameListener(controller))
    server.bind(Network.tcpPort, Network.udpPort)
    server.start()
}