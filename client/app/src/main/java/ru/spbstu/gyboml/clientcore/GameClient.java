package main.java.ru.spbstu.gyboml.clientcore;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

// imported from core
import java.util.ArrayList;
import java.util.List;

import main.java.ru.spbstu.gyboml.graphics.Drawable;
import main.java.ru.spbstu.gyboml.graphics.GraphicalBackground;
import main.java.ru.spbstu.gyboml.graphics.GraphicalCannon;
import main.java.ru.spbstu.gyboml.graphics.GraphicalCastle;
import main.java.ru.spbstu.gyboml.graphics.GraphicalForeground;
import main.java.ru.spbstu.gyboml.graphics.GraphicalTower;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.physical.PhysicalBackground;
import ru.spbstu.gyboml.core.physical.PhysicalCastle;
import ru.spbstu.gyboml.core.physical.PhysicalTower;
import ru.spbstu.gyboml.core.physical.Position;
import main.java.ru.spbstu.gyboml.clientnet.Controller;

import main.java.ru.spbstu.gyboml.clientnet.generating.ConnectionGenerator;
import ru.spbstu.gyboml.clientnet.generating.PassTurnGenerator;

/**
 * The GameClient class handles rendering, camera movement,
 * user input and the creation and disposal of graphic resources
 * implements methods that are invoked in the LibGDX game loop.
 * @since   2020-03-11
 */
public class GameClient extends ApplicationAdapter implements InputProcessor {
    private float SCALE;
    private static final float minRatio = 3f / 2f;
    private static final float minWidth = 50;
    private static final float minHeight = minWidth / minRatio;
    private static final float worldScale = 1.5f;
    private static final float worldWidth = minWidth * worldScale;
    private static final float worldHeight = minHeight;
    private static final float maxXRatio = 19.5f / 9f;
    private static final float maxYRatio = 4f / 3f;
    private static final float canvasWidth = worldWidth + minWidth * (maxXRatio / minRatio - 1);
    private static final float canvasHeight = worldHeight + minHeight * (minRatio / maxYRatio - 1);

    // physics
    private static final float gravityAccelerationX = 0f;
    private static final float gravityAccelerationY = -10f;
    private static final float STEP_TIME = 1f / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private float accumulator = 0;
    PhysicalBackground physicalBackground;
    PhysicalCastle physicalCastleP1;
    PhysicalCastle physicalCastleP2;
    PhysicalTower physicalTowerP1;
    PhysicalTower physicalTowerP2;

    private MessageSender toServerMessageSender;
    private SpriteBatch batch;
    private TextureAtlas background1;
    private TextureAtlas background2;
    private TextureAtlas objects;
    private final List<Drawable> drawables = new ArrayList<>();
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private Stage stageForUI;
    private Table table;
    private World world;
    private Box2DDebugRenderer debugRenderer;

    private Controller controller = null;
    private final String serverName = "34.91.65.96";
    private final int serverPort = 4445;


