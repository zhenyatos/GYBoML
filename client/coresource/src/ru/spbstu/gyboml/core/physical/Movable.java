package ru.spbstu.gyboml.core.physical;

import ru.spbstu.gyboml.core.graphics.Updatable;

/**
 * Interface to implement by physical classes
 * which has mobile parts that update their positions in graphical classes.
 */
public interface Movable {
    void setUpdatableSprite(Updatable sprite);
    void updateSprite();
}
