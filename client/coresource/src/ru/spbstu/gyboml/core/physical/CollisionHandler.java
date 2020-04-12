package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;

import ru.spbstu.gyboml.core.damage.Damage;
import ru.spbstu.gyboml.core.destructible.Destructible;
import ru.spbstu.gyboml.core.shot.Shot;
import sun.security.krb5.internal.crypto.Des;

public class CollisionHandler implements ContactListener {
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
            Destructible block = (Destructible) objA;
            Shot shot = (Shot) objB;
            block.handleDamage(shot.generateDamage(block));
            return;
        }
        if (typeA == Type.SHOT && typeB == Type.BLOCK) {
            Shot shot = (Shot) objA;
            Destructible block = (Destructible) objB;
            block.handleDamage(shot.generateDamage(block));
            return;
        }

        // Shot - Castle
        if (typeA == Type.SHOT && typeB == Type.CASTLE) {
            Shot shot = (Shot) objA;
            PhysicalCastle castle = (PhysicalCastle) objB;
            if (bodyB == castle.getFront())
                castle.handleDamage(shot.generateDamage(castle));
            else {
                Damage damage = shot.generateDamage(castle);
                // Double the damage
                castle.handleDamage(damage);
                castle.handleDamage(damage);
            }
            System.out.println(castle.getHP());
            return;
        }
        if (typeA == Type.CASTLE && typeB == Type.SHOT) {
            PhysicalCastle castle = (PhysicalCastle) objA;
            Shot shot = (Shot) objB;
            if (bodyA == castle.getFront())
                castle.handleDamage(shot.generateDamage(castle));
            else {
                Damage damage = shot.generateDamage(castle);
                // Double the damage
                castle.handleDamage(damage);
                castle.handleDamage(damage);
            }
            System.out.println(castle.getHP());
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
