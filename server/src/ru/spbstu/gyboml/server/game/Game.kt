package ru.spbstu.gyboml.server.game

import ru.spbstu.gyboml.core.Player
import ru.spbstu.gyboml.server.session.Session

enum class Stage {
    FIRST_PLAYER_ATTACK,
    SECOND_PLAYER_ATTACK,
    BUILDING;

    fun reverted() = when(this) {
        FIRST_PLAYER_ATTACK -> SECOND_PLAYER_ATTACK
        SECOND_PLAYER_ATTACK -> FIRST_PLAYER_ATTACK
        BUILDING -> BUILDING
    }
}

data class Game(val session: Session, val firstPlayer: Player = Player(),
                val secondPlayer: Player = Player(),
                var stage: Stage = Stage.FIRST_PLAYER_ATTACK)