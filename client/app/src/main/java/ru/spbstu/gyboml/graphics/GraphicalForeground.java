package main.java.ru.spbstu.gyboml.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class GraphicalForeground implements Drawable {
    private final Sprite foreground;

    public GraphicalForeground(Sprite foreground, float scale) {
        foreground.setSize(foreground.getWidth() * scale, foreground.getHeight() * scale);
        this.foreground = foreground;
    }

    @Override
    public void draw(Batch batch) {
        foreground.draw(batch);
    }

    @Override
    public void setSize(float width, float height) {
        foreground.setSize(width, height);
    }

    @Override
    public void setPosition(float x, float y) {
        foreground.setPosition(x, y);
    }

    @Override
    public void setOrigin(float x, float y) {
        foreground.setOrigin(x, y);
    }

    @Override
    public void setRotation(float degrees) {
        foreground.setRotation(degrees);
    }

    @Override
    public float getWidth() {
        return foreground.getWidth();
    }

    @Override
    public float getHeight() {
        return foreground.getHeight();
    }


}
