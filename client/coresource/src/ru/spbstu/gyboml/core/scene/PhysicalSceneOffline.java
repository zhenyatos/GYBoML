package ru.spbstu.gyboml.core.scene;

import androidx.annotation.NonNull;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import org.apache.commons.lang3.time.StopWatch;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.damage.Damage;
import ru.spbstu.gyboml.core.destructible.Material;
import ru.spbstu.gyboml.core.event.Events;
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
public class PhysicalSceneOffline {
    private final GraphicalScene graphicalScene;
    private final SoundEffects soundEffects;

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

    public PhysicalSceneOffline(@NonNull GraphicalScene graphicalScene, @NonNull SoundEffects soundEffects) {
        this.graphicalScene = graphicalScene;
        this.soundEffects = soundEffects;

        world = new World(new Vector2(gravityAccelerationX, gravityAccelerationY), true);
        world.setContactListener(new CollisionHandler(graphicalScene, soundEffects));

        movables = new ArrayList<>();
        physicalBlocksP1 = new ArrayList<>();
        physicalBlocksP2 = new ArrayList<>();
        physicalShots = new ArrayList<>();

        final float castleIndentX   = 860;  // manually set value for castle placement on platform
        final float towerIndentX    = 450;  // manually set value for tower  placement on platform
        final float platformIndentY = 364;  // y position of platforms for objects from background.xml
        final float castleTextureWidth = SceneConstants.castleWidth * SceneConstants.CASTLES_SCALE;
        final float towerTextureWidth  = SceneConstants.towerWidth * SceneConstants.TOWERS_SCALE;

        float castleP1X = SceneConstants.backgroundX + castleIndentX * SceneConstants.SCALE;
        float castleP2X = SceneConstants.backgroundX + (SceneConstants.resolutionWidth - castleIndentX) * SceneConstants.SCALE - castleTextureWidth;
        float castleP1Y = SceneConstants.backgroundY + platformIndentY * SceneConstants.SCALE;
        float castleP2Y = SceneConstants.backgroundY + platformIndentY * SceneConstants.SCALE;
        float towerP1X = SceneConstants.backgroundX + towerIndentX * SceneConstants.SCALE;
        float towerP2X = SceneConstants.backgroundX + (SceneConstants.resolutionWidth - towerIndentX) * SceneConstants.SCALE - towerTextureWidth;
        float towerP1Y = SceneConstants.backgroundY + platformIndentY * SceneConstants.SCALE;
        float towerP2Y = SceneConstants.backgroundY + platformIndentY * SceneConstants.SCALE;

        generatePhysicalBackground(new Location(SceneConstants.backgroundX, SceneConstants.backgroundY, 0, SceneConstants.SCALE));
        generatePhysicalCastle(new Location(castleP1X, castleP1Y, 0, SceneConstants.CASTLES_SCALE), PlayerType.FIRST_PLAYER);
        generatePhysicalCastle(new Location(castleP2X, castleP2Y, 0, SceneConstants.CASTLES_SCALE), PlayerType.SECOND_PLAYER);
        generatePhysicalTower(new Location(towerP1X, towerP1Y, 0, SceneConstants.TOWERS_SCALE), PlayerType.FIRST_PLAYER);
        generatePhysicalTower(new Location(towerP2X, towerP2Y, 0, SceneConstants.TOWERS_SCALE), PlayerType.SECOND_PLAYER);
        generateDefaultBlocks(castleP1X, castleP1Y, castleP2X, castleP2Y, Material.WOOD);
    }

    private void generatePhysicalBackground(Location location) {
        if (physicalBackground == null)
            physicalBackground = new PhysicalBackground(location, world);
        graphicalScene.generateGraphicalBackground(physicalBackground);
        graphicalScene.generateGraphicalForeground(physicalBackground);
    }

    private void generatePhysicalCastle(Location location, PlayerType playerType) {
        if (playerType == PlayerType.FIRST_PLAYER && physicalCastleP1 == null) {
            physicalCastleP1 = new PhysicalCastle(100, location, playerType, world);
            graphicalScene.generateGraphicalCastle(physicalCastleP1);
        }
        else if (playerType == PlayerType.SECOND_PLAYER && physicalCastleP2 == null) {
            physicalCastleP2 = new PhysicalCastle(100, location, playerType, world);
            graphicalScene.generateGraphicalCastle(physicalCastleP2);
        }
    }

