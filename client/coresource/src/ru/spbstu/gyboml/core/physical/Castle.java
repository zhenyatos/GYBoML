package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import ru.spbstu.gyboml.core.destructible.Destructible;
import ru.spbstu.gyboml.core.destructible.Material;
import ru.spbstu.gyboml.core.util.PhysicsShapeCache;

import java.io.InputStream;

import ru.spbstu.gyboml.core.PlayerType;

public class Castle extends Destructible implements Physical {
    private Body front;
    private Body tower;

    public Castle(int HP, Position pos, PlayerType playerType, World world) {
        super(HP, Material.STONE);
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PHYSICS_PATH_OBJECTS);

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

    @Override
    public Vector2 getPosition() { return front.getPosition(); }
}