    /**
     * This is the method that is called on client's creation.
     * It loads the graphical resources, and sets up the sprites, background,
     * viewport, camera, UI etc.
     *
     * The method body is a bit bloated at the moment, it will be refactored in the near future.
     */
    @Override
    public void create() {
        stageForUI = new Stage(new ScreenViewport());
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stageForUI);
        inputMultiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(inputMultiplexer);
        setUpUI();

        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(gravityAccelerationX, gravityAccelerationY), true);

        batch = new SpriteBatch();
        background1 = new TextureAtlas("sprites/background_1.txt");
        background2 = new TextureAtlas("sprites/background_2.txt");
        objects = new TextureAtlas("sprites/objects.txt");

        Sprite backgroundSky = background1.createSprite("bg_sky");
        Sprite backgroundDesert = background1.createSprite("bg_desert");
        Sprite backgroundLand = background2.createSprite("bg_land");
        SCALE = canvasWidth / backgroundSky.getWidth();

        float backgroundX = 0 - (canvasWidth - worldWidth) / 2;
        float backgroundY = 0 - (canvasHeight - worldHeight) / 2;
        float castleP1X = backgroundX + 860 * SCALE;
        float castleP1Y = backgroundY + 360 * SCALE;
        float castleP2X = backgroundX + 2634 * SCALE;   // 2634 = 3734 - 860 - 240 (bg resolution - castleP1X indent - castle width)
        float castleP2Y = backgroundY + 360 * SCALE;
        float towerP1X = backgroundX + 450 * SCALE;
        float towerP1Y = backgroundY + 360 * SCALE;
        float towerP2X = backgroundX + 3064 * SCALE;    // 3064 = 3734 - 450 - 220 (bg resolution - towerP1X indent - tower width)
        float towerP2Y = backgroundY + 360 * SCALE;

        physicalBackground = new PhysicalBackground(new Position(backgroundX, backgroundY, SCALE), world);
        physicalCastleP1 = new PhysicalCastle(100, new Position(castleP1X, castleP1Y, SCALE), PlayerType.FIRST_PLAYER, world);
        physicalCastleP2 = new PhysicalCastle(100, new Position(castleP2X, castleP2Y, SCALE), PlayerType.SECOND_PLAYER, world);
        physicalTowerP1 = new PhysicalTower(new Position(towerP1X, towerP1Y, SCALE), PlayerType.FIRST_PLAYER, world);
        physicalTowerP2 = new PhysicalTower(new Position(towerP2X, towerP2Y, SCALE), PlayerType.SECOND_PLAYER, world);

        GraphicalBackground graphicalBackground = new GraphicalBackground(backgroundSky, backgroundDesert, backgroundLand, SCALE);
        graphicalBackground.setSize(canvasWidth, canvasHeight);
        graphicalBackground.setOrigin(0, 0);
        graphicalBackground.setPosition(physicalBackground.getPosition().x, physicalBackground.getPosition().y);
        drawables.add(graphicalBackground);

        GraphicalCannon graphicalCannonP1 = new GraphicalCannon(objects.createSprite("cannon_p1"), SCALE);
        graphicalCannonP1.setOrigin(0, 0);
        graphicalCannonP1.setPosition(physicalTowerP1.getCannonPosition().x, physicalTowerP1.getCannonPosition().y);
        graphicalCannonP1.setRotation(physicalTowerP1.getCannonAngle());
        drawables.add(graphicalCannonP1);
        physicalTowerP1.setMovableSprite(graphicalCannonP1);

        GraphicalCannon graphicalCannonP2 = new GraphicalCannon(objects.createSprite("cannon_p2"), SCALE);
        graphicalCannonP2.setOrigin(0, 0);
        graphicalCannonP2.setPosition(physicalTowerP2.getCannonPosition().x, physicalTowerP2.getCannonPosition().y);
        graphicalCannonP2.setRotation(physicalTowerP2.getCannonAngle());
        drawables.add(graphicalCannonP2);
        physicalTowerP2.setMovableSprite(graphicalCannonP2);

        GraphicalTower graphicalTowerP1 = new GraphicalTower(objects.createSprite("tower_p1"), SCALE);
        graphicalTowerP1.setOrigin(0, 0);
        graphicalTowerP1.setPosition(physicalTowerP1.getTowerPosition().x, physicalTowerP1.getTowerPosition().y);
        drawables.add(graphicalTowerP1);

        GraphicalTower graphicalTowerP2 = new GraphicalTower(objects.createSprite("tower_p2"), SCALE);
        graphicalTowerP2.setOrigin(0, 0);
        graphicalTowerP2.setPosition(physicalTowerP2.getTowerPosition().x, physicalTowerP2.getTowerPosition().y);
        drawables.add(graphicalTowerP2);

        GraphicalCastle graphicalCastleP1 = new GraphicalCastle(objects.createSprite("castle_p1_back"), objects.createSprite("castle_p1_front"), objects.createSprite("castle_p1_tower"), SCALE, 100);
        graphicalCastleP1.setOrigin(0, 0);
        graphicalCastleP1.setPosition(physicalCastleP1.getPosition().x, physicalCastleP1.getPosition().y);
        drawables.add(graphicalCastleP1);

        GraphicalCastle graphicalCastleP2 = new GraphicalCastle(objects.createSprite("castle_p2_back"), objects.createSprite("castle_p2_front"), objects.createSprite("castle_p2_tower"), SCALE, 100);
        graphicalCastleP2.setOrigin(0, 0);
        graphicalCastleP2.setPosition(physicalCastleP2.getPosition().x, physicalCastleP2.getPosition().y);
        drawables.add(graphicalCastleP2);

        GraphicalForeground graphicalForeground = new GraphicalForeground(background2.createSprite("bg_front"), SCALE);
        graphicalForeground.setSize(canvasWidth, canvasHeight);
        graphicalForeground.setOrigin(0, 0);
        graphicalForeground.setPosition(backgroundX, backgroundY);
        drawables.add(graphicalForeground);

        camera = new OrthographicCamera(minWidth, minHeight);
        viewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    
        // create game net controller
        try {
            controller = new Controller(serverName, serverPort);
        } catch (Exception error) {
            System.out.println(error);

        }
        controller.start();

        // establish connection to server
        ConnectionGenerator generator = new ConnectionGenerator();
        generator.generate(null, controller.getServerAddress(), controller.getServerPort(), controller);
    }

    private void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();

        accumulator += Math.min(delta, 0.25f);

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;

            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

            // temp debug stuff
            physicalTowerP1.updateMovableSprite();
            physicalTowerP2.updateMovableSprite();
            if ((physicalTowerP1.getJoint().getJointAngle() >= physicalTowerP1.getJoint().getUpperLimit() && physicalTowerP1.getJoint().getMotorSpeed() > 0)||
                (physicalTowerP1.getJoint().getJointAngle() <= physicalTowerP1.getJoint().getLowerLimit() && physicalTowerP1.getJoint().getMotorSpeed() < 0))
                physicalTowerP1.getJoint().setMotorSpeed(-physicalTowerP1.getJoint().getMotorSpeed());

            if ((physicalTowerP2.getJoint().getJointAngle() >= physicalTowerP2.getJoint().getUpperLimit() && physicalTowerP2.getJoint().getMotorSpeed() > 0)||
                (physicalTowerP2.getJoint().getJointAngle() <= physicalTowerP2.getJoint().getLowerLimit() && physicalTowerP2.getJoint().getMotorSpeed() < 0))
                physicalTowerP2.getJoint().setMotorSpeed(-physicalTowerP2.getJoint().getMotorSpeed());
        }
    }

    /**
     * This is the main method that is called repeatedly in the game loop.
     * Renders all objects on the screen.
     */
    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stepWorld();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Drawable object : drawables) {
            object.draw(batch);
        }
        batch.end();

        stageForUI.act(Gdx.graphics.getDeltaTime());
        stageForUI.draw();

        debugRenderer.render(world, camera.combined);
    }

    /**
     * This method is called upon the game loop's completion. Disposes of the graphical resources
     * that were set up in the create() method.
     */
    @Override
    public void dispose() {
        batch.dispose();
        background1.dispose();
        background2.dispose();
        objects.dispose();
        stageForUI.dispose();
        controller.interrupt();
    }

    /** Called when a finger or the mouse was dragged.
     * Moves the camera in correspondence with the finger's movement.
     * @param screenX the horizontal position of the finger in screen coordinates.
     * @param screenY the vertical position of the finger in screen coordinates
     * @param pointer the pointer for the event.
     * @return whether the input was processed.
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float x = Gdx.input.getDeltaX() * SCALE;
        float y = Gdx.input.getDeltaY() * SCALE;

        float leftEdgePos = camera.position.x - minWidth / 2;
        float rightEdgePos = leftEdgePos + minWidth;
        if (leftEdgePos  - x < 0)
            x = leftEdgePos;
        else if (rightEdgePos - x > worldWidth)
            x = rightEdgePos - worldWidth;

        float topEdgePos = camera.position.y + minHeight / 2;
        float bottomEdgePos = topEdgePos - minHeight;
        if (topEdgePos + y > worldHeight)
            y = worldHeight - topEdgePos;
        else if (bottomEdgePos + y < 0)
            y = -bottomEdgePos;

        camera.position.add(-x, y, 0);
        camera.update();

        return true;
    }

    /** Called when the application is resized. Updates the viewport.
     * @param width the new width in pixels.
     * @param height the new height in pixels.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        stageForUI.getViewport().update(width, height, false);
    }

    /** This function sets up the UI. The name speaks for itself, really.
     * Creates the UI table and creates the layout of the UI elements.
     */
    private void setUpUI() {
        table = new Table();
        table.setFillParent(true);
        stageForUI.addActor(table);

        //table.setDebug(true);
        Skin skin = new Skin(Gdx.files.internal("skin/flat-earth-ui.json"));
        Button endTurnButton = new TextButton("End Turn", skin, "default");
        endTurnButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                toServerMessageSender.nextTurnMessage();
                PassTurnGenerator generator = new PassTurnGenerator();
                generator.generate(null, controller.getServerAddress(), controller.getServerPort(), controller);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        table.add(endTurnButton).width(200).height(100);
        table.right().bottom();
    }

    public boolean keyDown(int keycode) {return true;}

    public boolean keyUp(int keycode) {return true;}

    public boolean keyTyped(char character) {return true;}

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {return true;}

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {return true;}

    public boolean mouseMoved(int screenX, int screenY) {return true;}

    public boolean scrolled(int amount) { return true;}
}
