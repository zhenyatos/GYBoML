package ru.spbstu.gyboml.server.session

import ru.spbstu.gyboml.core.Player
import ru.spbstu.gyboml.server.GybomlConnection

data class NetPlayer(val connection: GybomlConnection, var player: Player? = null)