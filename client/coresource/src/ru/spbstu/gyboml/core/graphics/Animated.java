package ru.spbstu.gyboml.core.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;

import ru.spbstu.gyboml.core.physical.Location;

public interface Animated {
    void draw(Batch batch);
    boolean isFinished();
    Location getLocation();
}
