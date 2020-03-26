package ru.spbstu.gyboml;

import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.damage.Effect;
import ru.spbstu.gyboml.core.damage.Damage;
import ru.spbstu.gyboml.core.destructible.Destructible;

import org.junit.Test;
import ru.spbstu.gyboml.core.destructible.Material;
import ru.spbstu.gyboml.core.shot.Shot;

import static org.junit.Assert.*;

public class SystemTest {
    final int FIRE_TURNS = 2;
    final int FIRE_DAMAGE_SCALE = 2;
    final int HP = 20;
    final int DAMAGE = 2;
    final int PLAYER_POINTS = 100;

    class FireEffect implements Effect {
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

    class FireShot implements Shot {

        @Override
        public Damage generateDamage(Destructible destructible) {
            Damage damage = new Damage((int)(FIRE_DAMAGE_SCALE * (1 - destructible.material.getDefenceRatio())));
            if (destructible.material == Material.WOOD)
                damage.addEffect(new FireEffect());
            return damage;
        }
    }

    @Test
    public void damageCalculationsAreCorrect() {
        Effect fire = new FireEffect();
        Destructible woodBlock = new Destructible(HP, Material.WOOD);
        Destructible stoneBlock = new Destructible(HP, Material.STONE);
        Shot fireShot = new FireShot();

        woodBlock.handleDamage(fireShot.generateDamage(woodBlock));
        stoneBlock.handleDamage(fireShot.generateDamage(stoneBlock));

        // Testing wooden block, fire effect should work here for 2 turns
        assertEquals(HP-1, woodBlock.getHitpoints());
        woodBlock.applyEffects();
        assertEquals(HP-5, woodBlock.getHitpoints());
        woodBlock.applyEffects();
        assertEquals(HP-7, woodBlock.getHitpoints());
        woodBlock.applyEffects();
        assertEquals(HP-7, woodBlock.getHitpoints());

        // Testing stone block
        assertEquals(HP-1, stoneBlock.getHitpoints());
        stoneBlock.applyEffects();
        assertEquals(HP-1, stoneBlock.getHitpoints());
    }

    @Test
    public void passTurnIsCorrect() {
        Player firstPlayer = new Player(PLAYER_POINTS, true);
        Player secondPlayer = new Player(PLAYER_POINTS, false);

        // Passing turn
        firstPlayer.passTurn(secondPlayer);
        assertFalse(firstPlayer.isMyTurn());
        assertTrue(secondPlayer.isMyTurn());
    }
}
