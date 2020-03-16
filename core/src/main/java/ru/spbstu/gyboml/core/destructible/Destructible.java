package ru.spbstu.gyboml.core.destructible;

import org.jetbrains.annotations.NotNull;
import ru.spbstu.gyboml.core.damage.Effect;
import ru.spbstu.gyboml.core.damage.Damage;

import java.util.ArrayList;
import java.util.List;

public class Destructible {

    private int hitpoints;
    private List<Effect> activeEffects;
    public final Material material;

    public Destructible(int hitpoints, Material material) {
        this.hitpoints = hitpoints;
        this.material = material;
        activeEffects = new ArrayList<>();
    }

    public void handleDamage(@NotNull Damage damage) {
        hitpoints -= damage.value;
        activeEffects.addAll(damage.getEffects());
    }

    public void applyEffects() {
        for (Effect effect : activeEffects)
            effect.apply(this);
        activeEffects.removeIf(effect -> !effect.isActive());
    }

    public int getHitpoints() {
        return hitpoints;
    }
}
