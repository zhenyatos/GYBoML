package main.java.ru.spbstu.gyboml.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import ru.spbstu.gyboml.core.physical.Updatable;

public class GraphicalBasicShot implements Drawable, Updatable {
    private Sprite ball;

    public GraphicalBasicShot(Sprite ball, float scale) {
        ball.setSize(ball.getWidth() * scale, ball.getHeight() * scale);
        this.ball = ball;
    }

    public Sprite getSprite() { return ball; }

    @Override
    public void draw(Batch batch) {
        ball.draw(batch);
    }

    @Override
    public void setSize(float width, float height) {
        ball.setSize(width, height);
    }

    @Override
    public void setPosition(float x, float y) {
        ball.setPosition(x, y);
    }

    @Override
    public void setOrigin(float x, float y) {
        ball.setOrigin(x, y);
    }

    @Override
    public void setRotation(float degrees) {
        ball.setRotation(degrees);
    }

    @Override
    public float getWidth() {
        return ball.getWidth();
    }

    @Override
    public float getHeight() {
        return ball.getHeight();
    }

    @Override
    public void setUpdatablePartPosition(Vector2 position) { ball.setPosition(position.x, position.y); }

    @Override
    public void setUpdatablePartAngle(float angle) { ball.setRotation(angle); }
}
