package main.java.ru.spbstu.gyboml.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Drawable {
    void draw(Batch batch);
    void setPosition(float x, float y);
    void setSize(float width, float height);
    void setRotation(float degrees);
    void setOrigin(float x, float y);
    float getWidth();
    float getHeight();
}
