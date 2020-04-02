package ru.spbstu.gyboml.core.shot;

import ru.spbstu.gyboml.core.damage.Damage;
import ru.spbstu.gyboml.core.destructible.Destructible;

public interface Shot {
    Damage generateDamage(Destructible destructible);
}
