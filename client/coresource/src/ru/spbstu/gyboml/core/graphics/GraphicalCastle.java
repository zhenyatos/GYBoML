package ru.spbstu.gyboml.core.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class GraphicalCastle implements Drawable{
    private final Sprite front;
    private final Sprite back;
    private final Sprite tower;

    public GraphicalCastle(Sprite back, Sprite front, Sprite tower, float scale, int HP) {
        front.setSize(front.getWidth() * scale, front.getHeight() * scale);
        back.setSize(back.getWidth() * scale, back.getHeight() * scale);
        tower.setSize(tower.getWidth() * scale, tower.getHeight() * scale);
        this.front = front;
        this.back = back;
        this.tower = tower;
    }

    @Override
    public void draw(Batch batch) {
        back.draw(batch);
        front.draw(batch);
        tower.draw(batch);
    }

    @Override
    public void setSize(float width, float height) {
        front.setSize(width, height);
        back.setSize(width, height);
        tower.setSize(width, height);
    }

    @Override
    public void setPosition(float x, float y) {
        front.setPosition(x, y);
        back.setPosition(x, y);
        tower.setPosition(x, y);
    }

    @Override
    public void setOrigin(float x, float y) {
        front.setOrigin(x, y);
        back.setOrigin(x, y);
        tower.setOrigin(x, y);
    }

    @Override
    public void setRotation(float degrees) {
        front.setRotation(degrees);
        back.setRotation(degrees);
        tower.setRotation(degrees);
    }

    @Override
    public float getWidth() {
        return front.getWidth();
    }

    @Override
    public float getHeight() {
        return front.getHeight();
    }

}
