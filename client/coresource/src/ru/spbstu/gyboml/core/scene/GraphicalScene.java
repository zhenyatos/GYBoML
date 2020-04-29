package ru.spbstu.gyboml.core.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import ru.spbstu.gyboml.core.graphics.Animated;
import ru.spbstu.gyboml.core.graphics.AnimatedFading;
import ru.spbstu.gyboml.core.graphics.AnimatedInstance;
import ru.spbstu.gyboml.core.graphics.Drawable;
import ru.spbstu.gyboml.core.graphics.GraphicalBackground;
import ru.spbstu.gyboml.core.graphics.GraphicalShot;
import ru.spbstu.gyboml.core.graphics.GraphicalBlock;
import ru.spbstu.gyboml.core.graphics.GraphicalCannon;
import ru.spbstu.gyboml.core.graphics.GraphicalCastle;
import ru.spbstu.gyboml.core.graphics.GraphicalForeground;
import ru.spbstu.gyboml.core.graphics.GraphicalTower;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.physical.Location;
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
    private List<Animated> animations;
    private Map<Physical, Drawable> objectsMap;

    // scene graphics
    private TextureAtlas backgroundBack;
    private TextureAtlas backgroundFront;
    private TextureAtlas objects;
    private TextureAtlas texts;
    private Animation<TextureRegion> explosionAnimation;
    private Animation<TextureRegion> coinP1TurnAnimation;
    private Animation<TextureRegion> coinP2TurnAnimation;
    private Animation<TextureRegion> impactAnimation;

    public GraphicalScene() {
        drawables = new ArrayList<>();
        animations = new ArrayList<>();
        objectsMap = new HashMap<>();

        backgroundBack  = new TextureAtlas("sprites/background_1.txt");
        backgroundFront = new TextureAtlas("sprites/background_2.txt");
        objects         = new TextureAtlas("sprites/objects.txt");
        texts           = new TextureAtlas("sprites/texts.txt");

        explosionAnimation  = initAnimation("animations/explosion.png", 8, 8, 0.02f);
        coinP1TurnAnimation = initAnimation("animations/coin_p1_turn.png", 10, 10, 0.02f);
        coinP2TurnAnimation = initAnimation("animations/coin_p2_turn.png", 10, 10, 0.02f);
        impactAnimation     = initAnimation("animations/impact.png", 6, 3, 0.02f);
    }

    private Animation<TextureRegion> initAnimation(String path, int cols, int rows, float frameDuration) {
        Texture sheet = new Texture(Gdx.files.internal(path));
        TextureRegion[][] tempTextureRegions = TextureRegion.split(
                sheet,sheet.getWidth() / cols,sheet.getHeight() / rows);
        TextureRegion[] frames = new TextureRegion[cols * rows];
        int ind = 0;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j)
                frames[ind++] = tempTextureRegions[i][j];
        }
        return new Animation<>(frameDuration, frames);
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

    void generateGraphicalForeground(PhysicalBackground physicalBackground) {
        GraphicalForeground graphicalForeground = new GraphicalForeground(backgroundFront.createSprite("bg_front"), SceneConstants.SCALE);
        graphicalForeground.setSize(SceneConstants.canvasWidth, SceneConstants.canvasHeight);
        graphicalForeground.setOrigin(0, 0);
        graphicalForeground.setPosition(physicalBackground.getPosition().x, physicalBackground.getPosition().y);
        drawables.add(graphicalForeground);
    }

    void generateGraphicalCastle(PhysicalCastle physicalCastle) {
        String playerName = (physicalCastle.getPlayerType() == PlayerType.FIRST_PLAYER) ? "_p1_" : "_p2_";
        GraphicalCastle graphicalCastle = new GraphicalCastle(
                objects.createSprite("castle" + playerName + "back"),
                objects.createSprite("castle" + playerName + "front"),
                objects.createSprite("castle" + playerName + "tower"),
                SceneConstants.CASTLES_SCALE, 100);
        graphicalCastle.setOrigin(0, 0);
        graphicalCastle.setPosition(physicalCastle.getPosition().x, physicalCastle.getPosition().y);
        drawables.add(graphicalCastle);
    }

    void generateGraphicalTower(PhysicalTower physicalTower) {
        String playerName = (physicalTower.getPlayerType() == PlayerType.FIRST_PLAYER) ? "_p1" : "_p2";

        GraphicalCannon graphicalCannon = new GraphicalCannon(objects.createSprite("cannon" + playerName), SceneConstants.TOWERS_SCALE);
        graphicalCannon.setOrigin(0, 0);
        graphicalCannon.setPosition(physicalTower.getMovablePartPosition().x, physicalTower.getMovablePartPosition().y);
        graphicalCannon.setRotation(physicalTower.getMovablePartAngle());
        drawables.add(graphicalCannon);
        physicalTower.setUpdatableSprite(graphicalCannon);

        GraphicalTower graphicalTower = new GraphicalTower(objects.createSprite("tower" + playerName), SceneConstants.TOWERS_SCALE);
        graphicalTower.setOrigin(0, 0);
        graphicalTower.setPosition(physicalTower.getPosition().x, physicalTower.getPosition().y);
        drawables.add(graphicalTower);
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

    public void generateGraphicalShot(PhysicalShot physicalShot) {
        GraphicalShot graphicalShot = new GraphicalShot(objects.createSprite("shot_" + physicalShot.shotType.getName()), SceneConstants.SHOTS_SCALE);
        graphicalShot.setOrigin(0, 0);
        graphicalShot.setPosition(physicalShot.getPosition().x, physicalShot.getPosition().y);
        drawables.add(graphicalShot);
        physicalShot.setUpdatableSprite(graphicalShot);
        objectsMap.put(physicalShot, graphicalShot);

        generateAnimatedExplosion(physicalShot);
    }

     private void generateAnimatedExplosion(PhysicalShot physicalShot) {
        String spriteName = "shot_" + physicalShot.shotType.getName();
        // TODO: take into account body's origin
        float x = (physicalShot.playerType == PlayerType.FIRST_PLAYER) ?
                physicalShot.getPosition().x - SceneConstants.EXPLOSION_SCALE * explosionAnimation.getKeyFrames()[0].getRegionWidth() / 6f:
                physicalShot.getPosition().x + SceneConstants.SHOTS_SCALE * objects.findRegion(spriteName).originalWidth -
                        SceneConstants.EXPLOSION_SCALE * explosionAnimation.getKeyFrames()[0].getRegionWidth() * (5f / 6f);
        float y = physicalShot.getPosition().y - (SceneConstants.EXPLOSION_SCALE * explosionAnimation.getKeyFrames()[0].getRegionHeight() -
                SceneConstants.SHOTS_SCALE * objects.findRegion(spriteName).originalHeight) / 2f;

        animations.add(new AnimatedInstance(explosionAnimation, new Location(x, y, 0, SceneConstants.EXPLOSION_SCALE)));
    }

    public void generateAnimatedPlayerTurn(PlayerType playerTurn) {
        String name;
        if (playerTurn == PlayerType.FIRST_PLAYER) {
            animations.add(new AnimatedInstance(coinP1TurnAnimation, new Location(
                    SceneConstants.coinX, SceneConstants.coinY, 0, SceneConstants.COIN_SCALE)));
            name = "p1";
        }
        else {
            animations.add(new AnimatedInstance(coinP2TurnAnimation, new Location(
                    SceneConstants.coinX, SceneConstants.coinY, 0, SceneConstants.COIN_SCALE)));
            name = "p2";
        }
        Sprite text = texts.createSprite(name + "_turn");
        text.setPosition(
                SceneConstants.coinX - SceneConstants.TURN_SCALE * (text.getWidth() - coinP1TurnAnimation.getKeyFrames()[0].getRegionWidth()) / 2f,
                SceneConstants.coinY + SceneConstants.COIN_SCALE * coinP1TurnAnimation.getKeyFrames()[0].getRegionHeight());
        text.setSize(text.getWidth() * SceneConstants.TURN_SCALE, text.getHeight() * SceneConstants.TURN_SCALE);
        text.setAlpha(0.0f);
        animations.add(new AnimatedFading(text, 0.02f));
    }

    public void generateAnimatedImpact(PhysicalShot shot) {
        String spriteName = "shot_" + shot.shotType.getName();
        // TODO: take into account body's origin
        float x = shot.getPosition().x - (SceneConstants.IMPACT_SCALE * impactAnimation.getKeyFrames()[0].getRegionWidth() -
                SceneConstants.SHOTS_SCALE * objects.findRegion(spriteName).originalWidth) / 2f;
        float y = shot.getPosition().y - (SceneConstants.IMPACT_SCALE * impactAnimation.getKeyFrames()[0].getRegionHeight() -
                SceneConstants.SHOTS_SCALE * objects.findRegion(spriteName).originalHeight) / 2f;
        animations.add(new AnimatedInstance(impactAnimation, new Location(x, y, 0, SceneConstants.IMPACT_SCALE)));
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
        animations.add(new AnimatedFading(objectsMap.get(object).getSprite(), 0.03f));
        drawables.remove(objectsMap.get(object));
        objectsMap.remove(object);
    }

    public void draw(Batch batch) {
        for (Drawable object : drawables) {
            object.draw(batch);
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
