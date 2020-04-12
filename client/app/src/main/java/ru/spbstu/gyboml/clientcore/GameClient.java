package main.java.ru.spbstu.gyboml.clientcore;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import java.util.HashMap;
import java.util.List;

import main.java.ru.spbstu.gyboml.graphics.Drawable;
import main.java.ru.spbstu.gyboml.graphics.GraphicalBackground;
import main.java.ru.spbstu.gyboml.graphics.GraphicalBasicShot;
import main.java.ru.spbstu.gyboml.graphics.GraphicalBlock;
import main.java.ru.spbstu.gyboml.graphics.GraphicalCannon;
import main.java.ru.spbstu.gyboml.graphics.GraphicalCastle;
import main.java.ru.spbstu.gyboml.graphics.GraphicalForeground;
import main.java.ru.spbstu.gyboml.graphics.GraphicalTower;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.destructible.Material;
import ru.spbstu.gyboml.core.physical.CollisionHandler;
import ru.spbstu.gyboml.core.physical.PhysicalBackground;
import ru.spbstu.gyboml.core.physical.PhysicalBasicShot;
import ru.spbstu.gyboml.core.physical.PhysicalBlock;
import ru.spbstu.gyboml.core.physical.PhysicalCastle;
import ru.spbstu.gyboml.core.physical.PhysicalTower;
import ru.spbstu.gyboml.core.physical.Location;
import ru.spbstu.gyboml.core.physical.Movable;

/**
 * The GameClient class handles rendering, camera movement,
 * user input and the creation and disposal of graphic resources
 * implements methods that are invoked in the LibGDX game loop.
 * @since   2020-03-11
 */
public class GameClient extends ApplicationAdapter implements InputProcessor {
    // canvas / world constants
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

    // scene physics
    private final float backgroundX = 0 - (canvasWidth - worldWidth) / 2;
    private final float backgroundY = 0 - (canvasHeight - worldHeight) / 2;
    private float SCALE;
    private float BLOCKS_SCALE;
    private float SHOTS_SCALE;
    private List<Movable> movables;
    private PhysicalBackground physicalBackground;
    private PhysicalCastle physicalCastleP1;
    private PhysicalCastle physicalCastleP2;
    private PhysicalTower physicalTowerP1;
    private PhysicalTower physicalTowerP2;
    private ArrayList<PhysicalBlock> physicalBlocksP1;
    private ArrayList<PhysicalBlock> physicalBlocksP2;
    private HashMap<PhysicalBlock, GraphicalBlock> blocks;

    // scene graphics
    private List<Drawable> drawables;
    private TextureAtlas backgroundBack;
    private TextureAtlas backgroundFront;
    private TextureAtlas objects;

    // drawing and stuff
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private Stage stageForUI;
    private Table table;
    private World world;

    // connection
    private Controller controller = null;
    private final String serverName = "34.91.65.96";
    private final int serverPort = 4445;
    private MessageSender toServerMessageSender;

    // temp
    PlayerType playerTurn = PlayerType.FIRST_PLAYER;

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
        world.setContactListener(new CollisionHandler());
        batch = new SpriteBatch();

        backgroundBack  = new TextureAtlas("sprites/background_1.txt");
        backgroundFront = new TextureAtlas("sprites/background_2.txt");
        objects         = new TextureAtlas("sprites/objects.txt");

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
        final float RESOLUTION_W = backgroundBack.findRegion("bg_sky").originalWidth;
        SCALE = canvasWidth / RESOLUTION_W;
        BLOCKS_SCALE = SCALE * 0.35f;
        SHOTS_SCALE  = SCALE * 0.22f;

        movables = new ArrayList<>();
        drawables = new ArrayList<>();

        final float castleIndentX   = 860;  // manually set value for castle placement on platform
        final float towerIndentX    = 450;  // manually set value for tower  placement on platform
        final float platformIndentY = 364;  // y position of platforms for objects from background.xml
        final float castleTextureWidth = objects.findRegion("castle_p1_front").originalWidth;
        final float towerTextureWidth  = objects.findRegion("tower_p1").originalWidth;

