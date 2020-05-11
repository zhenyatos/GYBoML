package ru.spbstu.gyboml.server.game

import ru.spbstu.gyboml.core.Player

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

data class Game(val firstPlayer: Player, val secondPlayer: Player,
                var stage: Stage = Stage.FIRST_PLAYER_ATTACK)