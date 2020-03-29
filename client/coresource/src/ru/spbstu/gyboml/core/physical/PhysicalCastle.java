package ru.spbstu.gyboml.core.physical;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import ru.spbstu.gyboml.core.util.PhysicsShapeCache;

import java.io.File;
import java.io.InputStream;

import ru.spbstu.gyboml.core.Constants;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.destructible.Castle;

public class PhysicalCastle extends Castle {
    private final String PATH = "physics/objects.xml";
    private Body front;
    private Body tower;

    public PhysicalCastle(int HP, Position pos, PlayerType playerType, World world) {
        super(HP);
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH);

        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(is);
        String playerName = (playerType == PlayerType.FIRST_PLAYER) ? "_p1_" : "_p2_";
        front = physicsShapeCache.createBody("castle" + playerName + "front", world, pos.scale, pos.scale);
        tower = physicsShapeCache.createBody("castle" + playerName + "tower", world, pos.scale, pos.scale);

        front.setTransform(pos.x, pos.y, 0);
        tower.setTransform(pos.x, pos.y, 0);

        // Castle doesn't move => static components
        front.setType(BodyDef.BodyType.StaticBody);
        tower.setType(BodyDef.BodyType.StaticBody);
    }

    public Vector2 getPosition() { return front.getPosition(); }
}
