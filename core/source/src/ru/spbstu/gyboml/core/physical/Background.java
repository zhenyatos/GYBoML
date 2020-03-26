package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.io.File;

public class Background {
    private final Body body;
    public Background(World world, float x, float y, float SCALE) {
        File file = new File("source/res/physics/background.xml");
        PhysicsShapeCache physicsBodies = new PhysicsShapeCache(new FileHandle(file));
        body = physicsBodies.createBody("bg_land", world, SCALE, SCALE);
        body.setTransform(x, y, 0);
        body.setType(BodyDef.BodyType.StaticBody);
    }

}
