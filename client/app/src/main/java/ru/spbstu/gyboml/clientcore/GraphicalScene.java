package main.java.ru.spbstu.gyboml.clientcore;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.ru.spbstu.gyboml.graphics.Drawable;
import main.java.ru.spbstu.gyboml.graphics.GraphicalBackground;
import main.java.ru.spbstu.gyboml.graphics.GraphicalBasicShot;
import main.java.ru.spbstu.gyboml.graphics.GraphicalBlock;
import main.java.ru.spbstu.gyboml.graphics.GraphicalCannon;
import main.java.ru.spbstu.gyboml.graphics.GraphicalCastle;
import main.java.ru.spbstu.gyboml.graphics.GraphicalForeground;
import main.java.ru.spbstu.gyboml.graphics.GraphicalTower;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.physical.PhysicalBackground;
import ru.spbstu.gyboml.core.physical.PhysicalBasicShot;
import ru.spbstu.gyboml.core.physical.PhysicalBlock;
import ru.spbstu.gyboml.core.physical.PhysicalCastle;
import ru.spbstu.gyboml.core.physical.PhysicalTower;

/**
 * Initialized during client creation.
 * Builds displayed scene for client application.
 */
class GraphicalScene {
    private final float canvasWidth;
    private final float canvasHeight;
    private final float resolutionWidth;
    private List<Drawable> drawables;
    private List<GraphicalBlock> destroyed;
    private List<GraphicalBlock> toRemoveFromDestroyed;
    private List<GraphicalBasicShot> stopped;
    private List<GraphicalBasicShot> toRemoveFromStopped;
    private HashMap<PhysicalBlock, GraphicalBlock> blocks;
    private HashMap<PhysicalBasicShot, GraphicalBasicShot> shots;

    private float SCALE;
    private float BLOCKS_SCALE;
    private float SHOTS_SCALE;

    // scene graphics
    private TextureAtlas backgroundBack;
    private TextureAtlas backgroundFront;
    private TextureAtlas objects;

    GraphicalScene(float canvasWidth, float canvasHeight) {
        this.canvasWidth  = canvasWidth;
        this.canvasHeight = canvasHeight;
        drawables = new ArrayList<>();
        destroyed = new ArrayList<>();
        stopped = new ArrayList<>();
        toRemoveFromDestroyed = new ArrayList<>();
        toRemoveFromStopped = new ArrayList<>();
        blocks = new HashMap<>();
        shots = new HashMap<>();

        backgroundBack  = new TextureAtlas("sprites/background_1.txt");
        backgroundFront = new TextureAtlas("sprites/background_2.txt");
        objects         = new TextureAtlas("sprites/objects.txt");

        resolutionWidth = backgroundBack.findRegion("bg_sky").originalWidth;

        SCALE = canvasWidth / resolutionWidth;
        BLOCKS_SCALE = SCALE * 0.35f;
        SHOTS_SCALE  = SCALE * 0.22f;
    }

    void generateGraphicalShot(PhysicalBasicShot physicalShot) {
        GraphicalBasicShot graphicalShot = new GraphicalBasicShot(objects.createSprite("shot_basic"), SHOTS_SCALE);
        graphicalShot.setOrigin(0, 0);
        graphicalShot.setPosition(physicalShot.getPosition().x, physicalShot.getPosition().y);
        drawables.add(graphicalShot);
        physicalShot.setUpdatableSprite(graphicalShot);
        shots.put(physicalShot, graphicalShot);
    }

    void generateGraphicalBackground(PhysicalBackground physicalBackground) {
        GraphicalBackground graphicalBackground = new GraphicalBackground(
                backgroundBack.createSprite("bg_sky"),
                backgroundBack.createSprite("bg_desert"),
                backgroundFront.createSprite("bg_land"),
                SCALE);
        graphicalBackground.setSize(canvasWidth, canvasHeight);
        graphicalBackground.setOrigin(0, 0);
        graphicalBackground.setPosition(physicalBackground.getPosition().x, physicalBackground.getPosition().y);
        drawables.add(graphicalBackground);
    }

    void generateGraphicalCastle(PhysicalCastle physicalCastle) {
        String playerName = (physicalCastle.getPlayerType() == PlayerType.FIRST_PLAYER) ? "_p1_" : "_p2_";
        GraphicalCastle graphicalCastle = new GraphicalCastle(
                objects.createSprite("castle" + playerName + "back"),
                objects.createSprite("castle" + playerName + "front"),
                objects.createSprite("castle" + playerName + "tower"),
                SCALE, 100);
        graphicalCastle.setOrigin(0, 0);
        graphicalCastle.setPosition(physicalCastle.getPosition().x, physicalCastle.getPosition().y);
        drawables.add(graphicalCastle);
    }

    void generateGraphicalTower(PhysicalTower physicalTower) {
        String playerName = (physicalTower.getPlayerType() == PlayerType.FIRST_PLAYER) ? "_p1" : "_p2";

        GraphicalCannon graphicalCannon = new GraphicalCannon(objects.createSprite("cannon" + playerName), SCALE);
        graphicalCannon.setOrigin(0, 0);
        graphicalCannon.setPosition(physicalTower.getMovablePartPosition().x, physicalTower.getMovablePartPosition().y);
        graphicalCannon.setRotation(physicalTower.getMovablePartAngle());
        drawables.add(graphicalCannon);
        physicalTower.setUpdatableSprite(graphicalCannon);

        GraphicalTower graphicalTower = new GraphicalTower(objects.createSprite("tower" + playerName), SCALE);
        graphicalTower.setOrigin(0, 0);
        graphicalTower.setPosition(physicalTower.getPosition().x, physicalTower.getPosition().y);
        drawables.add(graphicalTower);
    }