        float castleP1X = backgroundX + castleIndentX * SCALE;
        float castleP2X = backgroundX + (RESOLUTION_W - castleIndentX - castleTextureWidth) * SCALE;
        float castleP1Y = backgroundY + platformIndentY * SCALE;
        float castleP2Y = backgroundY + platformIndentY * SCALE;
        float towerP1X = backgroundX + towerIndentX * SCALE;
        float towerP2X = backgroundX + (RESOLUTION_W - towerIndentX - towerTextureWidth) * SCALE;
        float towerP1Y = backgroundY + platformIndentY * SCALE;
        float towerP2Y = backgroundY + platformIndentY * SCALE;

        // physics
        physicalBackground = new PhysicalBackground(new Location(backgroundX, backgroundY,0, SCALE), world);
        physicalCastleP1 = new PhysicalCastle(100, new Location(castleP1X, castleP1Y, 0, SCALE), PlayerType.FIRST_PLAYER, world);
        physicalCastleP2 = new PhysicalCastle(100, new Location(castleP2X, castleP2Y, 0, SCALE), PlayerType.SECOND_PLAYER, world);
        physicalTowerP1 = new PhysicalTower(new Location(towerP1X, towerP1Y, 0, SCALE), PlayerType.FIRST_PLAYER, world);
        physicalTowerP2 = new PhysicalTower(new Location(towerP2X, towerP2Y, 0, SCALE), PlayerType.SECOND_PLAYER, world);
        movables.add(physicalTowerP1);
        movables.add(physicalTowerP2);
        placeDefaultBlocks(castleP1X, castleP1Y, castleP2X, castleP2Y);

        // graphics
        GraphicalBackground graphicalBackground = new GraphicalBackground(backgroundBack.createSprite("bg_sky"),
                                                                          backgroundBack.createSprite("bg_desert"),
                                                                          backgroundFront.createSprite("bg_land"),
                                                                          SCALE);
        graphicalBackground.setSize(canvasWidth, canvasHeight);
        graphicalBackground.setOrigin(0, 0);
        graphicalBackground.setPosition(physicalBackground.getPosition().x, physicalBackground.getPosition().y);
        drawables.add(graphicalBackground);

        GraphicalCannon graphicalCannonP1 = new GraphicalCannon(objects.createSprite("cannon_p1"), SCALE);
        graphicalCannonP1.setOrigin(0, 0);
        graphicalCannonP1.setPosition(physicalTowerP1.getMovablePartPosition().x, physicalTowerP1.getMovablePartPosition().y);
        graphicalCannonP1.setRotation(physicalTowerP1.getMovablePartAngle());
        drawables.add(graphicalCannonP1);
        physicalTowerP1.setUpdatableSprite(graphicalCannonP1);

        GraphicalCannon graphicalCannonP2 = new GraphicalCannon(objects.createSprite("cannon_p2"), SCALE);
        graphicalCannonP2.setOrigin(0, 0);
        graphicalCannonP2.setPosition(physicalTowerP2.getMovablePartPosition().x, physicalTowerP2.getMovablePartPosition().y);
        graphicalCannonP2.setRotation(physicalTowerP2.getMovablePartAngle());
        drawables.add(graphicalCannonP2);
        physicalTowerP2.setUpdatableSprite(graphicalCannonP2);

        GraphicalTower graphicalTowerP1 = new GraphicalTower(objects.createSprite("tower_p1"), SCALE);
        graphicalTowerP1.setOrigin(0, 0);
        graphicalTowerP1.setPosition(physicalTowerP1.getPosition().x, physicalTowerP1.getPosition().y);
        drawables.add(graphicalTowerP1);

        GraphicalTower graphicalTowerP2 = new GraphicalTower(objects.createSprite("tower_p2"), SCALE);
        graphicalTowerP2.setOrigin(0, 0);
        graphicalTowerP2.setPosition(physicalTowerP2.getPosition().x, physicalTowerP2.getPosition().y);
        drawables.add(graphicalTowerP2);

        GraphicalCastle graphicalCastleP1 = new GraphicalCastle(objects.createSprite("castle_p1_back"), objects.createSprite("castle_p1_front"), objects.createSprite("castle_p1_tower"), SCALE, 100);
        graphicalCastleP1.setOrigin(0, 0);
        graphicalCastleP1.setPosition(physicalCastleP1.getPosition().x, physicalCastleP1.getPosition().y);
        drawables.add(graphicalCastleP1);