    private void generatePhysicalTower(Location location, PlayerType playerType) {
        if (playerType == PlayerType.FIRST_PLAYER && physicalTowerP1 == null) {
            physicalTowerP1 = new PhysicalTower(location, playerType, world);
            movables.add(physicalTowerP1);
            graphicalScene.generateGraphicalTower(physicalTowerP1);
        }
        else if (playerType == PlayerType.SECOND_PLAYER && physicalTowerP2 == null) {
            physicalTowerP2 = new PhysicalTower(location, playerType, world);
            movables.add(physicalTowerP2);
            graphicalScene.generateGraphicalTower(physicalTowerP2);
        }
    }

    /**
     * This method is implemented for demo. Called within buildScene() method.
     * Fills physicalBlocksP1 and physicalBlocksP2 array lists with
     * several blocks placed by default same for both players.
     * Blocks placed manually in general.
     */
    private void generateDefaultBlocks(float castleP1X, float castleP1Y, float castleP2X, float castleP2Y, Material material) {
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

        movables.addAll(physicalBlocksP1);
        movables.addAll(physicalBlocksP2);

        graphicalScene.bindBlocksGraphics(physicalBlocksP1, physicalBlocksP2);
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

        movables.add(physicalShot);

        physicalShots.add(physicalShot);
        graphicalScene.generateGraphicalShot(physicalShot);
        soundEffects.playShot();
        //EventSystem.get().emit(this, "generateShot", physicalShot);
        //EventSystem.get().emit(this, "generateShot");
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

            for (Movable movable : movables)
                movable.updateSprite();

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

    // TODO: replace iterations with event system calls
    private void removeDeadBodies() {
        ListIterator<PhysicalBlock> physicalBlocksP1Iterator = physicalBlocksP1.listIterator();
        while (physicalBlocksP1Iterator.hasNext()) {
            PhysicalBlock block = physicalBlocksP1Iterator.next();
            if (block.getHP() <= 0) {
                world.destroyBody(block.getBody());
                movables.remove(block);
                graphicalScene.removeObject(block);
                //soundEffects.playBroken(block.material);
                physicalBlocksP1Iterator.remove();
            }
        }

        ListIterator<PhysicalBlock> physicalBlocksP2Iterator = physicalBlocksP2.listIterator();
        while (physicalBlocksP2Iterator.hasNext()) {
            PhysicalBlock block = physicalBlocksP2Iterator.next();
            if (block.getHP() <= 0) {
                world.destroyBody(block.getBody());
                movables.remove(block);
                graphicalScene.removeObject(block);
                //soundEffects.playBroken(block.material);
                physicalBlocksP2Iterator.remove();
            }
        }

        ListIterator<PhysicalShot> physicalShotsIterator = physicalShots.listIterator();
        while (physicalShotsIterator.hasNext()) {
            PhysicalShot shot = physicalShotsIterator.next();
            if (shot.getVelocity().isZero(0.5f) ||
                    shot.getPosition().x < 0.0f ||
                    shot.getPosition().x > SceneConstants.worldWidth ||
                    shot.getPosition().y < 0.0f) {
                world.destroyBody(shot.getBody());
                movables.remove(shot);
                graphicalScene.removeObject(shot);
                physicalShotsIterator.remove();
            }
        }
    }

    public boolean isStopped() {
        return physicalShots.size() == 0;
    }

    public void setTurn(PlayerType playerType) {
        if (playerType == PlayerType.FIRST_PLAYER) {
            physicalTowerP1.setCannonAwake(true);
            physicalTowerP2.setCannonAwake(false);
        }
        else {
            physicalTowerP1.setCannonAwake(false);
            physicalTowerP2.setCannonAwake(true);
        }
    }

    public void connectWithHPBar(PlayerType type, HPBar bar) {
            Method handleDamage = Events.get().find(PhysicalCastle.class, "handleDamage", Damage.class);
            Method update = Events.get().find(HPBar.class, "update", float.class);
            if (type == PlayerType.FIRST_PLAYER)
                Events.get().connect(physicalCastleP1, handleDamage, bar, update);
            else
                Events.get().connect(physicalCastleP2, handleDamage, bar, update);
    }

    public void connectWithGameOver(GameOver gameOver) {
        Method handleDamage = Events.get().find(PhysicalCastle.class, "handleDamage", Damage.class);
        Method victory1st = Events.get().find(GameOver.class, "victory1st", float.class);
        Method victory2nd = Events.get().find(GameOver.class, "victory2nd", float.class);
        Events.get().
                connect(physicalCastleP2, handleDamage, gameOver, victory1st);
        Events.get().
                connect(physicalCastleP1, handleDamage, gameOver, victory2nd);
        }
    }