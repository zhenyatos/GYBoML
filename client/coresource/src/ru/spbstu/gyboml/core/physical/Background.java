package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.io.InputStream;

import ru.spbstu.gyboml.core.util.PhysicsShapeCache;


public class Background {
    private final Body body;
    private final String PATH = "physics/background.xml";
    public Background(Position pos, World world) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH);

        PhysicsShapeCache physicsBodies = new PhysicsShapeCache(is);
        body = physicsBodies.createBody("bg_land", world, pos.scale, pos.scale);
        body.setTransform(pos.x, pos.y, 0);
        body.setType(BodyDef.BodyType.StaticBody);
    }

    public Vector2 getPosition() { return body.getPosition(); }
}
