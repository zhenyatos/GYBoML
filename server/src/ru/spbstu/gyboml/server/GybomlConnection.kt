package ru.spbstu.gyboml.server

import com.esotericsoftware.kryonet.Connection
import ru.spbstu.gyboml.core.Player

data class GybomlConnection(var player: Player? = null, var inSession: Boolean = false)
    : Connection()