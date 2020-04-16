package ru.spbstu.gyboml.core.destructible;

import org.jetbrains.annotations.NotNull;
import ru.spbstu.gyboml.core.damage.Effect;
import ru.spbstu.gyboml.core.damage.Damage;

import java.util.ArrayList;
import java.util.List;

public class Destructible {
    public final Material material;
    protected final float initialHP;
    private float HP;
    private List<Effect> activeEffects;

    public Destructible(float HP, Material material) {
        this.material  = material;
        this.initialHP = HP;
        this.HP        = HP;
        activeEffects  = new ArrayList<>();
    }

    public void handleDamage(@NotNull Damage damage) {
        HP -= damage.value;
        activeEffects.addAll(damage.getEffects());
    }

    public void applyEffects() {
        for (Effect effect : activeEffects)
            effect.apply(this);
        activeEffects.removeIf(effect -> !effect.isActive());
    }

    public float getHP() {
        return HP;
    }
}
