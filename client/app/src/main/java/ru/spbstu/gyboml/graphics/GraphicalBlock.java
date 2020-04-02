package main.java.ru.spbstu.gyboml.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class GraphicalBlock implements Drawable {
    private final Sprite intactBlock;
    private final Sprite damagedBlock;
    private Sprite currentSprite;

    public GraphicalBlock(Sprite intactBlock, Sprite damagedBlock, float scale) {
        intactBlock.setSize(intactBlock.getWidth() * scale, intactBlock.getHeight() * scale);
        damagedBlock.setSize(damagedBlock.getWidth() * scale, damagedBlock.getHeight() * scale);
        this.intactBlock = intactBlock;
        this.damagedBlock = damagedBlock;

        this.currentSprite = intactBlock;
    }

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
}
