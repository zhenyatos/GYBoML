package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.math.Vector2;

/**
 * Common interface for physical classes.
 */
public interface Physical {
    String PHYSICS_PATH_BACKGROUND = "physics/background.xml";
    String PHYSICS_PATH_OBJECTS = "physics/objects.xml";

    Vector2 getPosition();
    default Vector2 getMovablePartPosition() { return null; }
    default float getMovablePartAngle() { return 0; }
}
