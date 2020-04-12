package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.io.InputStream;

import ru.spbstu.gyboml.core.util.PhysicsShapeCache;


public class PhysicalBackground implements Physical, Interactable {
    private final Body body;

    public PhysicalBackground(Location location, World world) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PHYSICS_PATH_BACKGROUND);

        PhysicsShapeCache physicsBodies = new PhysicsShapeCache(is);
        body = physicsBodies.createBody("bg_land", world, location.scale, location.scale);
        body.setTransform(location.x, location.y, location.angle);
        body.setType(BodyDef.BodyType.StaticBody);
        body.setUserData(this);
    }

    @Override
    public Vector2 getPosition() { return body.getPosition(); }

    @Override
    public Type getType() {
        return Type.BACKGROUND;
    }
}
