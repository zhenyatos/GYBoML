package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import org.jetbrains.annotations.NotNull;

import ru.spbstu.gyboml.core.damage.Damage;
import ru.spbstu.gyboml.core.destructible.Destructible;
import ru.spbstu.gyboml.core.destructible.Material;
import ru.spbstu.gyboml.core.util.PhysicsShapeCache;

import java.io.InputStream;

import ru.spbstu.gyboml.core.PlayerType;

public class PhysicalCastle extends Destructible implements Physical, Interactable {
    private Body front;
    private Body tower;
    private PlayerType playerType;

    public PhysicalCastle(float HP, Location location, PlayerType playerType, World world) {
        super(HP, Material.STONE);
        this.playerType = playerType;

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PHYSICS_PATH_OBJECTS);

        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(is);
        String playerName = (playerType == PlayerType.FIRST_PLAYER) ? "_p1_" : "_p2_";
        front = physicsShapeCache.createBody("castle" + playerName + "front", world, location.scale, location.scale);
        tower = physicsShapeCache.createBody("castle" + playerName + "tower", world, location.scale, location.scale);

        front.setTransform(location.x, location.y, location.angle);
        tower.setTransform(location.x, location.y, location.angle);

        // Castle doesn't move => static components
        front.setType(BodyDef.BodyType.StaticBody);
        tower.setType(BodyDef.BodyType.StaticBody);

        front.setUserData(this);
        tower.setUserData(this);
    }

    Body getFront() {
        return front;
    }

    public PlayerType getPlayerType() { return playerType; }

    @Override
    public Vector2 getPosition() { return front.getPosition(); }

    @Override
    public void handleDamage(@NotNull Damage damage) {
        super.handleDamage(damage);
    }

    @Override
    public Type getType() {
        return Type.CASTLE;
    }
}