    void generateGraphicalForeground(PhysicalBackground physicalBackground) {
        GraphicalForeground graphicalForeground = new GraphicalForeground(backgroundFront.createSprite("bg_front"), SCALE);
        graphicalForeground.setSize(canvasWidth, canvasHeight);
        graphicalForeground.setOrigin(0, 0);
        graphicalForeground.setPosition(physicalBackground.getPosition().x, physicalBackground.getPosition().y);
        drawables.add(graphicalForeground);
    }

    /**
     * This method is implemented for demo. Called within buildScene() method.
     * Binds graphical blocks objects to its physical versions.
     */
    void bindBlocksGraphics(ArrayList<PhysicalBlock> physicalBlocksP1, ArrayList<PhysicalBlock> physicalBlocksP2) {
        for (PhysicalBlock block : physicalBlocksP1) {
            GraphicalBlock graphicalBlockP1 = new GraphicalBlock(objects.createSprite("block_wood"), objects.createSprite("block_wood_damaged"), BLOCKS_SCALE);
            graphicalBlockP1.setOrigin(0,0);
            graphicalBlockP1.setPosition(block.getPosition().x, block.getPosition().y);
            drawables.add(graphicalBlockP1);
            block.setUpdatableSprite(graphicalBlockP1);
            blocks.put(block, graphicalBlockP1);
        }

        for (PhysicalBlock block : physicalBlocksP2) {
            GraphicalBlock graphicalBlockP2 = new GraphicalBlock(objects.createSprite("block_wood"), objects.createSprite("block_wood_damaged"), BLOCKS_SCALE);
            graphicalBlockP2.setOrigin(0,0);
            graphicalBlockP2.setPosition(block.getPosition().x, block.getPosition().y);
            drawables.add(graphicalBlockP2);
            block.setUpdatableSprite(graphicalBlockP2);
            blocks.put(block, graphicalBlockP2);
        }
    }

    void removeBlock(PhysicalBlock block) {
        drawables.remove(blocks.get(block));
        blocks.remove(block);

        GraphicalBlock destroyedBlock = new GraphicalBlock(objects.createSprite("block_wood_damaged"), objects.createSprite("block_wood_damaged"), BLOCKS_SCALE);
        destroyedBlock.setOrigin(0,0);
        destroyedBlock.setPosition(block.getPosition().x, block.getPosition().y);
        destroyedBlock.setRotation((float)Math.toDegrees(block.getBody().getAngle()));
        destroyed.add(destroyedBlock);
    }

    void removeShot(PhysicalBasicShot shot) {
        drawables.remove(shots.get(shot));
        shots.remove(shot);

        GraphicalBasicShot stoppedShot = new GraphicalBasicShot(objects.createSprite("shot_basic"), SHOTS_SCALE);
        stoppedShot.setOrigin(0,0);
        stoppedShot.setPosition(shot.getPosition().x, shot.getPosition().y);
        stoppedShot.setRotation((float)Math.toDegrees(shot.getBody().getAngle()));
        stopped.add(stoppedShot);
    }

    float getScale() { return SCALE; }

    float getBlockScale() { return BLOCKS_SCALE; }

    float getShotsScale() { return SHOTS_SCALE; }

    float getResolutionWidth() { return resolutionWidth; }

    float getCastleWidth() { return objects.findRegion("castle_p1_front").originalWidth; }

    float getTowerWidth() { return objects.findRegion("tower_p1").originalWidth; }

    float getShotBasicWidth() { return objects.findRegion("shot_basic").originalWidth; }

    float getShotBasicHeight() { return getShotBasicWidth(); }

    float getBlockWidth() { return objects.findRegion("block_wood").originalWidth; }

    float getBlockHeight() { return objects.findRegion("block_wood").originalHeight; }

    void draw(Batch batch) {
        for (Drawable object : drawables) {
            object.draw(batch);
        }

        toRemoveFromDestroyed.clear();
        for (GraphicalBlock block : destroyed) {
            block.draw(batch);
            block.getCurrentSprite().setAlpha(block.getCurrentSprite().getColor().a - 0.03f);
            if (block.getCurrentSprite().getColor().a <= 0.03f)
                toRemoveFromDestroyed.add(block);
        }

        for (GraphicalBlock block : toRemoveFromDestroyed) {
            destroyed.remove(block);
        }

        toRemoveFromStopped.clear();
        for (GraphicalBasicShot shot : stopped) {
            shot.draw(batch);
            shot.getSprite().setAlpha(shot.getSprite().getColor().a - 0.03f);
            if (shot.getSprite().getColor().a <= 0.03f)
                toRemoveFromStopped.add(shot);
        }

        for (GraphicalBasicShot shot : toRemoveFromStopped) {
            stopped.remove(shot);
        }
    }

    void dispose() {
        backgroundBack.dispose();
        backgroundFront.dispose();
        objects.dispose();
    }
}
