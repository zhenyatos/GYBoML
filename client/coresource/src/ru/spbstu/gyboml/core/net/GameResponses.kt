package ru.spbstu.gyboml.core.net

import com.badlogic.gdx.math.Vector2

class GameResponses {
    class Shooted(val ballPosition: Vector2, val ballVelocity: Vector2)
    class PassTurned(val yourTurn: Boolean)
    class GameExited
}
