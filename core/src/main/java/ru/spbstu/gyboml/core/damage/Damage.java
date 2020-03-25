package ru.spbstu.gyboml.core.damage;

import java.util.ArrayList;
import java.util.List;

public class Damage {
    public final int value;
    private List<Effect> effects;

    public Damage(int value) {
        this.value = Math.max(value, 0);  // damage can't be negative
        effects = new ArrayList<>();
    }

    public void addEffect(Effect effect) {
        if (effect != null)
            effects.add(effect);
    }

    public List<Effect> getEffects() {
        return effects;
    }
}
