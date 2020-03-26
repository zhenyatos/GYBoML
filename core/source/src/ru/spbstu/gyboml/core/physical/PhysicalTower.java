package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.io.File;

import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.Tower;

public class PhysicalTower extends Tower {
    private final String PATH = "source/res/physics/objects.xml";
    private Body tower;
    private Body cannon;

    public PhysicalTower(Position pos, PlayerType playerType, World world) {
        File file = new File(PATH);
        FileHandle fileHandle = new FileHandle(file);

        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(fileHandle);
        String playerName = (playerType == PlayerType.FIRST_PLAYER) ? "_p1_" : "_p2_";
        tower = physicsShapeCache.createBody("tower" + playerName + "base", world, pos.scale, pos.scale);
        cannon = physicsShapeCache.createBody("tower" + playerName + "cannon", world, pos.scale, pos.scale);

        tower.setTransform(pos.x, pos.y, 0);
        cannon.setTransform(pos.x, pos.y, 0);

        // Tower doesn't move => static
        tower.setType(BodyDef.BodyType.StaticBody);
        // Cannon can move, but doesn't interact => kinematic
        cannon.setType(BodyDef.BodyType.KinematicBody);
    }
}
