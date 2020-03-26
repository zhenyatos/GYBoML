package ru.spbstu.gyboml;

import ru.spbstu.gyboml.core.Effect;
import ru.spbstu.gyboml.core.damage.Damage;
import ru.spbstu.gyboml.core.destructible.Destructible;

import org.junit.Test;
import static org.junit.Assert.*;

public class DestructibleTest {
    final int FIRE_TURNS = 2;
    final int FIRE_DAMAGE_SCALE = 2;
    final int HP = 20;
    final int DAMAGE = 2;

    Effect fire = new Effect() {
        private int turnsRemained = FIRE_TURNS;

        @Override
        public void apply(Destructible object) {
            Damage fireDamage = new Damage(turnsRemained * FIRE_DAMAGE_SCALE);
            object.handleDamage(fireDamage);
            turnsRemained--;
        }

        @Override
        public boolean isActive() {
            return (turnsRemained != 0);
        }
    };

    @Test
    public void damageCalculationsAreCorrect() {
        Destructible block = new Destructible(HP);
        Damage damage = new Damage(DAMAGE);
        damage.addEffect(fire);

        block.handleDamage(damage);
        assertEquals(HP-2, block.getHitpoints());

        block.applyEffects();
        assertEquals(HP-6, block.getHitpoints());
        block.applyEffects();
        assertEquals(HP-8, block.getHitpoints());
        block.applyEffects();
        assertEquals(HP-8, block.getHitpoints());
    }
}
