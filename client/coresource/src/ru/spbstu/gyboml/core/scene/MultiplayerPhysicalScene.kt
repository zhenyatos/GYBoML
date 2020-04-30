package ru.spbstu.gyboml.core.scene

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import org.apache.commons.lang3.time.StopWatch
import ru.spbstu.gyboml.core.PlayerType
import ru.spbstu.gyboml.core.PlayerType.FIRST_PLAYER
import ru.spbstu.gyboml.core.PlayerType.SECOND_PLAYER
import ru.spbstu.gyboml.core.destructible.Material
import ru.spbstu.gyboml.core.event.EventSystem
import ru.spbstu.gyboml.core.net.GameMessage
import ru.spbstu.gyboml.core.net.GameMessage.*
import ru.spbstu.gyboml.core.net.GybomlClient
import ru.spbstu.gyboml.core.physical.*
import ru.spbstu.gyboml.core.shot.ShotType
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class MultiplayerPhysicalScene(private val graphicalScene: GraphicalScene, val playerType: PlayerType) {

    // scene objects
    val world: World
    private var movables = mutableMapOf<Int, Movable>()
    private var firstBlocks = mutableMapOf<Int, PhysicalBlock>()
    private var secondBlocks = mutableMapOf<Int, PhysicalBlock>()
    private val firstCastle: PhysicalCastle
    private val secondCastle: PhysicalCastle
    private val firstTower: PhysicalTower
    private val secondTower: PhysicalTower
    private val background: PhysicalBackground
    private var shot: PhysicalShot? = null // null = not exist

    private val watch = StopWatch()
    private var time = 0f
    private var accumulator = 0f

    companion object {
        val gravity = Vector2(0f, -10f)
        const val step = 1 / 60f
        const val positionIterations = 2
        const val velocityIterations = 6
        const val baseHP = 100f

        // object placement
        val castleIndentX = 860
        val towerIndentX = 450;
        val platformIndentY = 364;
        val castleTextureWidth = SceneConstants.castleWidth * SceneConstants.CASTLES_SCALE
        val towerTextureWidth = SceneConstants.towerWidth * SceneConstants.TOWERS_SCALE
        val castleP1 = Vector2(SceneConstants.backgroundX + castleIndentX * SceneConstants.SCALE,
                SceneConstants.backgroundY + platformIndentY * SceneConstants.SCALE)
        val castleP2 = Vector2(SceneConstants.backgroundX + (SceneConstants.resolutionWidth - castleIndentX) * SceneConstants.SCALE - castleTextureWidth,
                SceneConstants.backgroundY + platformIndentY * SceneConstants.SCALE)
        val towerP1 = Vector2(SceneConstants.backgroundX + towerIndentX * SceneConstants.SCALE,
                SceneConstants.backgroundY + platformIndentY * SceneConstants.SCALE)
        val towerP2 = Vector2(SceneConstants.backgroundX + (SceneConstants.resolutionWidth - towerIndentX) * SceneConstants.SCALE - towerTextureWidth,
                SceneConstants.backgroundY + platformIndentY * SceneConstants.SCALE)

        private var nextAvailableId = 0
    }

    init {
        world = World(gravity, true)
        world.setContactListener(CollisionHandler())

        // fill-in physical objects
        firstTower =
                PhysicalTower(Location(towerP1.x, towerP1.y, 0f, SceneConstants.TOWERS_SCALE), FIRST_PLAYER, world)
        secondTower =
                PhysicalTower(Location(towerP2.x, towerP2.y, 0f, SceneConstants.TOWERS_SCALE), SECOND_PLAYER, world)
        firstCastle =
                PhysicalCastle(baseHP, Location(castleP1.x, castleP1.y, 0f, SceneConstants.CASTLES_SCALE), FIRST_PLAYER, world)
        secondCastle =
                PhysicalCastle(baseHP, Location(castleP2.x, castleP2.y, 0f, SceneConstants.CASTLES_SCALE), SECOND_PLAYER, world)
        background =
                PhysicalBackground(Location(SceneConstants.backgroundX, SceneConstants.backgroundY, 0f, SceneConstants.SCALE), world)
        addMovable(firstTower, secondTower)

        graphicalScene?.let {
            it.generateGraphicalBackground(background)
            it.generateGraphicalCastle(firstCastle)
            it.generateGraphicalCastle(secondCastle)
            it.generateGraphicalTower(firstTower)
            it.generateGraphicalTower(secondTower)
            it.bindBlocksGraphics(firstBlocks as ArrayList<PhysicalBlock>?, secondBlocks as ArrayList<PhysicalBlock>?)
            graphicalScene.generateGraphicalForeground(background)
        }
    }

    // add or update movable
    private fun addMovable(vararg movable: Movable) {
        for (m in movable) {
            if (m.id == null) m.id = nextAvailableId
            movables[m.id] = m
        };
    }

    private fun addBlock(firstBlocks: Boolean, vararg blocks: PhysicalBlock) {
        for (b in blocks) {
            addMovable(b) // this will set up new id for b if it has not
            if (firstBlocks) this.firstBlocks[b.id] = b
            else this.secondBlocks[b.id] = b
        }
    }

    fun setUpBlocks(material: Material) {
        val blockP1X: Float = castleP1.x + (SceneConstants.castleWidth + 60) * SceneConstants.SCALE
        val blockP1Y: Float = castleP1.y + 240 * SceneConstants.SCALE
        val blockP2X: Float = castleP2.x - 60 * SceneConstants.SCALE - SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE
        val blockP2Y: Float = castleP2.y + 240 * SceneConstants.SCALE

        // 1st row
        addBlock(true,
                PhysicalBlock(material, Location(blockP1X, blockP1Y, 0f, SceneConstants.BLOCKS_SCALE), world),
                PhysicalBlock(material, Location(blockP1X, blockP1Y + 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0f, SceneConstants.BLOCKS_SCALE), world),
                PhysicalBlock(material, Location(blockP1X, blockP1Y + 2 * 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0f, SceneConstants.BLOCKS_SCALE), world)
        )
        addBlock(false,
                PhysicalBlock(material, Location(blockP2X, blockP2Y, 0f, SceneConstants.BLOCKS_SCALE), world),
                PhysicalBlock(material, Location(blockP2X, blockP2Y + 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0f, SceneConstants.BLOCKS_SCALE), world),
                PhysicalBlock(material, Location(blockP2X, blockP2Y + 2 * 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0f, SceneConstants.BLOCKS_SCALE), world)
        )

        // 2nd row
        addBlock(true,
                PhysicalBlock(material, Location(blockP1X + 2 * SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP1Y, 0f, SceneConstants.BLOCKS_SCALE), world),
                PhysicalBlock(material, Location(blockP1X + 2 * SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP1Y + 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0f, SceneConstants.BLOCKS_SCALE), world)
        )
        addBlock(false,
                PhysicalBlock(material, Location(blockP2X - 2 * SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP2Y, 0f, SceneConstants.BLOCKS_SCALE), world),
                PhysicalBlock(material, Location(blockP2X - 2 * SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP2Y + 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0f, SceneConstants.BLOCKS_SCALE), world)
        )

        // 3rd row
        addBlock(true,
                PhysicalBlock(material, Location(blockP1X + 4 * SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP1Y, 0f, SceneConstants.BLOCKS_SCALE), world)
        )
        addBlock(false,
                PhysicalBlock(material, Location(blockP2X - 4 * SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP2Y, 0f, SceneConstants.BLOCKS_SCALE), world)
        )

        // back row
        addBlock(true,
                PhysicalBlock(material, Location(castleP1.x - 60 * SceneConstants.SCALE - SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP1Y, 0f, SceneConstants.BLOCKS_SCALE), world),
                PhysicalBlock(material, Location(castleP1.x - 60 * SceneConstants.SCALE - SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP1Y + 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0f, SceneConstants.BLOCKS_SCALE), world)
        )
        addBlock(false,
                PhysicalBlock(material, Location(castleP2.x + (SceneConstants.castleWidth + 60) * SceneConstants.SCALE, blockP2Y, 0f, SceneConstants.BLOCKS_SCALE), world),
                PhysicalBlock(material, Location(castleP2.x + (SceneConstants.castleWidth + 60) * SceneConstants.SCALE, blockP2Y + 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0f, SceneConstants.BLOCKS_SCALE), world)
        )
    }

    fun generateShot(turn: PlayerType, type: ShotType) {
        val sign = if (turn == FIRST_PLAYER) 1 else -1
        val angle = if (turn == FIRST_PLAYER) firstTower.movablePartAngle else secondTower.movablePartAngle
        val jointPosition = if (turn == FIRST_PLAYER) firstTower.jointPosition else secondTower.jointPosition

        val cos = cos(angle)
        val sin = sin(angle)
        val barrelLength = firstTower.barrelLength
        val shotX = jointPosition.x + sign * barrelLength * cos - SceneConstants.shotBasicWidth / 2f * SceneConstants.SHOTS_SCALE
        val shotY = jointPosition.y + sign * barrelLength * sin - SceneConstants.shotBasicHeight / 2f * SceneConstants.SHOTS_SCALE

        val location = Location(shotX, shotY, 0f, SceneConstants.SHOTS_SCALE)

        // todo: add more shot types
        val shot = PhysicalBasicShot(location, world)
        shot.playerType = turn
        shot.velocity = Vector2(sign * 25f * cos, sign * 25f * sin)

        synchronized(movables) { addMovable(shot) }
        this.shot = shot

        graphicalScene.generateGraphicalShot(this.shot);

        // todo: play sound
    }

    /**
     * Method updates current world state AND returns all messages, that
     * needed to be sent to both players
     */
    fun stepWorld(type: PlayerType) {
        val messages = mutableListOf<GameMessage>()

        if (!watch.isStarted()) {
            watch.start()
            time = watch.time * 1000f
        }

        watch.split()
        val currentTime = watch.splitTime * 1000f
        accumulator += min(currentTime - time, 0.25f)
        time = currentTime

        if (accumulator >= step) {
            accumulator -= step
            world.step(step, velocityIterations, positionIterations)

            synchronized(movables) { movables.values.forEach { it.updateSprite() } }

            // collect corps (only for first player)
            if (type == FIRST_PLAYER) notifyAboutDeadBodies()
        }
    }

    private fun notifyAboutDeadBodies() {
        firstBlocks.filter { it.value.hp <= 0 }.forEach { GybomlClient.sendTCP(RemoveBlock(it.key)) }
        secondBlocks.filter { it.value.hp <= 0 }.forEach { GybomlClient.sendTCP(RemoveBlock(it.key)) }
        if (shot?.velocity?.isZero(.1f) ?: return) GybomlClient.sendTCP(RemoveShot())
    }

    fun connectWithHPBar(type: PlayerType, bar: HPBar) =
        if (type == FIRST_PLAYER) EventSystem.get().connect(firstCastle, "handleDamage", bar, "update")
        else EventSystem.get().connect(secondCastle, "handleDamage", bar, "update")
    fun connectWithSoundEffects(effects: SoundEffects) {
        EventSystem.get().connect(this, "generateShot", effects, "playShot")

        // TODO: remove, because we don't want to connect each time by creation a new one
        for (block in firstBlocks) EventSystem.get().connect(block, "handleDamage", effects, "playWood")
        for (block in secondBlocks) EventSystem.get().connect(block, "handleDamage", effects, "playWood")
    }
    fun connectWithGameOver(type: PlayerType, gameOver: GameOver) {
        if (type == FIRST_PLAYER) {
            EventSystem.get().connect(secondCastle, "handleDamage", gameOver, "victoryCheck")
            EventSystem.get().connect(firstCastle, "handleDamage", gameOver, "defeatCheck")
        } else {
            EventSystem.get().connect(firstCastle, "handleDamage", gameOver, "victoryCheck")
            EventSystem.get().connect(secondCastle, "handleDamage", gameOver, "defeatCheck")
        }
    }
    fun getTowerAngle(type: PlayerType) = if (type == FIRST_PLAYER) firstTower.movablePartAngle else secondTower.movablePartAngle

    // block operations
    fun updateBlock(message: UpdateBlock) {
        if (!movables.contains(message.id)) return
        val block = movables[message.id] as PhysicalBlock
        block.body.setTransform(message.position, message.angle)
        block.body.linearVelocity = message.linearVelocity
        block.body.angularVelocity = message.angularVelocity
    }
    fun createBlock(message: CreateBlock) {
        addBlock(message.firstBlock,
                PhysicalBlock(message.material, Location(message.position.x, message.position.y, message.angle,
                SceneConstants.BLOCKS_SCALE), world)
        )
    }
    fun removeBlock(message: RemoveBlock) {
        if (movables.containsKey(message.id)) {
            val block = movables[message.id] as PhysicalBlock
            world.destroyBody(block.body)
            graphicalScene.removeObject(block)
            movables.remove(message.id)
            firstBlocks.remove(message.id)
            secondBlocks.remove(message.id)
        }
    }

    // shot operations
    fun updateShot(message: UpdateShot) {
        shot?.body?.setTransform(message.position, 0f)
        shot?.velocity = message.velocity
    }
    fun createShot(message: CreateShot) = generateShot(message.turn, message.type)
    fun removeShot() {
        shot?.let {
            world.destroyBody(it.body)
            movables.remove(it.id)
            graphicalScene.removeObject(it)

            this.shot = null
        }
    }
}