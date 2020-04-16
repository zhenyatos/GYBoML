package main.java.ru.spbstu.gyboml.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import ru.spbstu.gyboml.core.physical.Updatable;

public class GraphicalShot implements Drawable, Updatable {
    private Sprite sprite;

    public GraphicalShot(Sprite sprite, float scale) {
        sprite.setSize(sprite.getWidth() * scale, sprite.getHeight() * scale);
        this.sprite = sprite;
    }

    public Sprite getSprite() { return sprite; }

    @Override
    public void draw(Batch batch) {
        sprite.draw(batch);
    }

    @Override
    public void setSize(float width, float height) {
        sprite.setSize(width, height);
    }

    @Override
    public void setPosition(float x, float y) {
        sprite.setPosition(x, y);
    }

    @Override
    public void setOrigin(float x, float y) {
        sprite.setOrigin(x, y);
    }

    @Override
    public void setRotation(float degrees) {
        sprite.setRotation(degrees);
    }

    @Override
    public float getWidth() {
        return sprite.getWidth();
    }

    @Override
    public float getHeight() {
        return sprite.getHeight();
    }

    @Override
    public void setUpdatablePartPosition(Vector2 position) { sprite.setPosition(position.x, position.y); }

    @Override
    public void setUpdatablePartAngle(float angle) { sprite.setRotation(angle); }
}
