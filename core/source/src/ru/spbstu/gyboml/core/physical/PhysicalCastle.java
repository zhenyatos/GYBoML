package ru.spbstu.gyboml.core.physical;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.io.File;

import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.destructible.Castle;

public class PhysicalCastle extends Castle {
    private Body front;
    private Body tower;

    public PhysicalCastle(int HP, float x, float y, float scale, PlayerType playerType, World world) {
        super(HP);
        File file = new File("./res/physics/objects.xml");
        FileHandle fileHandle = new FileHandle(file);
        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(fileHandle);
        String playerName = (playerType == PlayerType.FIRST_PLAYER) ? "_p1_" : "_p2_";
        front = physicsShapeCache.createBody("castle" + playerName + "front", world, scale, scale);
        tower = physicsShapeCache.createBody("castle" + playerName + "tower", world, scale, scale);
        front.setTransform(x, y, 0);
        tower.setTransform(x, y, 0);
    }

    public Vector2 getPosition() { return front.getPosition(); }
}
