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

public class PhysicalTower implements Physical, Movable {
    private Body tower;
    private Body cannon;
    private RevoluteJoint joint;
    private Vector2 jointPosition;
    private PlayerType playerType;
    private float barrelLength;

    private Updatable sprite = null;

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
        float cannonWidth = 220 * location.scale;    // .xml full body width
        float cannonHeight = 90 * location.scale;    // .xml full body height
        float cannonX =  40 * location.scale;        // in front of the tower
        float cannonY = 800 * location.scale;        // over the tower
        barrelLength = cannonWidth - 80 * location.scale;

        if (playerType == PlayerType.FIRST_PLAYER) {
            cannon.setTransform(location.x + cannonX, location.y + cannonY, location.angle);
            jointPosition = new Vector2(cannon.getPosition().x + cannonWidth - barrelLength, cannon.getPosition().y + cannonHeight / 2f);
            revoluteJointDef.initialize(cannon, tower, jointPosition);
            revoluteJointDef.motorSpeed = -(float)Math.PI / 2;
            revoluteJointDef.upperAngle = 0.0f;
            revoluteJointDef.lowerAngle = -(float)Math.PI / 3;
        }
        else {
            cannon.setTransform(location.x - cannonX, location.y + cannonY, location.angle);
            jointPosition = new Vector2(cannon.getPosition().x + barrelLength, cannon.getPosition().y + cannonHeight / 2f);
            revoluteJointDef.initialize(cannon, tower, jointPosition);
            revoluteJointDef.motorSpeed = (float)Math.PI / 2;
            revoluteJointDef.upperAngle = (float)Math.PI / 3;
            revoluteJointDef.lowerAngle = 0.0f;
        }

        revoluteJointDef.maxMotorTorque = 50 / location.scale;
        revoluteJointDef.enableMotor = true;
        revoluteJointDef.enableLimit = true;
        joint = (RevoluteJoint)world.createJoint(revoluteJointDef);
    }

    public PlayerType getPlayerType() { return playerType; }

    public RevoluteJoint getJoint() { return joint; }

    public Vector2 getJointPosition() { return jointPosition; }

    public float getBarrelLength() { return barrelLength; }

    @Override
    public Vector2 getPosition() { return tower.getPosition(); }

    @Override
    public Vector2 getMovablePartPosition() { return cannon.getPosition(); }

    @Override
    public float getMovablePartAngle() { return cannon.getAngle(); }

    @Override
    public void setUpdatableSprite(Updatable sprite) {
        this.sprite = sprite;
    }

    @Override
    public void updateSprite() {
        if (this.sprite != null) {
            sprite.setUpdatablePartPosition(cannon.getPosition());
            sprite.setUpdatablePartAngle((float) Math.toDegrees(cannon.getAngle()));
        }
    }
}
