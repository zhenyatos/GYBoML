package ru.spbstu.gyboml.core.scene;

public class SceneConstants {
    private SceneConstants() {}

    // world / canvas constants
    public static final float minRatio     = 3f / 2f;
    public static final float minWidth     = 50;
    public static final float minHeight    = minWidth / minRatio;
    public static final float worldScale   = 1.5f;
    public static final float worldWidth   = minWidth * worldScale;
    public static final float worldHeight  = minHeight;
    public static final float maxXRatio    = 19.5f / 9f;
    public static final float maxYRatio    = 4f / 3f;
    public static final float canvasWidth  = worldWidth + minWidth * (maxXRatio / minRatio - 1);
    public static final float canvasHeight = worldHeight + minHeight * (minRatio / maxYRatio - 1);
    public static final float backgroundX = 0 - (canvasWidth - worldWidth) / 2;
    public static final float backgroundY = 0 - (canvasHeight - worldHeight) / 2;

    // objects parameters
    public static final int resolutionWidth = 3734;
    public static final int castleWidth = 240;
    public static final int towerWidth = 220;
    public static final int shotBasicWidth = 300;
    public static final int shotBasicHeight = 300;
    public static final int blockWoodWidth = 200;
    public static final int blockWoodHeight = 600;

    public static final float SCALE = canvasWidth / resolutionWidth;
    public static final float BLOCKS_SCALE = SCALE * 0.35f;
    public static final float SHOTS_SCALE  = SCALE * 0.22f;
    public static final float EXPLOSION_SCALE = SCALE * 0.85f;
    public static final float COIN_SCALE = SCALE * 1.8f;

}
