package ru.spbstu.gyboml.core.scene;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.destructible.Material;
import ru.spbstu.gyboml.core.event.EventSystem;
import ru.spbstu.gyboml.core.physical.CollisionHandler;
import ru.spbstu.gyboml.core.physical.Location;
import ru.spbstu.gyboml.core.physical.Movable;
import ru.spbstu.gyboml.core.physical.PhysicalBackground;
import ru.spbstu.gyboml.core.physical.PhysicalBasicShot;
import ru.spbstu.gyboml.core.physical.PhysicalBlock;
import ru.spbstu.gyboml.core.physical.PhysicalCastle;
import ru.spbstu.gyboml.core.physical.PhysicalShot;
import ru.spbstu.gyboml.core.physical.PhysicalTower;
import ru.spbstu.gyboml.core.shot.ShotType;

/**
 * Initialized during client creation.
 * Builds physical scene for client application.
 */
public class PhysicalScene {
    private final GraphicalScene graphicalScene;

    private final World world;

    private final StopWatch stopWatch = new StopWatch();
    private float previousTime;

    private final List<Movable> movables;
    private PhysicalBackground physicalBackground;
    private PhysicalCastle physicalCastleP1;
    private PhysicalCastle physicalCastleP2;
    private PhysicalTower physicalTowerP1;
    private PhysicalTower physicalTowerP2;
    private ArrayList<PhysicalBlock> physicalBlocksP1;
    private ArrayList<PhysicalBlock> physicalBlocksP2;
    private ArrayList<PhysicalShot> physicalShots;

    // physics
    private static final float gravityAccelerationX = 0f;
    private static final float gravityAccelerationY = -10f;
    private static final float STEP_TIME = 1f / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private float accumulator = 0;

    public PhysicalScene(GraphicalScene graphicalScene) {
        this.graphicalScene = graphicalScene;

        world = new World(new Vector2(gravityAccelerationX, gravityAccelerationY), true);
        world.setContactListener(new CollisionHandler());

        movables = new ArrayList<>();
        physicalBlocksP1 = new ArrayList<>();
        physicalBlocksP2 = new ArrayList<>();
        physicalShots = new ArrayList<>();

        final float castleIndentX   = 860;  // manually set value for castle placement on platform
        final float towerIndentX    = 450;  // manually set value for tower  placement on platform
        final float platformIndentY = 364;  // y position of platforms for objects from background.xml
        final float castleTextureWidth = SceneConstants.castleWidth;
        final float towerTextureWidth  = SceneConstants.towerWidth;

        float castleP1X = SceneConstants.backgroundX + castleIndentX * SceneConstants.SCALE;
        float castleP2X = SceneConstants.backgroundX + (SceneConstants.resolutionWidth - castleIndentX - castleTextureWidth) * SceneConstants.SCALE;
        float castleP1Y = SceneConstants.backgroundY + platformIndentY * SceneConstants.SCALE;
        float castleP2Y = SceneConstants.backgroundY + platformIndentY * SceneConstants.SCALE;
        float towerP1X = SceneConstants.backgroundX + towerIndentX * SceneConstants.SCALE;
        float towerP2X = SceneConstants.backgroundX + (SceneConstants.resolutionWidth - towerIndentX - towerTextureWidth) * SceneConstants.SCALE;
        float towerP1Y = SceneConstants.backgroundY + platformIndentY * SceneConstants.SCALE;
        float towerP2Y = SceneConstants.backgroundY + platformIndentY * SceneConstants.SCALE;

        physicalBackground = new PhysicalBackground(new Location(SceneConstants.backgroundX, SceneConstants.backgroundY, 0, SceneConstants.SCALE), world);
        physicalCastleP1 = new PhysicalCastle(100, new Location(castleP1X, castleP1Y, 0, SceneConstants.SCALE), PlayerType.FIRST_PLAYER, world);
        physicalCastleP2 = new PhysicalCastle(100, new Location(castleP2X, castleP2Y, 0, SceneConstants.SCALE), PlayerType.SECOND_PLAYER, world);
        physicalTowerP1 = new PhysicalTower(new Location(towerP1X, towerP1Y, 0, SceneConstants.SCALE), PlayerType.FIRST_PLAYER, world);
        physicalTowerP2 = new PhysicalTower(new Location(towerP2X, towerP2Y, 0, SceneConstants.SCALE), PlayerType.SECOND_PLAYER, world);
        movables.add(physicalTowerP1);
        movables.add(physicalTowerP2);
        placeDefaultBlocks(castleP1X, castleP1Y, castleP2X, castleP2Y, Material.WOOD);
        movables.addAll(physicalBlocksP1);
        movables.addAll(physicalBlocksP2);

        if (graphicalScene != null) {
            graphicalScene.generateGraphicalBackground(physicalBackground);
            graphicalScene.generateGraphicalCastle(physicalCastleP1);
            graphicalScene.generateGraphicalCastle(physicalCastleP2);
            graphicalScene.generateGraphicalTower(physicalTowerP1);
            graphicalScene.generateGraphicalTower(physicalTowerP2);
            graphicalScene.bindBlocksGraphics(physicalBlocksP1, physicalBlocksP2);
            graphicalScene.generateGraphicalForeground(physicalBackground);
        }
    }

