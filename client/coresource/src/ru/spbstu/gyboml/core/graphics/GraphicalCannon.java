package ru.spbstu.gyboml.core.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class GraphicalCannon implements Drawable, Updatable {
    private final Sprite cannon;

    public GraphicalCannon(Sprite cannon, float scale) {
        cannon.setSize(cannon.getWidth() * scale, cannon.getHeight() *  scale);
        this.cannon = cannon;
    }

    @Override
    public void draw(Batch batch) {
        cannon.draw(batch);
    }

    @Override
    public void setSize(float width, float height) {
        cannon.setSize(width, height);
    }

    @Override
    public void setPosition(float x, float y) {
        cannon.setPosition(x, y);
    }

    @Override
    public void setOrigin(float x, float y) {
        cannon.setOrigin(x, y);
    }

    @Override
    public void setRotation(float degrees) {
        cannon.setRotation(degrees);
    }

    @Override
    public float getWidth() {
        return cannon.getWidth();
    }

    @Override
    public float getHeight() {
        return cannon.getHeight();
    }

    @Override
    public void setUpdatablePartPosition(Vector2 position) { cannon.setPosition(position.x, position.y); }

    @Override
    public void setUpdatablePartAngle(float angle) { cannon.setRotation(angle); }
}
