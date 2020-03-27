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
    private Body tower;
    private Body cannon;

    public PhysicalTower(Position pos, PlayerType playerType, World world) {
        File file = new File(Constants.RES_PATH + PATH);
        FileHandle fileHandle = new FileHandle(file);

        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(fileHandle);
        String playerName = (playerType == PlayerType.FIRST_PLAYER) ? "_p1_" : "_p2_";
        tower = physicsShapeCache.createBody("tower" + playerName + "base", world, pos.scale, pos.scale);
        cannon = physicsShapeCache.createBody("tower" + playerName + "cannon", world, pos.scale, pos.scale);

        // Tower doesn't move => static
        tower.setType(BodyDef.BodyType.StaticBody);
        // Cannon can move, but doesn't interact => kinematic
        cannon.setType(BodyDef.BodyType.DynamicBody);
        cannon.setGravityScale(0.f);

        tower.setTransform(pos.x, pos.y, 0);
        cannon.setTransform(pos.x + (40.f * pos.scale), pos.y + (760.f * pos.scale), 0);
        tower.setActive(false);
        cannon.setAngularVelocity(1.f);
    }
}
