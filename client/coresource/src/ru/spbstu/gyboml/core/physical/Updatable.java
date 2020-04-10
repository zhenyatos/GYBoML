package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.math.Vector2;

/**
 * Interface to implement by graphical classes
 * which need their sprites positions to be updated.
 */
public interface Updatable {
   void setMovablePartPosition(Vector2 position);
   void setMovablePartAngle(float angle);
}
