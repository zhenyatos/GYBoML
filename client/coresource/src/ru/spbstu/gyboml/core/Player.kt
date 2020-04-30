package ru.spbstu.gyboml.core;

import ru.spbstu.gyboml.core.PlayerType.FIRST_PLAYER
import java.io.Serializable

data class Player(var type: PlayerType = FIRST_PLAYER, val name: String = "default_player",
                  var isTurn: Boolean = false, var points: Int = 0) : Serializable
