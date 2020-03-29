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
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.io.File;

import ru.spbstu.gyboml.core.Constants;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.Tower;

public class PhysicalTower extends Tower {
    private final String PATH = "physics/objects.xml";
    private PlayerType playerType;
    private Body tower;
    private Body cannon;
    private RevoluteJoint joint;

    public PhysicalTower(Position pos, PlayerType playerType, World world) {
        this.playerType = playerType;

        File file = new File(Constants.RES_PATH + PATH);
        FileHandle fileHandle = new FileHandle(file);

        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(fileHandle);
        String playerName = (playerType == PlayerType.FIRST_PLAYER) ? "_p1_" : "_p2_";
        tower = physicsShapeCache.createBody("tower" + playerName + "base", world, pos.scale, pos.scale);
        cannon = physicsShapeCache.createBody("tower" + playerName + "cannon", world, pos.scale, pos.scale);

        // Tower doesn't move => static
        tower.setType(BodyDef.BodyType.StaticBody);
        // Cannon can move, but doesn't interact => dynamic
        cannon.setType(BodyDef.BodyType.DynamicBody);

        tower.setTransform(pos.x, pos.y, 0);
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        if (playerType == PlayerType.FIRST_PLAYER) {
            cannon.setTransform(pos.x + (40.f * pos.scale), pos.y + (800.f * pos.scale), 0);
            Vector2 jointPos = new Vector2(cannon.getPosition().x + 80.f * pos.scale, cannon.getPosition().y + 45.f * pos.scale);
            revoluteJointDef.initialize(cannon, tower, jointPos);
            revoluteJointDef.motorSpeed = -(float)Math.PI * 2;
            revoluteJointDef.upperAngle = 0.0f;
            revoluteJointDef.lowerAngle = -(float)Math.PI / 3;
        }
        else {
            cannon.setTransform(pos.x - (40.f * pos.scale), pos.y + (800.f * pos.scale), 0);
            Vector2 jointPos = new Vector2(cannon.getPosition().x + 140.f * pos.scale, cannon.getPosition().y + 45.f * pos.scale);
            revoluteJointDef.initialize(cannon, tower, jointPos);
            revoluteJointDef.motorSpeed = (float)Math.PI * 2;
            revoluteJointDef.upperAngle = (float)Math.PI / 3;
            revoluteJointDef.lowerAngle = 0.0f;
        }


        revoluteJointDef.maxMotorTorque = 100;
        revoluteJointDef.enableMotor = true;
        revoluteJointDef.enableLimit = true;
        joint = (RevoluteJoint)world.createJoint(revoluteJointDef);
    }

    public RevoluteJoint getJoint() {
        return joint;
    }
}
