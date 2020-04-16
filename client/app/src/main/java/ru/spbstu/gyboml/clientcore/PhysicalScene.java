package main.java.ru.spbstu.gyboml.clientcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;

import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.destructible.DestructionListener;
import ru.spbstu.gyboml.core.destructible.Material;
import ru.spbstu.gyboml.core.physical.CollisionHandler;
import ru.spbstu.gyboml.core.physical.Location;
import ru.spbstu.gyboml.core.physical.Movable;
import ru.spbstu.gyboml.core.physical.Physical;
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
class PhysicalScene {
    private final GraphicalScene graphicalScene;

    // Sound
    private final SoundEffects soundEffects;
    private DestructionListener blockSounds;

    private final World world;

    private List<Movable> movables;
    private PhysicalBackground physicalBackground;
    private PhysicalCastle physicalCastleP1;
    private PhysicalCastle physicalCastleP2;
    private PhysicalTower physicalTowerP1;
    private PhysicalTower physicalTowerP2;
    private ArrayList<PhysicalBlock> physicalBlocksP1;
    private ArrayList<PhysicalBlock> physicalBlocksP2;
    private ArrayList<PhysicalShot> physicalShots;
    private List<PhysicalBlock> blocksToRemove;
    private List<PhysicalShot> shotsToRemove;

    // physics
    private static final float gravityAccelerationX = 0f;
    private static final float gravityAccelerationY = -10f;
    private static final float STEP_TIME = 1f / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private float accumulator = 0;

    private float SCALE;
    private float BLOCKS_SCALE;
    private float SHOTS_SCALE;

    PhysicalScene(GraphicalScene graphicalScene, float backgroundX, float backgroundY) {
        this.graphicalScene = graphicalScene;
        SCALE               = graphicalScene.getScale();
        BLOCKS_SCALE        = graphicalScene.getBlockScale();
        SHOTS_SCALE         = graphicalScene.getShotsScale();

        soundEffects = SoundEffects.get();
        blockSounds = new DestructionListener() {
            @Override
            public void destructionOccured(float newHP) {
                soundEffects.wood.play(1.f);
            }
        };

        world = new World(new Vector2(gravityAccelerationX, gravityAccelerationY), true);
        world.setContactListener(new CollisionHandler());

        movables = new ArrayList<>();
        physicalBlocksP1 = new ArrayList<>();
        physicalBlocksP2 = new ArrayList<>();
        physicalShots = new ArrayList<>();
        blocksToRemove = new ArrayList<>();
        shotsToRemove = new ArrayList<>();

        final float castleIndentX   = 860;  // manually set value for castle placement on platform
        final float towerIndentX    = 450;  // manually set value for tower  placement on platform
        final float platformIndentY = 364;  // y position of platforms for objects from background.xml
        final float castleTextureWidth = graphicalScene.getCastleWidth();
        final float towerTextureWidth  = graphicalScene.getTowerWidth();

        float castleP1X = backgroundX + castleIndentX * SCALE;
        float castleP2X = backgroundX + (graphicalScene.getResolutionWidth() - castleIndentX - castleTextureWidth) * SCALE;
        float castleP1Y = backgroundY + platformIndentY * SCALE;
        float castleP2Y = backgroundY + platformIndentY * SCALE;
        float towerP1X = backgroundX + towerIndentX * SCALE;
        float towerP2X = backgroundX + (graphicalScene.getResolutionWidth() - towerIndentX - towerTextureWidth) * SCALE;
        float towerP1Y = backgroundY + platformIndentY * SCALE;
        float towerP2Y = backgroundY + platformIndentY * SCALE;

        physicalBackground = new PhysicalBackground(new Location(backgroundX, backgroundY, 0, SCALE), world);
        physicalCastleP1 = new PhysicalCastle(100, new Location(castleP1X, castleP1Y, 0, SCALE), PlayerType.FIRST_PLAYER, world);
        physicalCastleP2 = new PhysicalCastle(100, new Location(castleP2X, castleP2Y, 0, SCALE), PlayerType.SECOND_PLAYER, world);
        physicalTowerP1 = new PhysicalTower(new Location(towerP1X, towerP1Y, 0, SCALE), PlayerType.FIRST_PLAYER, world);
        physicalTowerP2 = new PhysicalTower(new Location(towerP2X, towerP2Y, 0, SCALE), PlayerType.SECOND_PLAYER, world);
        movables.add(physicalTowerP1);
        movables.add(physicalTowerP2);
        placeDefaultBlocks(castleP1X, castleP1Y, castleP2X, castleP2Y, Material.WOOD);
        movables.addAll(physicalBlocksP1);
        movables.addAll(physicalBlocksP2);

        graphicalScene.generateGraphicalBackground(physicalBackground);
        graphicalScene.generateGraphicalCastle(physicalCastleP1);
        graphicalScene.generateGraphicalCastle(physicalCastleP2);
        graphicalScene.generateGraphicalTower(physicalTowerP1);
        graphicalScene.generateGraphicalTower(physicalTowerP2);
        graphicalScene.bindBlocksGraphics(physicalBlocksP1, physicalBlocksP2);
        graphicalScene.generateGraphicalForeground(physicalBackground);
    }

