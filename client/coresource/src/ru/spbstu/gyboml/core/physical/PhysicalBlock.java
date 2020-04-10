package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.io.InputStream;

import ru.spbstu.gyboml.core.destructible.Destructible;
import ru.spbstu.gyboml.core.destructible.Material;
import ru.spbstu.gyboml.core.util.PhysicsShapeCache;

public class PhysicalBlock extends Destructible implements Physical, Updatable {
    private static final int BASE_HP = 100;

    private Body body;
    private Movable sprite;

    public PhysicalBlock(Material material, Position pos, World world) {
        super((int)(BASE_HP * material.getDefenceRatio()), material);
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PHYSICS_PATH_OBJECTS);

        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(is);
        body = physicsShapeCache.createBody("block_wood", world, pos.scale, pos.scale);
        body.setTransform(pos.x, pos.y, 0);
        body.setType(BodyDef.BodyType.DynamicBody);
    }

    @Override
    public Vector2 getPosition() { return body.getPosition(); }

    @Override
    public Vector2 getUpdatablePosition() { return body.getPosition(); }

    @Override
    public float getUpdatableAngle() { return body.getAngle(); }

    @Override
    public void setMovableSprite(Movable sprite) {
        this.sprite = sprite;
    }

    @Override
    public void updateMovableSprite() {
        if (this.sprite != null) {
            sprite.setMovablePartPosition(body.getPosition());
            sprite.setMovablePartAngle((float) Math.toDegrees(body.getAngle()));
        }
    }
}