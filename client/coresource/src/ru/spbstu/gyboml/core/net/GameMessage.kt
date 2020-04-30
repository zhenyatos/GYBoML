package ru.spbstu.gyboml.core.net

import com.badlogic.gdx.math.Vector2
import ru.spbstu.gyboml.core.PlayerType
import ru.spbstu.gyboml.core.destructible.Material
import ru.spbstu.gyboml.core.shot.ShotType

open class GameMessage {

    /* Update block, do nothing if not exist */
    class UpdateBlock(var id: Int = 0,
                      var position: Vector2 = Vector2(), var angle: Float = 0f,
                      var linearVelocity: Vector2 = Vector2(), var angularVelocity: Float = 0f) : GameMessage()

    /** Create block if there is no block with same id */
    class CreateBlock(var id: Int = 0, var firstBlock: Boolean = true,
                      var position: Vector2 = Vector2(), var angle: Float = 0f,
                      var linearVelocity: Vector2 = Vector2(), var angularVelocity: Float = 0f,
                      var material: Material = Material.WOOD) : GameMessage()

    /** Remove block with specific id, if exists */
    class RemoveBlock(var id: Int = 0) : GameMessage()

    class CreateShot(var turn: PlayerType, var type: ShotType) : GameMessage()

    /** Create ball with that parameters if not exist, and update if exists
     *  If shot not exist, do nothing
     */
    class UpdateShot(var position: Vector2 = Vector2(), var velocity: Vector2 = Vector2()) : GameMessage()

    /** Remove ball if exist */
    class RemoveShot : GameMessage()

    class UpdateHP(var HP1: Int, var HP2: Int)
}