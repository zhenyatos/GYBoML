package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.io.InputStream;

import ru.spbstu.gyboml.core.destructible.Destructible;
import ru.spbstu.gyboml.core.destructible.Material;
import ru.spbstu.gyboml.core.graphics.Updatable;
import ru.spbstu.gyboml.core.util.PhysicsShapeCache;

public class PhysicalBlock extends Destructible implements Physical, Movable, Interactable {
    private static final float BASE_HP = 100;
    private Body body;
    private Updatable sprite;

    private int id;
        public void setId(int id) { this.id = id; }
        public int getId() { return id; }

    public PhysicalBlock(Material material, Location location, World world) {
        super(BASE_HP, material);
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PHYSICS_PATH_OBJECTS);

        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(is);
        body = physicsShapeCache.createBody("block_" + material.getName(), world, location.scale, location.scale);
        body.setTransform(location.x, location.y, location.angle);
        body.setType(BodyDef.BodyType.DynamicBody);
        body.setUserData(this);
    }

    @Override
    public Vector2 getPosition() { return body.getPosition(); }

    @Override
    public Vector2 getMovablePartPosition() { return body.getPosition(); }

    @Override
    public float getMovablePartAngle() { return body.getAngle(); }

    @Override
    public void setUpdatableSprite(Updatable sprite) {
        this.sprite = sprite;
    }

    @Override
    public void updateSprite() {
        if (this.sprite != null) {
            sprite.setUpdatablePartPosition(body.getPosition());
            sprite.setUpdatablePartAngle((float) Math.toDegrees(body.getAngle()));

            if (this.getHP() < initialHP / 2)
                sprite.changeSprite();
        }
    }

    @Override
    public Type getType() {
        return Type.BLOCK;
    }

    public Body getBody() {
        return body;
    }
}
