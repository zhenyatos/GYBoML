package main.java.ru.spbstu.gyboml.clientcore;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.ru.spbstu.gyboml.graphics.Animated;
import main.java.ru.spbstu.gyboml.graphics.AnimatedExplosion;
import main.java.ru.spbstu.gyboml.graphics.Drawable;
import main.java.ru.spbstu.gyboml.graphics.GraphicalBackground;
import main.java.ru.spbstu.gyboml.graphics.GraphicalShot;
import main.java.ru.spbstu.gyboml.graphics.GraphicalBlock;
import main.java.ru.spbstu.gyboml.graphics.GraphicalCannon;
import main.java.ru.spbstu.gyboml.graphics.GraphicalCastle;
import main.java.ru.spbstu.gyboml.graphics.GraphicalForeground;
import main.java.ru.spbstu.gyboml.graphics.GraphicalTower;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.destructible.Material;
import ru.spbstu.gyboml.core.physical.PhysicalBackground;
import ru.spbstu.gyboml.core.physical.PhysicalBlock;
import ru.spbstu.gyboml.core.physical.PhysicalCastle;
import ru.spbstu.gyboml.core.physical.PhysicalShot;
import ru.spbstu.gyboml.core.physical.PhysicalTower;
import ru.spbstu.gyboml.core.shot.ShotType;

/**
 * Initialized during client creation.
 * Builds displayed scene for client application.
 */
class GraphicalScene {
    private final float canvasWidth;
    private final float canvasHeight;
    private final float resolutionWidth;
    private List<Drawable> drawables;
    private List<Animated> animations;
    private List<Animated> toRemoveFromAnimations;
    private List<GraphicalBlock> destroyed;
    private List<GraphicalBlock> toRemoveFromDestroyed;
    private List<GraphicalShot> stopped;
    private List<GraphicalShot> toRemoveFromStopped;
    private HashMap<PhysicalBlock, GraphicalBlock> blocks;
    private HashMap<PhysicalShot, GraphicalShot> shots;

    private float SCALE;
    private float BLOCKS_SCALE;
    private float SHOTS_SCALE;
    private float EXPLOSION_SCALE;

    // scene graphics
    private TextureAtlas backgroundBack;
    private TextureAtlas backgroundFront;
    private TextureAtlas objects;

    GraphicalScene(float canvasWidth, float canvasHeight) {
        this.canvasWidth  = canvasWidth;
        this.canvasHeight = canvasHeight;
        drawables = new ArrayList<>();
        animations = new ArrayList<>();
        destroyed = new ArrayList<>();
        stopped = new ArrayList<>();
        toRemoveFromDestroyed = new ArrayList<>();
        toRemoveFromStopped = new ArrayList<>();
        toRemoveFromAnimations = new ArrayList<>();
        blocks = new HashMap<>();
        shots = new HashMap<>();

        backgroundBack  = new TextureAtlas("sprites/background_1.txt");
        backgroundFront = new TextureAtlas("sprites/background_2.txt");
        objects         = new TextureAtlas("sprites/objects.txt");

        resolutionWidth = backgroundBack.findRegion("bg_sky").originalWidth;

        SCALE = canvasWidth / resolutionWidth;
        BLOCKS_SCALE = SCALE * 0.35f;
        SHOTS_SCALE  = SCALE * 0.22f;
        EXPLOSION_SCALE = SCALE * 0.85f;
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

    void generateGraphicalShot(PhysicalShot physicalShot) {
        String spriteName = "shot_" + physicalShot.shotType.getName();
        GraphicalShot graphicalShot = new GraphicalShot(objects.createSprite(spriteName), SHOTS_SCALE);
        graphicalShot.setOrigin(0, 0);
        graphicalShot.setPosition(physicalShot.getPosition().x, physicalShot.getPosition().y);
        drawables.add(graphicalShot);
        physicalShot.setUpdatableSprite(graphicalShot);
        shots.put(physicalShot, graphicalShot);

        animations.add(new AnimatedExplosion(
                physicalShot.getPosition().x - EXPLOSION_SCALE * Math.abs(AnimatedExplosion.FRAME_WIDTH  - objects.findRegion(spriteName).originalWidth)  / 2f,
                physicalShot.getPosition().y - EXPLOSION_SCALE * Math.abs(AnimatedExplosion.FRAME_HEIGHT - objects.findRegion(spriteName).originalHeight) / 2f,
                EXPLOSION_SCALE));
    }

    void generateGraphicalBlock(PhysicalBlock physicalBlock) {
        GraphicalBlock graphicalBlock = new GraphicalBlock(
                objects.createSprite("block_" + physicalBlock.material.getName()),
                objects.createSprite("block_" + physicalBlock.material.getName() + "_damaged"),
                BLOCKS_SCALE);
        graphicalBlock.setOrigin(0,0);
        graphicalBlock.setPosition(physicalBlock.getPosition().x, physicalBlock.getPosition().y);
        drawables.add(graphicalBlock);
        physicalBlock.setUpdatableSprite(graphicalBlock);
        blocks.put(physicalBlock, graphicalBlock);
    }

    /**
     * This method is implemented for demo. Called within buildScene() method.
     * Binds graphical blocks objects to its physical versions.
     */
    void bindBlocksGraphics(ArrayList<PhysicalBlock> physicalBlocksP1, ArrayList<PhysicalBlock> physicalBlocksP2) {
        for (PhysicalBlock block : physicalBlocksP1) {
            generateGraphicalBlock(block);
        }
        for (PhysicalBlock block : physicalBlocksP2) {
            generateGraphicalBlock(block);
        }
    }

    void removeBlock(PhysicalBlock block) {
        destroyed.add(blocks.get(block));
        drawables.remove(blocks.get(block));
        blocks.remove(block);
    }

    void removeShot(PhysicalShot shot) {
        stopped.add(shots.get(shot));
        drawables.remove(shots.get(shot));
        shots.remove(shot);
    }

    float getScale() { return SCALE; }

    float getBlockScale() { return BLOCKS_SCALE; }

    float getShotsScale() { return SHOTS_SCALE; }

    float getResolutionWidth() { return resolutionWidth; }

    float getCastleWidth() { return objects.findRegion("castle_p1_front").originalWidth; }

    float getTowerWidth() { return objects.findRegion("tower_p1").originalWidth; }

    float getShotWidth(ShotType shotType) { return objects.findRegion("shot_" + shotType.getName()).originalWidth; }

    float getShotHeight(ShotType shotType) { return objects.findRegion("shot_" + shotType.getName()).originalHeight; }

    float getBlockWidth(Material material) { return objects.findRegion("block_" + material.getName()).originalWidth; }

    float getBlockHeight(Material material) { return objects.findRegion("block_" + material.getName()).originalHeight; }

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
        for (GraphicalShot shot : stopped) {
            shot.draw(batch);
            shot.getSprite().setAlpha(shot.getSprite().getColor().a - 0.03f);
            if (shot.getSprite().getColor().a <= 0.03f)
                toRemoveFromStopped.add(shot);
        }

        for (GraphicalShot shot : toRemoveFromStopped) {
            stopped.remove(shot);
        }

        for (Animated animated : animations) {
            animated.draw(batch);
            if (animated.isFinished())
                toRemoveFromAnimations.add(animated);
        }

        for (Animated animated : toRemoveFromAnimations) {
            animations.remove(animated);
        }
    }

    void dispose() {
        backgroundBack.dispose();
        backgroundFront.dispose();
        objects.dispose();
    }
}