        GraphicalCastle graphicalCastleP2 = new GraphicalCastle(objects.createSprite("castle_p2_back"), objects.createSprite("castle_p2_front"), objects.createSprite("castle_p2_tower"), SCALE, 100);
        graphicalCastleP2.setOrigin(0, 0);
        graphicalCastleP2.setPosition(physicalCastleP2.getPosition().x, physicalCastleP2.getPosition().y);
        drawables.add(graphicalCastleP2);

        bindDefaultBlocksGraphics();

        GraphicalForeground graphicalForeground = new GraphicalForeground(backgroundFront.createSprite("bg_front"), SCALE);
        graphicalForeground.setSize(canvasWidth, canvasHeight);
        graphicalForeground.setOrigin(0, 0);
        graphicalForeground.setPosition(physicalBackground.getPosition().x, physicalBackground.getPosition().y);
        drawables.add(graphicalForeground);
    }

    /**
     * This method is implemented for demo. Called within buildScene() method.
     * Fills physicalBlocksP1 and physicalBlocksP2 array lists with
     * several blocks placed by default same for both players.
     * Blocks placed manually in general.
     */
    private void placeDefaultBlocks(float castleP1X, float castleP1Y, float castleP2X, float castleP2Y) {
        final float blockTextureWidth  = objects.findRegion("block_wood").originalWidth;
        final float blockTextureHeight = objects.findRegion("block_wood").originalHeight;
        final float castleTextureWidth = objects.findRegion("castle_p1_front").originalWidth;

        float blockP1X = castleP1X + (castleTextureWidth + 60) * SCALE;
        float blockP1Y = castleP1Y + 240 * SCALE;
        float blockP2X = castleP2X -  60 * SCALE - blockTextureWidth * BLOCKS_SCALE;
        float blockP2Y = castleP2Y + 240 * SCALE;

        physicalBlocksP1 = new ArrayList<>();
        physicalBlocksP2 = new ArrayList<>();

        // 1st row
        physicalBlocksP1.add(new PhysicalBlock(Material.WOOD, new Location(blockP1X, blockP1Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(Material.WOOD, new Location(blockP2X, blockP2Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP1.add(new PhysicalBlock(Material.WOOD, new Location(blockP1X, blockP1Y + 1.2f * blockTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(Material.WOOD, new Location(blockP2X, blockP2Y + 1.2f * blockTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));
        physicalBlocksP1.add(new PhysicalBlock(Material.WOOD, new Location(blockP1X, blockP1Y + 2 * 1.2f * blockTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(Material.WOOD, new Location(blockP2X, blockP2Y + 2 * 1.2f * blockTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));

        // 2nd row
        physicalBlocksP1.add(new PhysicalBlock(Material.WOOD, new Location(blockP1X + 2 * blockTextureWidth * BLOCKS_SCALE, blockP1Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(Material.WOOD, new Location(blockP2X - 2 * blockTextureWidth * BLOCKS_SCALE, blockP2Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP1.add(new PhysicalBlock(Material.WOOD, new Location(blockP1X + 2 * blockTextureWidth * BLOCKS_SCALE, blockP1Y + 1.2f * blockTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(Material.WOOD, new Location(blockP2X - 2 * blockTextureWidth * BLOCKS_SCALE, blockP2Y + 1.2f * blockTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));

        // 3rd row
        physicalBlocksP1.add(new PhysicalBlock(Material.WOOD, new Location(blockP1X + 4 * blockTextureWidth * BLOCKS_SCALE, blockP1Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(Material.WOOD, new Location(blockP2X - 4 * blockTextureWidth * BLOCKS_SCALE, blockP2Y, 0 ,BLOCKS_SCALE), world));

        // back row
        physicalBlocksP1.add(new PhysicalBlock(Material.WOOD, new Location(castleP1X - 60 * SCALE - blockTextureWidth * BLOCKS_SCALE, blockP1Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP1.add(new PhysicalBlock(Material.WOOD, new Location(castleP1X - 60 * SCALE - blockTextureWidth * BLOCKS_SCALE, blockP1Y + 1.2f * blockTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(Material.WOOD, new Location(castleP2X + (castleTextureWidth + 60) * SCALE, blockP2Y, 0, BLOCKS_SCALE), world));
        physicalBlocksP2.add(new PhysicalBlock(Material.WOOD, new Location(castleP2X + (castleTextureWidth + 60) * SCALE, blockP2Y + 1.2f * blockTextureHeight * BLOCKS_SCALE, 0, BLOCKS_SCALE), world));

        movables.addAll(physicalBlocksP1);
        movables.addAll(physicalBlocksP2);
    }

    /**
     * This method is implemented for demo. Called within buildScene() method.
     * Binds graphical blocks objects to its physical versions.
     */
    private void bindDefaultBlocksGraphics() {
        blocks = new HashMap<>();
        for (PhysicalBlock block : physicalBlocksP1) {
            GraphicalBlock graphicalBlockP1 = new GraphicalBlock(objects.createSprite("block_wood"), objects.createSprite("block_wood_damaged"), BLOCKS_SCALE);
            graphicalBlockP1.setOrigin(0,0);
            graphicalBlockP1.setPosition(block.getPosition().x, block.getPosition().y);
            drawables.add(graphicalBlockP1);
            block.setUpdatableSprite(graphicalBlockP1);
            blocks.put(block, graphicalBlockP1);
        }

        for (PhysicalBlock block : physicalBlocksP2) {
            GraphicalBlock graphicalBlockP2 = new GraphicalBlock(objects.createSprite("block_wood"), objects.createSprite("block_wood_damaged"), BLOCKS_SCALE);
            graphicalBlockP2.setOrigin(0,0);
            graphicalBlockP2.setPosition(block.getPosition().x, block.getPosition().y);
            drawables.add(graphicalBlockP2);
            block.setUpdatableSprite(graphicalBlockP2);
            blocks.put(block, graphicalBlockP2);
        }
    }

    /** This function sets up the UI. The name speaks for itself, really.
     * Creates the UI table and creates the layout of the UI elements.
     */
    private void setUpUI() {
        final float buttonWidth  = 200;
        final float buttonHeight = 100;
        table = new Table();
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stageForUI.addActor(table);

        Button endTurnButton = new TextButton("End Turn", new Skin(Gdx.files.internal("skin/flat-earth-ui.json")), "default");
        endTurnButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //toServerMessageSender.nextTurnMessage();
                PassTurnGenerator generator = new PassTurnGenerator();
                generator.generate(null, controller.getServerAddress(), controller.getServerPort(), controller);

                // temp
                playerTurn = (playerTurn == PlayerType.FIRST_PLAYER) ? PlayerType.SECOND_PLAYER : PlayerType.FIRST_PLAYER;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        table.bottom().left();
        table.add(endTurnButton).width(buttonWidth).height(buttonHeight);
        table.getCell(endTurnButton).spaceRight(Gdx.graphics.getWidth() - 2 * buttonWidth);

        Button fireButton = new TextButton("Fire", new Skin(Gdx.files.internal("skin/flat-earth-ui.json")), "default");
        fireButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // temp
                if (playerTurn == PlayerType.FIRST_PLAYER) {
                    Vector2 jointPosition = physicalTowerP1.getJointPosition();
                    float barrelLength = physicalTowerP1.getBarrelLength();
                    float angle = physicalTowerP1.getMovablePartAngle();
                    float cos = (float) Math.cos(angle);
                    float sin = (float) Math.sin(angle);
                    float shotX = jointPosition.x + barrelLength * cos - objects.findRegion("shot_basic").originalWidth  / 2f * SHOTS_SCALE;
                    float shotY = jointPosition.y + barrelLength * sin - objects.findRegion("shot_basic").originalHeight / 2f * SHOTS_SCALE;
                    Location location = new Location(shotX, shotY, 0, SHOTS_SCALE);
                    PhysicalBasicShot physicalShot = new PhysicalBasicShot(location, world);
                    physicalShot.setVelocity(new Vector2(20.f * cos, 20.f * sin));
                    movables.add(physicalShot);
                    GraphicalBasicShot graphicalShot = new GraphicalBasicShot(objects.createSprite("shot_basic"), SHOTS_SCALE);
                    graphicalShot.setOrigin(0, 0);
                    graphicalShot.setPosition(physicalShot.getPosition().x, physicalShot.getPosition().y);
                    drawables.add(graphicalShot);
                    physicalShot.setUpdatableSprite(graphicalShot);
                }
                // temp
                else if (playerTurn == PlayerType.SECOND_PLAYER) {
                    Vector2 jointPosition = physicalTowerP2.getJointPosition();
                    float barrelLength = physicalTowerP2.getBarrelLength();
                    float angle = physicalTowerP2.getMovablePartAngle();
                    float cos = (float) Math.cos(angle);
                    float sin = (float) Math.sin(angle);
                    float shotX = jointPosition.x - barrelLength * cos - objects.findRegion("shot_basic").originalWidth  / 2f * SHOTS_SCALE;
                    float shotY = jointPosition.y - barrelLength * sin - objects.findRegion("shot_basic").originalHeight / 2f * SHOTS_SCALE;
                    Location location = new Location(shotX, shotY, 0, SHOTS_SCALE);
                    PhysicalBasicShot physicalShot = new PhysicalBasicShot(location, world);
                    physicalShot.setVelocity(new Vector2(-20.f * cos, -20.f * sin));
                    movables.add(physicalShot);
                    GraphicalBasicShot graphicalShot = new GraphicalBasicShot(objects.createSprite("shot_basic"), SHOTS_SCALE);
                    graphicalShot.setOrigin(0, 0);
                    graphicalShot.setPosition(physicalShot.getPosition().x, physicalShot.getPosition().y);
                    drawables.add(graphicalShot);
                    physicalShot.setUpdatableSprite(graphicalShot);
                }
            }
        });
        table.add(fireButton).width(buttonWidth).height(buttonHeight);
    }

    private void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();

        accumulator += Math.min(delta, 0.25f);

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;

            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

            for (Movable movable : movables)
                movable.updateSprite();

            // temp debug stuff (demonstrating tower cannon rotation)
            if ((physicalTowerP1.getJoint().getJointAngle() >= physicalTowerP1.getJoint().getUpperLimit() && physicalTowerP1.getJoint().getMotorSpeed() > 0)||
                (physicalTowerP1.getJoint().getJointAngle() <= physicalTowerP1.getJoint().getLowerLimit() && physicalTowerP1.getJoint().getMotorSpeed() < 0))
                physicalTowerP1.getJoint().setMotorSpeed(-physicalTowerP1.getJoint().getMotorSpeed());

            if ((physicalTowerP2.getJoint().getJointAngle() >= physicalTowerP2.getJoint().getUpperLimit() && physicalTowerP2.getJoint().getMotorSpeed() > 0)||
                (physicalTowerP2.getJoint().getJointAngle() <= physicalTowerP2.getJoint().getLowerLimit() && physicalTowerP2.getJoint().getMotorSpeed() < 0))
                physicalTowerP2.getJoint().setMotorSpeed(-physicalTowerP2.getJoint().getMotorSpeed());

            removeDeadBlocks();
        }
    }

    private void removeDeadBlocks() {
        List<PhysicalBlock> toRemove = new ArrayList<>();
        for (PhysicalBlock block : blocks.keySet()) {
            if (block.getHP() <= 0) {
                world.destroyBody(block.getBody());
                movables.remove(block);
                drawables.remove(blocks.get(block));
                toRemove.add(block);
            }
        }
        for (PhysicalBlock block : toRemove)
            blocks.remove(block);
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

    /** Called when key is pressed, fires with P1 cannon
     * @param keycode key code (one of the Input.Keys)
     */
    @Override
    public boolean keyDown(int keycode) { return true; }

    @Override
    public boolean keyUp(int keycode) {return true;}

    @Override
    public boolean keyTyped(char character) {return true;}

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {return true;}

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {return true;}

    @Override
    public boolean mouseMoved(int screenX, int screenY) {return true;}

    @Override
    public boolean scrolled(int amount) { return true;}
}
