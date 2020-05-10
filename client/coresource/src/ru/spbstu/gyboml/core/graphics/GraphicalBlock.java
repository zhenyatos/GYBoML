package ru.spbstu.gyboml.core.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class GraphicalBlock implements Drawable, Updatable {
    private final Sprite intactBlock;
    private final Sprite damagedBlock;
    private final Sprite specialBlock;    // e.g. block_wood_fired.png
    private Sprite currentSprite;

    public GraphicalBlock(Sprite intactBlock, Sprite damagedBlock, Sprite specialBlock, float scale) {
        intactBlock.setSize(intactBlock.getWidth() * scale, intactBlock.getHeight() * scale);
        damagedBlock.setSize(damagedBlock.getWidth() * scale, damagedBlock.getHeight() * scale);
        if (specialBlock != null)
            specialBlock.setSize(specialBlock.getWidth() * scale, specialBlock.getHeight() * scale);
        this.intactBlock   = intactBlock;
        this.damagedBlock  = damagedBlock;
        this.currentSprite = intactBlock;
        this.specialBlock = specialBlock;
    }

    @Override
    public Sprite getSprite() { return currentSprite; }

    @Override
    public void draw(Batch batch) {
        currentSprite.draw(batch);
    }

    @Override
    public void setSize(float width, float height) {
        intactBlock.setSize(width, height);
        damagedBlock.setSize(width, height);
    }

    @Override
    public void setPosition(float x, float y) {
        intactBlock.setPosition(x, y);
        damagedBlock.setPosition(x, y);
    }

    @Override
    public void setOrigin(float x, float y) {
        intactBlock.setOrigin(x, y);
        damagedBlock.setOrigin(x, y);
    }

    @Override
    public void setRotation(float degrees) {
        intactBlock.setRotation(degrees);
        damagedBlock.setRotation(degrees);
    }

    @Override
    public float getWidth() {
        return currentSprite.getWidth();
    }

    @Override
    public float getHeight() {
        return currentSprite.getHeight();
    }

    @Override
    public void setUpdatablePartPosition(Vector2 position) {
        // change both to exclude further glitches
        intactBlock.setPosition(position.x, position.y);
        damagedBlock.setPosition(position.x, position.y);
    }

    @Override
    public void setUpdatablePartAngle(float angle) {
        // change both to exclude further glitches
        intactBlock.setRotation(angle);
        damagedBlock.setRotation(angle);
    }

    @Override
    public void setDamagedSprite() {
        currentSprite = damagedBlock;
    }

    @Override
    public void setSpecialSprite() {
        if (specialBlock != null)
            currentSprite = specialBlock;
    }
}
