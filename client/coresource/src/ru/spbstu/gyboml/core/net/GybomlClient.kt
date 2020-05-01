package ru.spbstu.gyboml.core.net

import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import java.io.IOException
import kotlin.concurrent.thread

object GybomlClient {
    var client: Client? = null

    fun connect(connectedReaction: () -> Unit = {}, notConnectedReaction: () -> Unit = {}) {
        client?.close()
        client = Client()
        client?.let {
            it.start()
            Network.register(it)
            it.addListener(object : Listener() {
                override fun connected(connection: Connection?) = connectedReaction()
            })
            thread {
                try { it.connect(5000, Network.address, Network.tcpPort, Network.udpPort) }
                catch (ex: IOException) { notConnectedReaction() }
            }
        }
    }

    fun addListener(listener: Listener) {
        client?.addListener(listener)
    }

    fun disconnect() {
        client?.close()
        client = null
    }

    fun sendTCP(obj: Any) {
        thread {
            client?.sendTCP(obj)
        }
    }

    fun sendUDP(obj: Any) {
        thread {
            client?.sendUDP(obj)
        }
    }
}