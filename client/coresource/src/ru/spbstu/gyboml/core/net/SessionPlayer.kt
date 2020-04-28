package ru.spbstu.gyboml.core.net

import ru.spbstu.gyboml.core.PlayerType
import ru.spbstu.gyboml.core.PlayerType.FIRST_PLAYER

data class SessionPlayer(var sessionId: Int? = null,
                         var name: String = "default",
                         var type: PlayerType = FIRST_PLAYER,
                         var ready: Boolean = false) {
    init {
        if (name.isEmpty()) name = "default"
    }
}