    /**
     * This method is implemented for demo. Called within buildScene() method.
     * Fills physicalBlocksP1 and physicalBlocksP2 array lists with
     * several blocks placed by default same for both players.
     * Blocks placed manually in general.
     */
    private void placeDefaultBlocks(float castleP1X, float castleP1Y, float castleP2X, float castleP2Y, Material material) {
        float blockP1X = castleP1X + (SceneConstants.castleWidth + 60) * SceneConstants.SCALE;
        float blockP1Y = castleP1Y + 240 * SceneConstants.SCALE;
        float blockP2X = castleP2X -  60 * SceneConstants.SCALE - SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE;
        float blockP2Y = castleP2Y + 240 * SceneConstants.SCALE;

        // 1st row
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(blockP1X, blockP1Y, 0, SceneConstants.BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(blockP2X, blockP2Y, 0, SceneConstants.BLOCKS_SCALE), world));
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(blockP1X, blockP1Y + 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0, SceneConstants.BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(blockP2X, blockP2Y + 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0, SceneConstants.BLOCKS_SCALE), world));
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(blockP1X, blockP1Y + 2 * 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0, SceneConstants.BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(blockP2X, blockP2Y + 2 * 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0, SceneConstants.BLOCKS_SCALE), world));

        // 2nd row
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(blockP1X + 2 * SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP1Y, 0, SceneConstants.BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(blockP2X - 2 * SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP2Y, 0, SceneConstants.BLOCKS_SCALE), world));
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(blockP1X + 2 * SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP1Y + 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0, SceneConstants.BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(blockP2X - 2 * SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP2Y + 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0, SceneConstants.BLOCKS_SCALE), world));

        // 3rd row
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(blockP1X + 4 * SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP1Y, 0, SceneConstants.BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(blockP2X - 4 * SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP2Y, 0, SceneConstants.BLOCKS_SCALE), world));

        // back row
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(castleP1X - 60 * SceneConstants.SCALE - SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP1Y, 0, SceneConstants.BLOCKS_SCALE), world));
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(castleP1X - 60 * SceneConstants.SCALE - SceneConstants.blockWoodWidth * SceneConstants.BLOCKS_SCALE, blockP1Y + 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0, SceneConstants.BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(castleP2X + (SceneConstants.castleWidth + 60) * SceneConstants.SCALE, blockP2Y, 0, SceneConstants.BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(castleP2X + (SceneConstants.castleWidth + 60) * SceneConstants.SCALE, blockP2Y + 1.2f * SceneConstants.blockWoodHeight * SceneConstants.BLOCKS_SCALE, 0, SceneConstants.BLOCKS_SCALE), world));
    }

    public void generateShot(PlayerType playerTurn, ShotType shotType) {
        int sign;
        float angle;
        Vector2 jointPosition;

        if (playerTurn == PlayerType.FIRST_PLAYER) {
            sign = 1;
            angle = physicalTowerP1.getMovablePartAngle();
            jointPosition = physicalTowerP1.getJointPosition();
        }
        else {
            sign = -1;
            angle = physicalTowerP2.getMovablePartAngle();
            jointPosition = physicalTowerP2.getJointPosition();
        }

        float barrelLength = physicalTowerP1.getBarrelLength();
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float shotX = jointPosition.x + sign * barrelLength * cos - SceneConstants.shotBasicWidth  / 2f * SceneConstants.SHOTS_SCALE;
        float shotY = jointPosition.y + sign * barrelLength * sin - SceneConstants.shotBasicHeight / 2f * SceneConstants.SHOTS_SCALE;
        Location location = new Location(shotX, shotY, 0, SceneConstants.SHOTS_SCALE);
        PhysicalShot physicalShot;

        // add new cases for new shots
        switch (shotType) {
            case BASIC:
                physicalShot = new PhysicalBasicShot(location, world);
                break;
            default:
                return;
        }

        physicalShot.playerType = playerTurn;
        physicalShot.setVelocity(new Vector2(sign * 25.f * cos, sign * 25.f * sin));

        synchronized (movables) {
            movables.add(physicalShot);
        }

        physicalShots.add(physicalShot);
//        if (graphicalScene != null)
//            graphicalScene.generateGraphicalShot(physicalShot);
    }

    public World getWorld() { return world; }

    public void stepWorld() {
        if (!stopWatch.isStarted()) {
            stopWatch.start();
            previousTime = (float) (stopWatch.getTime() * 1000);
        }
        stopWatch.split();
        float currentTime = stopWatch.getSplitTime() * 1000f;
        accumulator += Math.min(currentTime - previousTime, 0.25f);
        previousTime = currentTime;

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;

            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

            synchronized (movables) {
                for (Movable movable : movables)
                    movable.updateSprite();
            }

            // temp debug stuff (demonstrating tower cannon rotation)
            if ((physicalTowerP1.getJoint().getJointAngle() >= physicalTowerP1.getJoint().getUpperLimit() && physicalTowerP1.getJoint().getMotorSpeed() > 0)||
                    (physicalTowerP1.getJoint().getJointAngle() <= physicalTowerP1.getJoint().getLowerLimit() && physicalTowerP1.getJoint().getMotorSpeed() < 0))
                physicalTowerP1.getJoint().setMotorSpeed(-physicalTowerP1.getJoint().getMotorSpeed());

            if ((physicalTowerP2.getJoint().getJointAngle() >= physicalTowerP2.getJoint().getUpperLimit() && physicalTowerP2.getJoint().getMotorSpeed() > 0)||
                    (physicalTowerP2.getJoint().getJointAngle() <= physicalTowerP2.getJoint().getLowerLimit() && physicalTowerP2.getJoint().getMotorSpeed() < 0))
                physicalTowerP2.getJoint().setMotorSpeed(-physicalTowerP2.getJoint().getMotorSpeed());

            removeDeadBodies();
        }
    }

    private void removeDeadBodies() {
        ListIterator<PhysicalBlock> physicalBlocksP1Iterator = physicalBlocksP1.listIterator();
        while (physicalBlocksP1Iterator.hasNext()) {
            PhysicalBlock block = physicalBlocksP1Iterator.next();
            if (block.getHP() <= 0) {
                world.destroyBody(block.getBody());
                synchronized (movables) {
                    movables.remove(block);
                }
                if (graphicalScene != null)
                    graphicalScene.removeObject(block);
                physicalBlocksP1Iterator.remove();
            }
        }

        ListIterator<PhysicalBlock> physicalBlocksP2Iterator = physicalBlocksP2.listIterator();
        while (physicalBlocksP2Iterator.hasNext()) {
            PhysicalBlock block = physicalBlocksP2Iterator.next();
            if (block.getHP() <= 0) {
                world.destroyBody(block.getBody());
                synchronized (movables) {
                    movables.remove(block);
                }
                if (graphicalScene != null)
                    graphicalScene.removeObject(block);
                physicalBlocksP2Iterator.remove();
            }
        }

        ListIterator<PhysicalShot> physicalShotsIterator = physicalShots.listIterator();
        while (physicalShotsIterator.hasNext()) {
            PhysicalShot shot = physicalShotsIterator.next();
            if (shot.getVelocity().isZero(0.1f)) {
                world.destroyBody(shot.getBody());
                movables.remove(shot);
                if (graphicalScene != null)
                    graphicalScene.removeObject(shot);
                physicalShotsIterator.remove();
            }
        }
    }

    public void connectWithHPBar(PlayerType type, HPBar bar) {
        if (type == PlayerType.FIRST_PLAYER)
            EventSystem.get().connect(physicalCastleP1, "handleDamage", bar, "update");
        else
            EventSystem.get().connect(physicalCastleP2, "handleDamage", bar, "update");
    }

    public void connectWithSoundEffects(SoundEffects soundEffects) {

        for (PhysicalBlock block : physicalBlocksP1)
            EventSystem.get().connect(block, "handleDamage", soundEffects, "playWood");

        for (PhysicalBlock block : physicalBlocksP2)
            EventSystem.get().connect(block, "handleDamage", soundEffects, "playWood");

    }

    public void connectWithGameOver(PlayerType type, GameOver gameOver) {
        if (type == PlayerType.FIRST_PLAYER) {
            EventSystem.get().
                    connect(physicalCastleP2, "handleDamage", gameOver, "victoryCheck");
            EventSystem.get().
                    connect(physicalCastleP1, "handleDamage", gameOver, "defeatCheck");
        }
        else {
            EventSystem.get().
                    connect(physicalCastleP1, "handleDamage", gameOver, "victoryCheck");
            EventSystem.get().
                    connect(physicalCastleP2, "handleDamage", gameOver, "defeatCheck");
        }

    }

    public float getTowerAngle(PlayerType playerType) {
        if (playerType == PlayerType.FIRST_PLAYER) {
            return physicalTowerP1.getMovablePartAngle();
        }
        else {
            return physicalTowerP2.getMovablePartAngle();
        }
    }

    public PhysicalShot getLastShot() { return physicalShots.get(physicalShots.size() -1); }
}