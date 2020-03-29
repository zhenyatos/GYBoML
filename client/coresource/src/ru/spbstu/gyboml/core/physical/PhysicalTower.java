package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import ru.spbstu.gyboml.core.util.PhysicsShapeCache;

import java.io.File;
import java.io.InputStream;

import ru.spbstu.gyboml.core.Constants;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.Tower;

public class PhysicalTower extends Tower {
    private final String PATH = "physics/objects.xml";
    private Body tower;
    private Body cannon;

    public PhysicalTower(Position pos, PlayerType playerType, World world) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH);

        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(is);
        String playerName = (playerType == PlayerType.FIRST_PLAYER) ? "_p1_" : "_p2_";
        tower = physicsShapeCache.createBody("tower" + playerName + "base", world, pos.scale, pos.scale);
        cannon = physicsShapeCache.createBody("tower" + playerName + "cannon", world, pos.scale, pos.scale);

        // Tower doesn't move => static
        tower.setType(BodyDef.BodyType.StaticBody);
        // Cannon can move, but doesn't interact => kinematic
        cannon.setType(BodyDef.BodyType.DynamicBody);
        cannon.setGravityScale(0.f);

        tower.setTransform(pos.x, pos.y, 0);
        cannon.setTransform(pos.x + (40.f * pos.scale), pos.y + (740.f * pos.scale), 0);

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.initialize(tower, cannon, new Vector2(pos.x + (40.f * pos.scale), pos.y + (770.f * pos.scale)));
        jointDef.type = JointDef.JointType.RevoluteJoint;
        jointDef.collideConnected = false;
        jointDef.motorSpeed = -100.f * pos.scale;
        jointDef.enableMotor = true;
        jointDef.enableLimit = false;
        jointDef.maxMotorTorque = 100000;

        RevoluteJoint joint = (RevoluteJoint)world.createJoint(jointDef);
    }
}
