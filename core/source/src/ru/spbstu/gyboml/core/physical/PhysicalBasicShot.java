package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.io.File;

import ru.spbstu.gyboml.core.Constants;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.shot.BasicShot;

public class PhysicalBasicShot extends BasicShot {
    private final String PATH = "physics/objects.xml";
    private Body shot;

    public PhysicalBasicShot(int basicDamage, Position pos, Vector2 initVelocity, World world) {
        super(basicDamage);

        File file = new File(Constants.RES_PATH + PATH);
        FileHandle fileHandle = new FileHandle(file);

        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(fileHandle);
        shot = physicsShapeCache.createBody("cannonball", world, pos.scale, pos.scale);
        shot.setLinearVelocity(initVelocity);
    }
}
