package ru.spbstu.gyboml.core.damage;

import ru.spbstu.gyboml.core.destructible.Destructible;

public interface Effect {
    void apply(Destructible object);
    boolean isActive();
}
