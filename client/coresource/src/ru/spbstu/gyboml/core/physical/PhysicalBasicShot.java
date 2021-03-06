package ru.spbstu.gyboml.core.physical;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.io.InputStream;

import ru.spbstu.gyboml.core.damage.Damage;
import ru.spbstu.gyboml.core.destructible.Destructible;
import ru.spbstu.gyboml.core.shot.ShotType;
import ru.spbstu.gyboml.core.util.PhysicsShapeCache;

public class PhysicalBasicShot extends PhysicalShot {
    private static final float BASE_DAMAGE = 100;
    private static final float SPEED_PROP_LIMIT = 500.f;

    public PhysicalBasicShot(Location location, World world) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PHYSICS_PATH_OBJECTS);
        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache(is);
        shotType = ShotType.BASIC;
        body = physicsShapeCache.createBody("shot_" + shotType.getName(), world, location.scale, location.scale);
        body.setTransform(location.x, location.y, location.angle);
        body.setType(BodyDef.BodyType.DynamicBody);
        body.setUserData(this);
    }

    @Override
    public Damage generateDamage(Destructible destructible) {
        if (getVelocity().isZero(COLLISION_MARGIN))
            return new Damage(0.f);
        return new Damage(speedProportion(BASE_DAMAGE *
                (1 - destructible.material.getDefenceRatio())));
    }

    private float speedProportion(float damage) {
        return damage * Math.min(getVelocity().len2(), SPEED_PROP_LIMIT) / SPEED_PROP_LIMIT;
    }
}
