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

import main.java.ru.spbstu.gyboml.clientnet.Controller;

import main.java.ru.spbstu.gyboml.clientnet.generating.ConnectionGenerator;
import main.java.ru.spbstu.gyboml.clientnet.generating.PassTurnGenerator;
// imported from core
import java.util.ArrayList;
import java.util.List;

import main.java.ru.spbstu.gyboml.graphics.Drawable;
import main.java.ru.spbstu.gyboml.graphics.GraphicalBackground;
import main.java.ru.spbstu.gyboml.graphics.GraphicalBlock;
import main.java.ru.spbstu.gyboml.graphics.GraphicalCannon;
import main.java.ru.spbstu.gyboml.graphics.GraphicalCastle;
import main.java.ru.spbstu.gyboml.graphics.GraphicalForeground;
import main.java.ru.spbstu.gyboml.graphics.GraphicalTower;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.destructible.Material;
import ru.spbstu.gyboml.core.physical.Background;
import ru.spbstu.gyboml.core.physical.Block;
import ru.spbstu.gyboml.core.physical.Castle;
import ru.spbstu.gyboml.core.physical.Tower;
import ru.spbstu.gyboml.core.physical.Position;

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

    // TODO: wrap in single container
    Background background;
    Castle castleP1;
    Castle castleP2;
    Tower towerP1;
    Tower towerP2;
    ArrayList<Block> blocksP1;
    ArrayList<Block> blocksP2;

    private MessageSender toServerMessageSender;
    private SpriteBatch batch;
    private TextureAtlas backgroundBack;
    private TextureAtlas backgroundFront;
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

        buildScene();

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


    /**
     * Called during client creation.
     * Builds physical and displayed scene for client application.
     */
    private void buildScene() {
        backgroundBack  = new TextureAtlas("sprites/background_1.txt");
        backgroundFront = new TextureAtlas("sprites/background_2.txt");
        objects         = new TextureAtlas("sprites/objects.txt");

        Sprite backgroundSky    = backgroundBack.createSprite("bg_sky");
        Sprite backgroundDesert = backgroundBack.createSprite("bg_desert");
        Sprite backgroundLand   = backgroundFront.createSprite("bg_land");

        final float RESOLUTION_W = backgroundBack.findRegion("bg_sky").originalWidth;
        SCALE = canvasWidth / RESOLUTION_W;

        // all values are calculated manually for same placement for both players
        final float castleIndentX   = 860;  // manually set value for castle placement
        final float towerIndentX    = 450;  // manually set value for tower  placement
        final float platformIndentY = 364;  // value from background.xml file - position of platform for objects
        final float castleTextureWidth = objects.findRegion("castle_p1_front").originalWidth;
        final float towerTextureWidth  = objects.findRegion("tower_p1").originalWidth;
        final float blockTextureWidth  = objects.findRegion("block_wood").originalWidth;

        float backgroundX = 0 - (canvasWidth - worldWidth) / 2;
        float backgroundY = 0 - (canvasHeight - worldHeight) / 2;

        float castleP1X = backgroundX + castleIndentX * SCALE;
        float castleP2X = backgroundX + (RESOLUTION_W - castleIndentX - castleTextureWidth) * SCALE;
        float castleP1Y = backgroundY + platformIndentY * SCALE;
        float castleP2Y = backgroundY + platformIndentY * SCALE;
        float towerP1X = backgroundX + towerIndentX * SCALE;
        float towerP2X = backgroundX + (RESOLUTION_W - towerIndentX - towerTextureWidth) * SCALE;
        float towerP1Y = backgroundY + platformIndentY * SCALE;
        float towerP2Y = backgroundY + platformIndentY * SCALE;

        final float BLOCKS_SCALE = SCALE * 0.35f;
        float blockP1X = castleP1X + (castleTextureWidth + 60) * SCALE;
        float blockP1Y = castleP1Y + 240 * SCALE;
        float blockP2X = castleP2X - 60 * SCALE - blockTextureWidth * BLOCKS_SCALE;
        float blockP2Y = castleP2Y + 240 * SCALE;

        background = new Background(new Position(backgroundX, backgroundY, SCALE), world);
        castleP1 = new Castle(100, new Position(castleP1X, castleP1Y, SCALE), PlayerType.FIRST_PLAYER, world);
        castleP2 = new Castle(100, new Position(castleP2X, castleP2Y, SCALE), PlayerType.SECOND_PLAYER, world);
        towerP1 = new Tower(new Position(towerP1X, towerP1Y, SCALE), PlayerType.FIRST_PLAYER, world);
        towerP2 = new Tower(new Position(towerP2X, towerP2Y, SCALE), PlayerType.SECOND_PLAYER, world);
        blocksP1 = new ArrayList<>();
        blocksP2 = new ArrayList<>();
        blocksP1.add(new Block(Material.WOOD, new Position(blockP1X, blockP1Y, BLOCKS_SCALE ), world));
        blocksP2.add(new Block(Material.WOOD, new Position(blockP2X, blockP2Y, BLOCKS_SCALE ), world));

        GraphicalBackground graphicalBackground = new GraphicalBackground(backgroundSky, backgroundDesert, backgroundLand, SCALE);
        graphicalBackground.setSize(canvasWidth, canvasHeight);
        graphicalBackground.setOrigin(0, 0);
        graphicalBackground.setPosition(background.getPosition().x, background.getPosition().y);
        drawables.add(graphicalBackground);

        GraphicalCannon graphicalCannonP1 = new GraphicalCannon(objects.createSprite("cannon_p1"), SCALE);
        graphicalCannonP1.setOrigin(0, 0);
        graphicalCannonP1.setPosition(towerP1.getUpdatablePosition().x, towerP1.getUpdatablePosition().y);
        graphicalCannonP1.setRotation(towerP1.getUpdatableAngle());
        drawables.add(graphicalCannonP1);
        towerP1.setMovableSprite(graphicalCannonP1);

        GraphicalCannon graphicalCannonP2 = new GraphicalCannon(objects.createSprite("cannon_p2"), SCALE);
        graphicalCannonP2.setOrigin(0, 0);
        graphicalCannonP2.setPosition(towerP2.getUpdatablePosition().x, towerP2.getUpdatablePosition().y);
        graphicalCannonP2.setRotation(towerP2.getUpdatableAngle());
        drawables.add(graphicalCannonP2);
        towerP2.setMovableSprite(graphicalCannonP2);

        GraphicalTower graphicalTowerP1 = new GraphicalTower(objects.createSprite("tower_p1"), SCALE);
        graphicalTowerP1.setOrigin(0, 0);
        graphicalTowerP1.setPosition(towerP1.getPosition().x, towerP1.getPosition().y);
        drawables.add(graphicalTowerP1);

        GraphicalTower graphicalTowerP2 = new GraphicalTower(objects.createSprite("tower_p2"), SCALE);
        graphicalTowerP2.setOrigin(0, 0);
        graphicalTowerP2.setPosition(towerP2.getPosition().x, towerP2.getPosition().y);
        drawables.add(graphicalTowerP2);

        GraphicalCastle graphicalCastleP1 = new GraphicalCastle(objects.createSprite("castle_p1_back"), objects.createSprite("castle_p1_front"), objects.createSprite("castle_p1_tower"), SCALE, 100);
        graphicalCastleP1.setOrigin(0, 0);
        graphicalCastleP1.setPosition(castleP1.getPosition().x, castleP1.getPosition().y);
        drawables.add(graphicalCastleP1);

        GraphicalCastle graphicalCastleP2 = new GraphicalCastle(objects.createSprite("castle_p2_back"), objects.createSprite("castle_p2_front"), objects.createSprite("castle_p2_tower"), SCALE, 100);
        graphicalCastleP2.setOrigin(0, 0);
        graphicalCastleP2.setPosition(castleP2.getPosition().x, castleP2.getPosition().y);
        drawables.add(graphicalCastleP2);

        GraphicalBlock graphicalBlockP1 = new GraphicalBlock(objects.createSprite("block_wood"), objects.createSprite("block_wood_damaged"), BLOCKS_SCALE);
        graphicalBlockP1.setOrigin(0,0);
        graphicalBlockP1.setPosition(blocksP1.get(0).getPosition().x, blocksP1.get(0).getPosition().y);
        drawables.add(graphicalBlockP1);
        blocksP1.get(0).setMovableSprite(graphicalBlockP1);

        GraphicalBlock graphicalBlockP2 = new GraphicalBlock(objects.createSprite("block_wood"), objects.createSprite("block_wood_damaged"), BLOCKS_SCALE);
        graphicalBlockP2.setOrigin(0,0);
        graphicalBlockP2.setPosition(blocksP2.get(0).getPosition().x, blocksP2.get(0).getPosition().y);
        drawables.add(graphicalBlockP2);
        blocksP2.get(0).setMovableSprite(graphicalBlockP2);

        GraphicalForeground graphicalForeground = new GraphicalForeground(backgroundFront.createSprite("bg_front"), SCALE);
        graphicalForeground.setSize(canvasWidth, canvasHeight);
        graphicalForeground.setOrigin(0, 0);
        graphicalForeground.setPosition(backgroundX, backgroundY);
        drawables.add(graphicalForeground);
    }

    /**
     * This method is implemented for demo. Called within buildScene() method.
     * Fills physicalBlocksP1 and physicalBlocksP2 array lists with
     * several blocks placed by default same for both players.
     */
    private void placeDefaultBlocks() {

    }

    private void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();

        accumulator += Math.min(delta, 0.25f);

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;

            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

            // temp debug stuff
            // TODO: wrap updateMovableSprite() method
            towerP1.updateMovableSprite();
            towerP2.updateMovableSprite();
            for (Block block : blocksP1)
                block.updateMovableSprite();
            for (Block block : blocksP2)
                block.updateMovableSprite();

            if ((towerP1.getJoint().getJointAngle() >= towerP1.getJoint().getUpperLimit() && towerP1.getJoint().getMotorSpeed() > 0)||
                (towerP1.getJoint().getJointAngle() <= towerP1.getJoint().getLowerLimit() && towerP1.getJoint().getMotorSpeed() < 0))
                towerP1.getJoint().setMotorSpeed(-towerP1.getJoint().getMotorSpeed());

            if ((towerP2.getJoint().getJointAngle() >= towerP2.getJoint().getUpperLimit() && towerP2.getJoint().getMotorSpeed() > 0)||
                (towerP2.getJoint().getJointAngle() <= towerP2.getJoint().getLowerLimit() && towerP2.getJoint().getMotorSpeed() < 0))
                towerP2.getJoint().setMotorSpeed(-towerP2.getJoint().getMotorSpeed());
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
        backgroundBack.dispose();
        backgroundFront.dispose();
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
                //toServerMessageSender.nextTurnMessage();
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
