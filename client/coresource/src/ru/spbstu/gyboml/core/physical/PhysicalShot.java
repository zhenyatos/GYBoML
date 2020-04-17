package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.damage.Damage;
import ru.spbstu.gyboml.core.destructible.Destructible;
import ru.spbstu.gyboml.core.shot.ShotType;

abstract public class PhysicalShot implements Physical, Movable, Interactable {
    private Updatable sprite;
    protected Body body;
    public ShotType shotType;
    public PlayerType playerType;

    abstract public Damage generateDamage(Destructible destructible);

    public void setVelocity(Vector2 velocity) {
        body.setLinearVelocity(velocity);
    }

    public Vector2 getVelocity() { return body.getLinearVelocity(); }

    public Body getBody() { return body; }

    @Override
    public Vector2 getPosition() { return body.getPosition(); }

    @Override
    public Vector2 getMovablePartPosition() { return body.getPosition(); }

    @Override
    public float getMovablePartAngle() { return body.getAngle(); }

    @Override
    public void setUpdatableSprite(Updatable sprite) {
        this.sprite = sprite;
    }

    @Override
    public void updateSprite() {
        if (this.sprite != null) {
            sprite.setUpdatablePartPosition(body.getPosition());
            sprite.setUpdatablePartAngle((float) Math.toDegrees(body.getAngle()));
        }
    }

    @Override
    public Type getType() {
        return Type.SHOT;
    }
}
