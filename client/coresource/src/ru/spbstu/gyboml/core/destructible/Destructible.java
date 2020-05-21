package ru.spbstu.gyboml.core.destructible;

import org.jetbrains.annotations.NotNull;
import ru.spbstu.gyboml.core.damage.Effect;
import ru.spbstu.gyboml.core.damage.Damage;
import ru.spbstu.gyboml.core.event.Events;
import ru.spbstu.gyboml.core.physical.PhysicalBlock;

import java.lang.reflect.Method;
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
        calcPoints(damage.value * (1 - material.getDefenceRatio()));
        HP -= damage.value;
        activeEffects.addAll(damage.getEffects());
        Events.get().emit(this,
                Events.get().find(this.getClass(), "handleDamage", Damage.class), HP);
    }

    public void applyEffects() {
        for (Effect effect : activeEffects)
            effect.apply(this);
        activeEffects.removeIf(effect -> !effect.isActive());
    }

    public float getHP() {
        return HP;
    }

    public void calcPoints(float dmgvalue) {
        Method thisMethod = Events.get().find(PhysicalBlock.class, "calcPoints", float.class);
        int points = (int)(dmgvalue * 0.5f);
        Events.get().emit(this, thisMethod, points);
    }
}
