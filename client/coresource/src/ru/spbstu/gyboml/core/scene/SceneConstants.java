package ru.spbstu.gyboml.core.scene;

public class SceneConstants {
    private SceneConstants() {}

    // world / canvas constants
    public static final float cameraRatio = 3f / 2f;
    public static final float cameraWidth = 50;
    public static final float cameraHeight = cameraWidth / cameraRatio;
    public static final float worldScale   = 1.5f;
    public static final float worldWidth   = cameraWidth * worldScale;
    public static final float worldHeight  = cameraHeight;
    public static final float maxXRatio    = 19.5f / 9f;
    public static final float maxYRatio    = 4f / 3f;
    public static final float canvasWidth  = worldWidth + cameraWidth * (maxXRatio / cameraRatio - 1);
    public static final float canvasHeight = worldHeight + cameraHeight * (cameraRatio / maxYRatio - 1);
    public static final float graphicalCanvasWidth = canvasWidth;
    public static final float graphicalCanvasHeight = worldHeight * worldScale + cameraHeight * (cameraRatio / maxYRatio - 1);
    public static final float backgroundX = 0 - (canvasWidth - worldWidth) / 2;
    public static final float backgroundY = 0 - (canvasHeight - worldHeight) / 2;
    public static final float graphicalXOffset = 0;
    public static final float graphicalYOffset = (graphicalCanvasHeight - canvasHeight) / 2;

    // objects parameters
    public static final int resolutionWidth = 3734;
    public static final int resolutionHeight = 1440;
    public static final int castleWidth = 240;
    public static final int towerWidth = 220;
    public static final int shotBasicWidth = 300;
    public static final int shotBasicHeight = 300;
    public static final int blockWoodWidth = 200;
    public static final int blockWoodHeight = 600;
    public static final float platformHeight = 364;

    // objects in-game parameters
    // TODO: balance hp
    public static final float castleHP = 200.0f;
    public static final float basicShotSpeed = 22.0f;

    public static final float SCALE = canvasWidth / resolutionWidth;
    public static final float TOWERS_SCALE = SCALE;
    public static final float CASTLES_SCALE = SCALE;
    public static final float BLOCKS_SCALE = SCALE * 0.35f;
    public static final float SHOTS_SCALE  = SCALE * 0.22f;
    public static final float EXPLOSION_SCALE = SCALE * 1.25f;
    public static final float COIN_SCALE = SCALE * 1.55f;
    public static final float TURN_SCALE = COIN_SCALE;
    public static final float IMPACT_SCALE = SCALE * 1.5f;
    public static final float coinX = backgroundX + (resolutionWidth  / 2.0f) * SCALE - 100.0f * COIN_SCALE;
    public static final float coinY = backgroundY + (resolutionHeight / 2.0f) * SCALE;
}
