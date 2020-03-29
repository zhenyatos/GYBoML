package main.java.ru.spbstu.gyboml.clientcore;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
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
import main.java.ru.spbstu.gyboml.graphics.GraphicalCastle;
import main.java.ru.spbstu.gyboml.graphics.GraphicalForeground;
import main.java.ru.spbstu.gyboml.graphics.GraphicalTower;
import ru.spbstu.gyboml.core.physical.Background;
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
    private static final float gravityAccelerationX = 0f;
    private static final float gravityAccelerationY = -10f;

    //private MessageSender toServerMessageSender;
    private SpriteBatch batch;
    private TextureAtlas background_1;
    private TextureAtlas background_2;
    private TextureAtlas objects;
    private final List<Drawable> drawables = new ArrayList<>();
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private Stage stageForUI;
    private Table table;
    private World world;

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

        world = new World(new Vector2(gravityAccelerationX, gravityAccelerationY), true);

        batch = new SpriteBatch();
        background_1 = new TextureAtlas("sprites/background_1.txt");
        background_2 = new TextureAtlas("sprites/background_2.txt");
        objects = new TextureAtlas("sprites/objects.txt");

        Sprite backgroundSky = background_1.createSprite("bg_sky");
        Sprite backgroundDesert = background_1.createSprite("bg_desert");
        Sprite backgroundLand = background_2.createSprite("bg_land");
        SCALE = canvasWidth / backgroundSky.getWidth();

        GraphicalBackground background = new GraphicalBackground(backgroundSky, backgroundDesert,
                backgroundLand, SCALE);



        float backgroundX = 0 - (canvasWidth - worldWidth) / 2;
        float backgroundY = 0 - (canvasHeight - worldHeight) / 2;

        Position backgroundPosition = new Position(backgroundX, backgroundY, 1);

        Background physicalBackground = new Background(backgroundPosition, world);

        background.setSize(canvasWidth, canvasHeight);
        background.setOrigin(0, 0);
        background.setPosition(backgroundX, backgroundY);
        drawables.add(background);

        GraphicalTower leftTower = new GraphicalTower(objects.createSprite("tower_p1"), SCALE);
        leftTower.setOrigin(0, 0);
        leftTower.setPosition(0, 5);
        drawables.add(leftTower);

        GraphicalCastle leftCastle = new GraphicalCastle(objects.createSprite("castle_p1_back"),
                objects.createSprite("castle_p1_front"), objects.createSprite("castle_p1_tower"), SCALE, 100);
        leftCastle.setOrigin(0, 0);
        leftCastle.setPosition(0 + leftCastle.getWidth() + leftTower.getWidth(), 5);
        drawables.add(leftCastle);

        GraphicalTower rightTower = new GraphicalTower(objects.createSprite("tower_p2"), SCALE);
        rightTower.setOrigin(0, 0);
        rightTower.setPosition(worldWidth - rightTower.getWidth(), 5);
        drawables.add(rightTower);

        GraphicalCastle rightCastle = new GraphicalCastle(objects.createSprite("castle_p2_back"),
                objects.createSprite("castle_p2_front"), objects.createSprite("castle_p2_tower"), SCALE, 100);
        rightCastle.setOrigin(0, 0);
        rightCastle.setPosition(worldWidth - rightTower.getWidth() - rightCastle.getWidth() * 2, 5);
        drawables.add(rightCastle);

        GraphicalForeground graphicalForeground = new GraphicalForeground(background_2.createSprite("bg_front"), SCALE);
        graphicalForeground.setSize(canvasWidth, canvasHeight);
        graphicalForeground.setOrigin(0, 0);
        graphicalForeground.setPosition(backgroundX, backgroundY);
        drawables.add(graphicalForeground);

        camera = new OrthographicCamera(minWidth, minHeight);
        viewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    /**
     * This is the main method that is called repeatedly in the game loop.
     * Renders all objects on the screen.
     */
    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Drawable object : drawables)
            object.draw(batch);
        batch.end();

        stageForUI.act(Gdx.graphics.getDeltaTime());
        stageForUI.draw();
    }

    /**
     * This method is called upon the game loop's completion. Disposes of the graphical resources
     * that were set up in the create() method.
     */
    @Override
    public void dispose() {
        batch.dispose();
        background_1.dispose();
        background_2.dispose();
        objects.dispose();
        stageForUI.dispose();
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
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //toServerMessageSender.nextTurnMessage(); method stub
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
