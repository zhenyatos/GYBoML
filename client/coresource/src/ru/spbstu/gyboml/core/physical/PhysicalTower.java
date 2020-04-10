package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import ru.spbstu.gyboml.core.util.PhysicsShapeCache;

import java.io.InputStream;

import ru.spbstu.gyboml.core.PlayerType;

public class PhysicalTower implements Physical, Updatable {
    private PlayerType playerType;
    private Body tower;
    private Body cannon;
    private RevoluteJoint joint;

    private Movable sprite = null;

    public PhysicalTower(Location location, PlayerType playerType, World world) {
        this.playerType = playerType;

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PHYSICS_PATH_OBJECTS);

        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(is);
        String playerName = (playerType == PlayerType.FIRST_PLAYER) ? "_p1_" : "_p2_";
        tower = physicsShapeCache.createBody("tower" + playerName + "base", world, location.scale, location.scale);
        cannon = physicsShapeCache.createBody("tower" + playerName + "cannon", world, location.scale, location.scale);

        // Tower doesn't move => static
        tower.setType(BodyDef.BodyType.StaticBody);
        // Cannon can move => dynamic
        cannon.setType(BodyDef.BodyType.DynamicBody);

        tower.setTransform(location.x, location.y, location.angle);
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        if (playerType == PlayerType.FIRST_PLAYER) {
            cannon.setTransform(location.x + (40.f * location.scale), location.y + (800.f * location.scale), location.angle);
            Vector2 jointPos = new Vector2(cannon.getPosition().x + 80.f * location.scale, cannon.getPosition().y + 45.f * location.scale);
            revoluteJointDef.initialize(cannon, tower, jointPos);
            revoluteJointDef.motorSpeed = -(float)Math.PI;
            revoluteJointDef.upperAngle = 0.0f;
            revoluteJointDef.lowerAngle = -(float)Math.PI / 3;
        }
        else {
            cannon.setTransform(location.x - (40.f * location.scale), location.y + (800.f * location.scale), location.angle);
            Vector2 jointPos = new Vector2(cannon.getPosition().x + 140.f * location.scale, cannon.getPosition().y + 45.f * location.scale);
            revoluteJointDef.initialize(cannon, tower, jointPos);
            revoluteJointDef.motorSpeed = (float)Math.PI;
            revoluteJointDef.upperAngle = (float)Math.PI / 3;
            revoluteJointDef.lowerAngle = 0.0f;
        }

        revoluteJointDef.maxMotorTorque = 50 / location.scale;
        revoluteJointDef.enableMotor = true;
        revoluteJointDef.enableLimit = true;
        joint = (RevoluteJoint)world.createJoint(revoluteJointDef);
    }

    public RevoluteJoint getJoint() { return joint; }

    @Override
    public Vector2 getPosition() { return tower.getPosition(); }

    @Override
    public Vector2 getUpdatablePosition() { return cannon.getPosition(); }

    @Override
    public float getUpdatableAngle() { return cannon.getAngle(); }

    @Override
    public void setMovableSprite(Movable sprite) {
        this.sprite = sprite;
    }

    @Override
    public void updateMovableSprite() {
        if (this.sprite != null) {
            sprite.setMovablePartPosition(cannon.getPosition());
            sprite.setMovablePartAngle((float) Math.toDegrees(cannon.getAngle()));
        }
    }
}
