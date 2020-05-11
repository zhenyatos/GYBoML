package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import ru.spbstu.gyboml.core.damage.Damage;
import ru.spbstu.gyboml.core.destructible.Destructible;
import ru.spbstu.gyboml.core.scene.GraphicalScene;
import ru.spbstu.gyboml.core.scene.SoundEffects;

public class CollisionHandler implements ContactListener {
    private final GraphicalScene graphicalScene;
    private final SoundEffects soundEffects;

    public CollisionHandler(GraphicalScene graphicalScene, SoundEffects soundEffects) {
        this.graphicalScene = graphicalScene;
        this.soundEffects = soundEffects;
    }

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        Object objA = bodyA.getUserData();
        Object objB = bodyB.getUserData();

        if ((Interactable)objA == null || (Interactable)objB == null)
            return;

        Type typeA = ((Interactable) objA).getType();
        Type typeB = ((Interactable) objB).getType();

        // Shot - Block
        if (typeA == Type.BLOCK && typeB == Type.SHOT) {
            PhysicalBlock block = (PhysicalBlock) objA;
            PhysicalShot shot = (PhysicalShot) objB;
            collisionShotBlock(shot, block);
            return;
        }
        if (typeA == Type.SHOT && typeB == Type.BLOCK) {
            PhysicalShot shot = (PhysicalShot) objA;
            PhysicalBlock block = (PhysicalBlock) objB;
            collisionShotBlock(shot, block);
            return;
        }

        // Shot - Castle
        if (typeA == Type.SHOT && typeB == Type.CASTLE) {
            PhysicalShot shot = (PhysicalShot) objA;
            PhysicalCastle castle = (PhysicalCastle) objB;
            boolean doubleDamage = !(bodyB  == castle.getFront());
            collisionShotCastle(shot, castle, doubleDamage);
            return;
        }
        if (typeA == Type.CASTLE && typeB == Type.SHOT) {
            PhysicalCastle castle = (PhysicalCastle) objA;
            PhysicalShot shot = (PhysicalShot) objB;
            boolean doubleDamage = !(bodyA  == castle.getFront());
            collisionShotCastle(shot, castle, doubleDamage);
            return;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void collisionShotBlock(PhysicalShot shot, PhysicalBlock block) {
        block.handleDamage(shot.generateDamage(block));

        // Effects
        if (!shot.getVelocity().isZero(PhysicalShot.COLLISION_MARGIN)) {
            if (graphicalScene != null) {
                graphicalScene.generateAnimatedImpact(shot);
            }
            if (soundEffects != null) {
                if (block.getHP() > 0f)
                    soundEffects.playImpact(block.material);
                else
                    soundEffects.playBroken(block.material);
            }
        }
    }

    private void collisionShotCastle(PhysicalShot shot, PhysicalCastle castle, boolean doubleDamage) {
        if (doubleDamage) {
            Damage damage = shot.generateDamage(castle);
            // Double the damage
            castle.handleDamage(damage);
            castle.handleDamage(damage);
        }
        else
            castle.handleDamage(shot.generateDamage(castle));

        // Effects
        if (!shot.getVelocity().isZero(PhysicalShot.COLLISION_MARGIN)) {
            if (graphicalScene != null) {
                graphicalScene.generateAnimatedImpact(shot);
            }
            if (soundEffects != null) {
                if (castle.getHP() > 0f)
                    soundEffects.playImpact(castle.material);
                else
                    soundEffects.playBroken(castle.material);
            }
        }
    }
}
