package ru.spbstu.gyboml.core.net

class GameResponses {
    class PassTurned(val yourTurn: Boolean = true)
    class GameExited
    class GameStarted
}
