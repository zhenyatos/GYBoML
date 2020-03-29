package main.java.ru.spbstu.gyboml.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import ru.spbstu.gyboml.core.Tower;

public class GraphicalTower extends Tower implements Drawable {
    private final Sprite tower;

    public GraphicalTower(Sprite tower, float scale) {
        tower.setSize(tower.getWidth() * scale, tower.getHeight() *  scale);
        this.tower = tower;
    }

    @Override
    public void draw(Batch batch) {
        tower.draw(batch);
    }

    @Override
    public void setSize(float width, float height) {
        tower.setSize(width, height);
    }

    @Override
    public void setPosition(float x, float y) {
        tower.setPosition(x, y);
    }

    @Override
    public void setOrigin(float x, float y) {
        tower.setOrigin(x, y);
    }

    @Override
    public void setRotation(float degrees) {
        tower.setRotation(degrees);
    }

    @Override
    public float getWidth() {
        return tower.getWidth();
    }

    @Override
    public float getHeight() {
        return tower.getHeight();
    }
}
