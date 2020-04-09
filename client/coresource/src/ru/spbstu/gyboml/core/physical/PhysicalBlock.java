package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.io.InputStream;

import ru.spbstu.gyboml.core.destructible.Block;
import ru.spbstu.gyboml.core.destructible.Material;
import ru.spbstu.gyboml.core.util.PhysicsShapeCache;

public class PhysicalBlock extends Block {
    private final String PATH = "physics/objects.xml";
    private Body body;
    private Movable sprite;

    public PhysicalBlock(Material material, Position pos, World world) {
        super(material);
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH);

        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(is);
        body = physicsShapeCache.createBody("block_wood", world, pos.scale, pos.scale);

        // IMPORTANT: fixtures now set in .xml manually

        // FixtureDef fixtureDef   = new FixtureDef();
        // fixtureDef.density      = 600;      // oak density 0.6 * 10^3 kg/m^3
        // fixtureDef.friction     = 0.5f;     // friction coefficient [0, 1]
        // fixtureDef.restitution  = 0.1f;     // elasticity coefficient [0, 1]
        // body.createFixture(fixtureDef);

        body.setTransform(pos.x, pos.y, 0);
        body.setType(BodyDef.BodyType.DynamicBody);
    }

    public Vector2 getPosition() { return body.getPosition(); }

    public void setMovableSprite(Movable sprite) { this.sprite = sprite; }

    public void updateMovableSprite() {
        if (this.sprite != null) {
            sprite.setMovablePartPosition(body.getPosition());
            sprite.setMovablePartAngle((float) Math.toDegrees(body.getAngle()));
        }
    }
}
