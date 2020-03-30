package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.math.Vector2;

public interface Movable {
   void setMovablePartPosition(Vector2 position);
   void setMovablePartAngle(float angle);
}
