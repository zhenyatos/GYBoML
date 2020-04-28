package ru.spbstu.gyboml.core.net

import com.badlogic.gdx.math.Vector2

class GameRequests {
    class Shoot(var ballPosition: Vector2 = Vector2(), var ballVelocity: Vector2 = Vector2())
    class GameExit
}