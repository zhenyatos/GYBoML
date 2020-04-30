package ru.spbstu.gyboml.game

import com.badlogic.gdx.Gdx
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import ru.spbstu.gyboml.GybomlClient
import ru.spbstu.gyboml.core.net.GameResponses.GameExited
import ru.spbstu.gyboml.core.net.GameResponses.Shooted

class GameListener(private val game: Game) : Listener() {
    override fun received(connection: Connection?, response: Any?) {
        when (response) {
            is Shooted -> shoted(response)
            is GameExited -> exited()
        }
    }

    private fun shoted(response: Shooted) {
        synchronized(game) {
            game.physicalScene.generateShot(game.player.type.reverted(), game.shotType)
            game.physicalScene.lastShot.body.setTransform(response.ballPosition, 0f)
            game.physicalScene.lastShot.velocity = response.ballVelocity
            game.graphicalScene.generateGraphicalShot(game.physicalScene.lastShot)
            game.switchTurn()
        }
    }
    private fun exited() = Gdx.app.exit()
}