package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

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

        // Background - Block
        if (typeA == Type.BLOCK && typeB == Type.BACKGROUND)
            System.out.println("Background-Block");
        if (typeA == Type.BACKGROUND && typeB == Type.BLOCK)
            System.out.println("Background-Block");

        // Block - Block
        if (typeA == Type.BLOCK && typeB == Type.BLOCK)
            System.out.println("Block-Block");
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
