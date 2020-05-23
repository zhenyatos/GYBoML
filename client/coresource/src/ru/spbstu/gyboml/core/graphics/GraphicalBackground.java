package ru.spbstu.gyboml.core.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;


public class GraphicalBackground implements Drawable {
    private final Sprite black;
    private final Sprite sky;
    private final Sprite desert;
    private final Sprite land;

    public GraphicalBackground(Sprite black, Sprite sky, Sprite desert, Sprite land, float scale) {
        black.setSize(black.getWidth() * scale, black.getHeight() * scale);
        sky.setSize(sky.getWidth() * scale, sky.getHeight() * scale);
        desert.setSize(desert.getWidth() * scale, desert.getHeight() * scale);
        land.setSize(land.getWidth() * scale, land.getHeight() * scale);

        this.black = black;
        this.sky = sky;
        this.desert = desert;
        this.land = land;
    }

    @Override
    public void draw(Batch batch) {
        black.draw(batch);
        sky.draw(batch);
        desert.draw(batch);
        land.draw(batch);
    }

    @Override
    public void setSize(float width, float height) {
        black.setSize(width, height);
        sky.setSize(width, height);
        desert.setSize(width, height);
        land.setSize(width, height);
    }

    @Override
    public void setPosition(float x, float y) {
        black.setPosition(x, y);
        sky.setPosition(x, y);
        desert.setPosition(x, y);
        land.setPosition(x, y);
    }

    @Override
    public void setOrigin(float x, float y) {
        black.setOrigin(x, y);
        sky.setOrigin(x, y);
        desert.setOrigin(x, y);
        land.setOrigin(x, y);
    }

    @Override
    public void setRotation(float degrees) {
        black.setRotation(degrees);
        sky.setRotation(degrees);
        desert.setRotation(degrees);
        land.setRotation(degrees);
    }

    @Override
    public float getWidth() {
        return land.getWidth();
    }

    @Override
    public float getHeight() {
        return land.getHeight();
    }

}
