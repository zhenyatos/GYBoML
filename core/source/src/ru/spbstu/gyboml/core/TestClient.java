package ru.spbstu.gyboml.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import ru.spbstu.gyboml.core.physical.PhysicalBackground;
import ru.spbstu.gyboml.core.physical.PhysicalTower;
import ru.spbstu.gyboml.core.physical.Position;


public class TestClient extends ApplicationAdapter {
    private static final float SCALE = 1f / 20f;
    private static final float minRatio = 3f / 2f;
    private static final float minWidth = 50;
    private static final float minHeight = minWidth / minRatio;
    private static final float worldScale = 1.5f;
    private static final float worldWidth = minWidth * worldScale;
    private static final float worldHeight = minHeight;
    private static final float maxXRatio = 19.5f / 9f;
    private static final float maxYRatio = 4f / 3f;
    private static final float STEP_TIME = 1f / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private float accumulator = 0;

    private World world;
    private Body ground;

    private OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;
    private ExtendViewport viewport;
    private PhysicalBackground physicalBackground;
    private Body test;

    @Override
    public void create () {
        camera = new OrthographicCamera(minWidth, minHeight);
        viewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();


        Box2D.init();
        world = new World(new Vector2(0, -10), true);
        createTestedObjects();

        debugRenderer = new Box2DDebugRenderer();

    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0.57f, 0.77f, 0.85f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stepWorld();
        debugRenderer.render(world, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void dispose () {
        world.dispose();
        debugRenderer.dispose();
    }

    private void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();

        accumulator += Math.min(delta, 0.25f);

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;

            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }

    private void createTestedObjects() {
        float width = worldWidth + minWidth * (maxXRatio / minRatio - 1);
        float height = worldHeight + minHeight * (minRatio / maxYRatio - 1);

        PhysicalTower tower = new PhysicalTower(new Position(15.f, 0.f, SCALE/2),
                PlayerType.FIRST_PLAYER, world);

    }

    private Body createBox(float hx, float hy, float x, float y, float angle) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hx, hy);

        fixtureDef.shape = shape;
        fixtureDef.friction = 1;

        Body box = world.createBody(bodyDef);
        box.createFixture(fixtureDef);
        box.setTransform(x, y, angle);

        shape.dispose();

        return box;
    }

    private void createGround() {
        if (ground != null)
            world.destroyBody(ground);
        ground = createBox(worldWidth, 1, 0, 0, 0);
    }
}
