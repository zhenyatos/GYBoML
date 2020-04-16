package main.java.ru.spbstu.gyboml.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Animated {
    void draw(Batch batch);
    boolean isFinished();
}
