package ru.spbstu.gyboml.core.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import ru.spbstu.gyboml.core.graphics.Animated;
import ru.spbstu.gyboml.core.graphics.AnimatedExplosion;
import ru.spbstu.gyboml.core.graphics.Drawable;
import ru.spbstu.gyboml.core.graphics.GraphicalBackground;
import ru.spbstu.gyboml.core.graphics.GraphicalShot;
import ru.spbstu.gyboml.core.graphics.GraphicalBlock;
import ru.spbstu.gyboml.core.graphics.GraphicalCannon;
import ru.spbstu.gyboml.core.graphics.GraphicalCastle;
import ru.spbstu.gyboml.core.graphics.GraphicalForeground;
import ru.spbstu.gyboml.core.graphics.GraphicalTower;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.physical.Physical;
import ru.spbstu.gyboml.core.physical.PhysicalBackground;
import ru.spbstu.gyboml.core.physical.PhysicalBlock;
import ru.spbstu.gyboml.core.physical.PhysicalCastle;
import ru.spbstu.gyboml.core.physical.PhysicalShot;
import ru.spbstu.gyboml.core.physical.PhysicalTower;

/**
 * Initialized during client creation.
 * Builds displayed scene for client application.
 */
public class GraphicalScene {
    private List<Drawable> drawables;
    private List<Drawable> destroyed;
    private List<Animated> animations;
    private Map<Physical, Drawable> objectsMap;

    // scene graphics
    private TextureAtlas backgroundBack;
    private TextureAtlas backgroundFront;
    private TextureAtlas objects;

    public GraphicalScene() {
        drawables = new ArrayList<>();
        animations = new ArrayList<>();
        destroyed = new ArrayList<>();
        objectsMap = new HashMap<>();

        backgroundBack  = new TextureAtlas("sprites/background_1.txt");
        backgroundFront = new TextureAtlas("sprites/background_2.txt");
        objects         = new TextureAtlas("sprites/objects.txt");
    }

    void generateGraphicalBackground(PhysicalBackground physicalBackground) {
        GraphicalBackground graphicalBackground = new GraphicalBackground(
                backgroundBack.createSprite("bg_sky"),
                backgroundBack.createSprite("bg_desert"),
                backgroundFront.createSprite("bg_land"),
                SceneConstants.SCALE);
        graphicalBackground.setSize(SceneConstants.canvasWidth, SceneConstants.canvasHeight);
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
                SceneConstants.SCALE, 100);
        graphicalCastle.setOrigin(0, 0);
        graphicalCastle.setPosition(physicalCastle.getPosition().x, physicalCastle.getPosition().y);
        drawables.add(graphicalCastle);
    }

    void generateGraphicalTower(PhysicalTower physicalTower) {
        String playerName = (physicalTower.getPlayerType() == PlayerType.FIRST_PLAYER) ? "_p1" : "_p2";

        GraphicalCannon graphicalCannon = new GraphicalCannon(objects.createSprite("cannon" + playerName),SceneConstants.SCALE);
        graphicalCannon.setOrigin(0, 0);
        graphicalCannon.setPosition(physicalTower.getMovablePartPosition().x, physicalTower.getMovablePartPosition().y);
        graphicalCannon.setRotation(physicalTower.getMovablePartAngle());
        drawables.add(graphicalCannon);
        physicalTower.setUpdatableSprite(graphicalCannon);

        GraphicalTower graphicalTower = new GraphicalTower(objects.createSprite("tower" + playerName), SceneConstants.SCALE);
        graphicalTower.setOrigin(0, 0);
        graphicalTower.setPosition(physicalTower.getPosition().x, physicalTower.getPosition().y);
        drawables.add(graphicalTower);
    }

    void generateGraphicalForeground(PhysicalBackground physicalBackground) {
        GraphicalForeground graphicalForeground = new GraphicalForeground(backgroundFront.createSprite("bg_front"), SceneConstants.SCALE);
        graphicalForeground.setSize(SceneConstants.canvasWidth, SceneConstants.canvasHeight);
        graphicalForeground.setOrigin(0, 0);
        graphicalForeground.setPosition(physicalBackground.getPosition().x, physicalBackground.getPosition().y);
        drawables.add(graphicalForeground);
    }

    void generateGraphicalShot(PhysicalShot physicalShot) {
        String spriteName = "shot_" + physicalShot.shotType.getName();
        GraphicalShot graphicalShot = new GraphicalShot(objects.createSprite(spriteName), SceneConstants.SHOTS_SCALE);
        graphicalShot.setOrigin(0, 0);
        graphicalShot.setPosition(physicalShot.getPosition().x, physicalShot.getPosition().y);
        drawables.add(graphicalShot);
        physicalShot.setUpdatableSprite(graphicalShot);
        objectsMap.put(physicalShot, graphicalShot);

        float explosionX = (physicalShot.playerType == PlayerType.FIRST_PLAYER) ?
                physicalShot.getPosition().x -  SceneConstants.SHOTS_SCALE * objects.findRegion(spriteName).originalWidth / 2f :
                physicalShot.getPosition().x + (SceneConstants.SHOTS_SCALE * objects.findRegion(spriteName).originalWidth - SceneConstants.EXPLOSION_SCALE * AnimatedExplosion.FRAME_WIDTH) / 2f;
        float explosionY = physicalShot.getPosition().y - Math.abs(SceneConstants.EXPLOSION_SCALE * AnimatedExplosion.FRAME_HEIGHT - SceneConstants.SHOTS_SCALE * objects.findRegion(spriteName).originalHeight) / 2f;

        animations.add(new AnimatedExplosion(explosionX, explosionY, SceneConstants.EXPLOSION_SCALE));
    }

    void generateGraphicalBlock(PhysicalBlock physicalBlock) {
        GraphicalBlock graphicalBlock = new GraphicalBlock(
                objects.createSprite("block_" + physicalBlock.material.getName()),
                objects.createSprite("block_" + physicalBlock.material.getName() + "_damaged"),
                SceneConstants.BLOCKS_SCALE);
        graphicalBlock.setOrigin(0,0);
        graphicalBlock.setPosition(physicalBlock.getPosition().x, physicalBlock.getPosition().y);
        drawables.add(graphicalBlock);
        physicalBlock.setUpdatableSprite(graphicalBlock);
        objectsMap.put(physicalBlock, graphicalBlock);
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

    void removeObject(Physical object) {
        destroyed.add(objectsMap.get(object));
        drawables.remove(objectsMap.get(object));
        objectsMap.remove(object);
    }

    public void draw(Batch batch) {
        for (Drawable object : drawables) {
            object.draw(batch);
        }

        ListIterator<Drawable> destroyedIterator = destroyed.listIterator();
        while (destroyedIterator.hasNext()) {
            Drawable drawable = destroyedIterator.next();
            drawable.draw(batch);
            drawable.getSprite().setAlpha(drawable.getSprite().getColor().a - 0.03f);
            if (drawable.getSprite().getColor().a <= 0.03f) {
                destroyedIterator.remove();
            }
        }

        ListIterator<Animated> animationsIterator = animations.listIterator();
        while (animationsIterator.hasNext()) {
            Animated animated = animationsIterator.next();
            animated.draw(batch);
            if (animated.isFinished())
                animationsIterator.remove();
        }
    }

    public void dispose() {
        backgroundBack.dispose();
        backgroundFront.dispose();
        objects.dispose();
    }
}