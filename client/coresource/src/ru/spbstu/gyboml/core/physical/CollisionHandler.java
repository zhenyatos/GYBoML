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
    private final float marginSpeed = 128.0f;   // x^2 + y^2

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
            //Destructible block = (Destructible) objA;
            PhysicalBlock block = (PhysicalBlock) objA;
            PhysicalShot shot = (PhysicalShot) objB;
            block.handleDamage(shot.generateDamage(block));
            if (!shot.getVelocity().isZero(marginSpeed)) {
                if (graphicalScene != null) {
                    graphicalScene.generateAnimatedImpact(shot);
                }
                if (soundEffects != null) {
                    if (block.getHP() > 0)
                        soundEffects.playImpact(block.material);
                    else
                        soundEffects.playBroken(block.material);
                }
            }
            return;
        }
        if (typeA == Type.SHOT && typeB == Type.BLOCK) {
            PhysicalShot shot = (PhysicalShot) objA;
            PhysicalBlock block = (PhysicalBlock) objB;
            //Destructible block = (Destructible) objB;
            block.handleDamage(shot.generateDamage(block));
            if (!shot.getVelocity().isZero(marginSpeed)) {
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
            return;
        }

        // Shot - Castle
        if (typeA == Type.SHOT && typeB == Type.CASTLE) {
            PhysicalShot shot = (PhysicalShot) objA;
            PhysicalCastle castle = (PhysicalCastle) objB;
            if (bodyB == castle.getFront())
                castle.handleDamage(shot.generateDamage(castle));
            else {
                Damage damage = shot.generateDamage(castle);
                // Double the damage
                castle.handleDamage(damage);
                castle.handleDamage(damage);
            }
            if (!shot.getVelocity().isZero(marginSpeed)) {
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
            //System.out.println(castle.getHP());
            return;
        }
        if (typeA == Type.CASTLE && typeB == Type.SHOT) {
            PhysicalCastle castle = (PhysicalCastle) objA;
            PhysicalShot shot = (PhysicalShot) objB;
            if (bodyA == castle.getFront())
                castle.handleDamage(shot.generateDamage(castle));
            else {
                Damage damage = shot.generateDamage(castle);
                // Double the damage
                castle.handleDamage(damage);
                castle.handleDamage(damage);
            }
            if (!shot.getVelocity().isZero(marginSpeed)) {
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
}
