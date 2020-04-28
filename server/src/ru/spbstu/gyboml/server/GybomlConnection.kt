package ru.spbstu.gyboml.server

import com.esotericsoftware.kryonet.Connection
import ru.spbstu.gyboml.core.Player
import ru.spbstu.gyboml.core.net.SessionPlayer

data class GybomlConnection(var player: SessionPlayer? = null) : Connection()