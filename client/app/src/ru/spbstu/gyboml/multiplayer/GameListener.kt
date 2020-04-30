package ru.spbstu.gyboml.multiplayer

import com.badlogic.gdx.Gdx
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import ru.spbstu.gyboml.core.net.GameMessage.*
import ru.spbstu.gyboml.core.net.GameResponses.*

class GameListener(private val game: Game) : Listener() {
    override fun received(connection: Connection?, message: Any?) = when (message) {
        is GameExited -> { game.dispose(); Gdx.app.exit() }

        is CreateBlock -> game.physicalScene.createBlock(message)
        is UpdateBlock -> game.physicalScene.updateBlock(message)
        is RemoveBlock -> game.physicalScene.removeBlock(message)

        is CreateShot -> game.physicalScene.createShot(message)
        is UpdateShot -> game.physicalScene.updateShot(message)
        is RemoveShot -> game.physicalScene.removeShot()

        else -> {}
    }
}