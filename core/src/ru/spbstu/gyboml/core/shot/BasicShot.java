package ru.spbstu.gyboml.core.shot;

import ru.spbstu.gyboml.core.Constants;
import ru.spbstu.gyboml.core.damage.Damage;
import ru.spbstu.gyboml.core.destructible.Destructible;

public class BasicShot implements Shot {
    private int basicDamage;
    public BasicShot(int basicDamage) {
        this.basicDamage = basicDamage;
    }

    @Override
    public Damage generateDamage(Destructible destructible) {
        return new Damage((int)(basicDamage * (1 - destructible.material.getDefenceRatio())));
    }
}
