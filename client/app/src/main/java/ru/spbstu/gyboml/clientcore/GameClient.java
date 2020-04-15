package main.java.ru.spbstu.gyboml.clientcore;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.shot.ShotType;

/**
 * The GameClient class handles rendering, camera movement,
 * user input and the creation and disposal of graphic resources
 * implements methods that are invoked in the LibGDX game loop.
 * @since   2020-03-11
 */
public class GameClient extends ApplicationAdapter implements InputProcessor {
    // canvas / world constants
    private static final float minRatio     = 3f / 2f;
    private static final float minWidth     = 50;
    private static final float minHeight    = minWidth / minRatio;
    private static final float worldScale   = 1.5f;
    private static final float worldWidth   = minWidth * worldScale;
    private static final float worldHeight  = minHeight;
    private static final float maxXRatio    = 19.5f / 9f;
    private static final float maxYRatio    = 4f / 3f;
    private static final float canvasWidth  = worldWidth + minWidth * (maxXRatio / minRatio - 1);
    private static final float canvasHeight = worldHeight + minHeight * (minRatio / maxYRatio - 1);

    PhysicalScene  physicalScene;
    GraphicalScene graphicalScene;
    SoundEffects soundEffects;

    // drawing and stuff
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private Stage stageForUI;
    private Table table;

    // connection
    private MessageSender toServerMessageSender;

    // temp
    PlayerType playerTurn = PlayerType.FIRST_PLAYER;
    ShotType shotType = ShotType.BASIC;

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

        debugRenderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();

        float backgroundX = 0 - (canvasWidth - worldWidth) / 2;
        float backgroundY = 0 - (canvasHeight - worldHeight) / 2;

        graphicalScene = new GraphicalScene(canvasWidth, canvasHeight);
        physicalScene  = new PhysicalScene(graphicalScene, backgroundX, backgroundY);
        soundEffects = SoundEffects.get();

        // UI is setup after main game objects was created
        setUpUI();

        camera = new OrthographicCamera(minWidth, minHeight);
        viewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
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

        // End turn button
        Button endTurnButton = new TextButton("End Turn", new Skin(Gdx.files.internal("skin/flat-earth-ui.json")), "default");
        endTurnButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        table.bottom().left();
        table.add(endTurnButton).width(buttonWidth).height(buttonHeight);
        table.getCell(endTurnButton).spaceRight(Gdx.graphics.getWidth() - 2 * buttonWidth);

        // Fire button
        Button fireButton = new TextButton("Fire", new Skin(Gdx.files.internal("skin/flat-earth-ui.json")), "default");
        fireButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                physicalScene.generateShot(playerTurn, shotType);
                soundEffects.shot.play(1.f);
            }
        });
        table.add(fireButton).width(buttonWidth).height(buttonHeight);

        // HP progress bar
        HPBar bar1 = new HPBar(100);
        physicalScene.connectWithHPBar(PlayerType.FIRST_PLAYER, bar1);
        bar1.getHealthBar().setPosition(10, Gdx.graphics.getHeight() - 30);
        stageForUI.addActor(bar1.getHealthBar());

        HPBar bar2 = new HPBar(100);
        physicalScene.connectWithHPBar(PlayerType.SECOND_PLAYER, bar2);
        bar2.getHealthBar().setPosition(Gdx.graphics.getWidth() - HPBar.width - 10,
                Gdx.graphics.getHeight() - 30);
        stageForUI.addActor(bar2.getHealthBar());
    }


    /**
     * This is the main method that is called repeatedly in the game loop.
     * Renders all objects on the screen.
     */
    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        physicalScene.stepWorld();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        graphicalScene.draw(batch);
        batch.end();

        stageForUI.act(Gdx.graphics.getDeltaTime());
        stageForUI.draw();

        debugRenderer.render(physicalScene.getWorld(), camera.combined);
    }

    /**
     * This method is called upon the game loop's completion. Disposes of the graphical resources
     * that were set up in the create() method.
     */
    @Override
    public void dispose() {
        batch.dispose();
        graphicalScene.dispose();
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
        float x = Gdx.input.getDeltaX() * graphicalScene.getScale();
        float y = Gdx.input.getDeltaY() * graphicalScene.getScale();

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
