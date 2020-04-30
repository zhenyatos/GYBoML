package ru.spbstu.gyboml

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import ru.spbstu.gyboml.core.net.Network
import ru.spbstu.gyboml.core.net.SessionPlayer
import java.io.IOException
import kotlin.concurrent.thread

object GybomlClient {
    var client: Client? = null

    fun connect(activity: Activity, connectedReaction: () -> Unit) {
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
                catch (ex: IOException) {
                    activity.runOnUiThread {
                        val toast = Toast.makeText(activity, "Couldn't connect to server", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
                        toast.show()
                    }
                }
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
}