    /**
     * This method is implemented for demo. Called within buildScene() method.
     * Fills physicalBlocksP1 and physicalBlocksP2 array lists with
     * several blocks placed by default same for both players.
     * Blocks placed manually in general.
     */
    private void placeDefaultBlocks(float castleP1X, float castleP1Y, float castleP2X, float castleP2Y, Material material) {
        final float blockWoodTextureWidth  = graphicalScene.getBlockWidth(material);
        final float blockWoodTextureHeight = graphicalScene.getBlockHeight(material);
        final float castleTextureWidth = graphicalScene.getCastleWidth();

        float blockP1X = castleP1X + (castleTextureWidth + 60) * SCALE;
        float blockP1Y = castleP1Y + 240 * SCALE;
        float blockP2X = castleP2X -  60 * SCALE - blockWoodTextureWidth * BLOCKS_SCALE;
        float blockP2Y = castleP2Y + 240 * SCALE;

        // 1st row
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(blockP1X, blockP1Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(blockP2X, blockP2Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(blockP1X, blockP1Y + 1.2f * blockWoodTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(blockP2X, blockP2Y + 1.2f * blockWoodTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(blockP1X, blockP1Y + 2 * 1.2f * blockWoodTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(blockP2X, blockP2Y + 2 * 1.2f * blockWoodTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));

        // 2nd row
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(blockP1X + 2 * blockWoodTextureWidth * BLOCKS_SCALE, blockP1Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(blockP2X - 2 * blockWoodTextureWidth * BLOCKS_SCALE, blockP2Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(blockP1X + 2 * blockWoodTextureWidth * BLOCKS_SCALE, blockP1Y + 1.2f * blockWoodTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(blockP2X - 2 * blockWoodTextureWidth * BLOCKS_SCALE, blockP2Y + 1.2f * blockWoodTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));

        // 3rd row
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(blockP1X + 4 * blockWoodTextureWidth * BLOCKS_SCALE, blockP1Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(blockP2X - 4 * blockWoodTextureWidth * BLOCKS_SCALE, blockP2Y, 0, BLOCKS_SCALE), world));

        // back row
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(castleP1X - 60 * SCALE - blockWoodTextureWidth * BLOCKS_SCALE, blockP1Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP1.add(new PhysicalBlock(material, new Location(castleP1X - 60 * SCALE - blockWoodTextureWidth * BLOCKS_SCALE, blockP1Y + 1.2f * blockWoodTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(castleP2X + (castleTextureWidth + 60) * SCALE, blockP2Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(material, new Location(castleP2X + (castleTextureWidth + 60) * SCALE, blockP2Y + 1.2f * blockWoodTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));

        for (PhysicalBlock block : physicalBlocksP1)
            block.getDestructionEmitter().addListener(blockSounds);

        for (PhysicalBlock block : physicalBlocksP2)
            block.getDestructionEmitter().addListener(blockSounds);
    }

    void generateShot(PlayerType playerTurn, ShotType shotType) {
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
        float shotX = jointPosition.x + sign * barrelLength * cos - graphicalScene.getShotWidth(shotType)  / 2f * SHOTS_SCALE;
        float shotY = jointPosition.y + sign * barrelLength * sin - graphicalScene.getShotHeight(shotType) / 2f * SHOTS_SCALE;
        Location location = new Location(shotX, shotY, 0, SHOTS_SCALE);
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
    }

    World getWorld() { return world; }

    void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();

        accumulator += Math.min(delta, 0.25f);

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

    private void removeDeadBodies() {
        blocksToRemove.clear();
        for (PhysicalBlock block : physicalBlocksP1) {
            if (block.getHP() <= 0) {
                blocksToRemove.add(block);
            }
        }
        for (PhysicalBlock block : physicalBlocksP2) {
            if (block.getHP() <= 0) {
                blocksToRemove.add(block);
            }
        }

        for (PhysicalBlock block : blocksToRemove) {
            world.destroyBody(block.getBody());
            movables.remove(block);
            graphicalScene.removeBlock(block);
            physicalBlocksP1.remove(block);
            physicalBlocksP2.remove(block);
        }

        shotsToRemove.clear();
        for (PhysicalShot shot : physicalShots) {
            if (shot.getVelocity().isZero(0.1f))
                shotsToRemove.add(shot);
        }

        for (PhysicalShot shot : shotsToRemove) {
            world.destroyBody(shot.getBody());
            movables.remove(shot);
            graphicalScene.removeShot(shot);
            physicalShots.remove(shot);
        }
    }

    public void connectWithHPBar(PlayerType type, HPBar bar) {
        if (type == PlayerType.FIRST_PLAYER)
            physicalCastleP1.getDestructionEmitter().addListener(bar);
        else
            physicalCastleP2.getDestructionEmitter().addListener(bar);
    }
}