package ru.spbstu.gyboml.core.physical;

/**
 * Interface to implement by physical classes
 * that has mobile parts to pass its parts positions to graphical classes.
 */
public interface Movable {
    void setMovableSprite(Updatable sprite);
    void updateMovableSprite();
}
