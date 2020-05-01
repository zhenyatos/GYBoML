package ru.spbstu.gyboml.core.net

import com.badlogic.gdx.math.Vector2
import ru.spbstu.gyboml.core.PlayerType
import ru.spbstu.gyboml.core.PlayerType.FIRST_PLAYER
import ru.spbstu.gyboml.core.destructible.Material
import ru.spbstu.gyboml.core.shot.ShotType
import ru.spbstu.gyboml.core.shot.ShotType.BASIC

open class GameMessage {

    /* Update block, do nothing if not exist */
    class UpdateBlock(var id: Int = 0,
                      var position: Vector2 = Vector2(), var angle: Float = 0f,
                      var linearVelocity: Vector2 = Vector2(), var angularVelocity: Float = 0f) : GameMessage()

    /** Create block if there is no block with same id */
    class CreateBlock(var firstBlock: Boolean = true,
                      var position: Vector2 = Vector2(), var angle: Float = 0f,
                      var material: Material = Material.WOOD) : GameMessage()

    /** Remove block with specific id, if exists */
    class RemoveBlock(var id: Int = 0) : GameMessage()

    class CreateShot(var side: PlayerType = FIRST_PLAYER, var type: ShotType = BASIC) : GameMessage()

    /** Create ball with that parameters if not exist, and update if exists
     *  If shot not exist, do nothing
     */
    class UpdateShot(var position: Vector2 = Vector2(), var angle: Float = 0f,
                     var linearVelocity: Vector2 = Vector2(), var angularVelocity: Float = 0f) : GameMessage()

    /** Remove ball if exist */
    class RemoveShot : GameMessage()

    class UpdateHP(var HP1: Int = 0, var HP2: Int = 0)

    class UpdateTower(var firstTower: Boolean = true)
}