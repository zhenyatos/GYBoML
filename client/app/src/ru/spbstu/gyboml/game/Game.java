package ru.spbstu.gyboml.game;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.spbstu.gyboml.GybomlClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import ru.spbstu.gyboml.MainActivity;
import ru.spbstu.gyboml.core.scene.GameOver;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.net.GameRequests;
import ru.spbstu.gyboml.core.physical.PhysicalShot;
import ru.spbstu.gyboml.core.scene.GraphicalScene;
import ru.spbstu.gyboml.core.scene.HPBar;
import ru.spbstu.gyboml.core.scene.PhysicalScene;
import ru.spbstu.gyboml.core.scene.SceneConstants;
import ru.spbstu.gyboml.core.scene.SoundEffects;
import ru.spbstu.gyboml.core.shot.ShotType;
import ru.spbstu.gyboml.core.Winnable;

/**
 * The GameClient class handles rendering, camera movement,
 * user input and the creation and disposal of graphic resources
 * implements methods that are invoked in the LibGDX game loop.
 * @since   2020-03-11
 */
public class Game extends ApplicationAdapter implements InputProcessor, Winnable {
    MainActivity mainActivity;

    private static final float buttonWidth  = 200 / Gdx.graphics.getWidth();
    private static final float buttonHeight = 100 / Gdx.graphics.getHeight();

    private static final int armoryRowCount = 4;
    private static final int armoryColumnCount = 4;
    private static final float armoryChooseButtonWidthFactor = 2 / 3.0f;

    PhysicalScene physicalScene;
    GraphicalScene graphicalScene;
    SoundEffects soundEffects;

    // drawing and stuff
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;

    //UI
    private GestureDetector gestureDetector;
    private Stage stageForUI;
    private Table table;
    private final List<Button> buttons = new ArrayList<>();
    //private Label victoryLabel;

    private Skin earthSkin;
    private Table armoryCells;
    private boolean visibleArmory;

    private GameListener gameListener;
    private ImageButton fireButton;
    private PlayerType playerTurn = PlayerType.FIRST_PLAYER;

    // temp
    ShotType shotType = ShotType.BASIC;

    public Game( MainActivity mainActivity ) {this.mainActivity = mainActivity;}

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
        gestureDetector = new GestureDetector(new GestureProcessor(camera, viewport));
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stageForUI);
        inputMultiplexer.addProcessor(gestureDetector);
        inputMultiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(inputMultiplexer);

        debugRenderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();

        graphicalScene = new GraphicalScene();
        physicalScene = new PhysicalScene(graphicalScene);
        //soundEffects = SoundEffects.get();

        this.connectWithGraphicalScene();
        //physicalScene.connectWithSoundEffects(soundEffects);
        //graphicalScene.connectWithSoundEffects(soundEffects);

        // UI is setup after main game objects was created
        setUpUI();

        camera = new OrthographicCamera(SceneConstants.cameraWidth, SceneConstants.cameraHeight);
        viewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        Timer t = new Timer();

        // add game listener
        gameListener = new GameListener(this);
        GybomlClient.getClient().addListener(gameListener);
    }

    /** This function sets up the UI. The name speaks for itself, really.
     * Creates the UI table and creates the layout of the UI elements.
     */
    private void setUpUI() {
        table = new Table();
        //table.setDebug(true);
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stageForUI.addActor(table);

        Skin UISkin = new Skin(Gdx.files.internal("skin/flat-earth-ui.json"));

        // End turn button
        TextureRegionDrawable leaveUp   = new TextureRegionDrawable(
                new TextureRegion(
                        new Texture(Gdx.files.internal("skin/buttons/leave_up.png"))));
        TextureRegionDrawable leaveDown = new TextureRegionDrawable(
                new TextureRegion(
                        new Texture(Gdx.files.internal("skin/buttons/leave_down.png"))));
        ImageButton exitButton = new ImageButton(leaveUp, leaveDown);

        exitButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Dialog dialog = new Dialog("Are you sure you want to exit?", UISkin)
                {
                    @Override
                    protected void result(Object object) {
                        if ((boolean)object) {
                            GybomlClient.sendTCP(new GameRequests.GameExit());
                        }
                    }
                };
                dialog.button("Yes", true);
                dialog.button("No", false);
                dialog.show(stageForUI);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        table.bottom().left();
        table.row();
        table.add(exitButton).width(buttonWidth * Gdx.graphics.getWidth()).height(buttonHeight * Gdx.graphics.getHeight()).bottom();

        setUpArmoryStorage();

        // Fire button
        TextureRegionDrawable fireUp      = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/fire_up.png"))));
        TextureRegionDrawable fireDown    = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/fire_down.png"))));
        fireButton = new ImageButton(fireUp, fireDown);
        fireButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                physicalScene.generateShot(GybomlClient.getPlayer().type, shotType);
                graphicalScene.generateGraphicalShot(physicalScene.getLastShot());

                // send shot to server
                GameRequests.Shoot shootRequest = new GameRequests.Shoot();
                PhysicalShot shot = physicalScene.getLastShot();
                Vector2 position = shot.getPosition();
                Vector2 velocity = shot.getVelocity();
                shootRequest.ballPosition = position;
                shootRequest.ballVelocity = velocity;

                GybomlClient.sendTCP(shootRequest);

                switchTurn();
            }
        });
        table.add(fireButton).width(buttonWidth * Gdx.graphics.getWidth()).height(buttonHeight * Gdx.graphics.getHeight()).bottom().
                spaceLeft(Gdx.graphics.getWidth() * (1 - (3 + armoryColumnCount * armoryChooseButtonWidthFactor) * buttonWidth));

        buttons.add(fireButton);

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

        //Game over labels
        Label victoryLabel = new Label("Victory!", UISkin, "title");
        victoryLabel.setPosition(Gdx.graphics.getWidth() / 2f - victoryLabel.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - victoryLabel.getHeight() / 2f);
        stageForUI.addActor(victoryLabel);
        Label defeatLabel = new Label("Defeat!", UISkin, "title");
        defeatLabel.setPosition(Gdx.graphics.getWidth() / 2f - defeatLabel.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - defeatLabel.getHeight() / 2f);
        physicalScene.connectWithGameOver(PlayerType.FIRST_PLAYER, new GameOver(this, victoryLabel, defeatLabel));
        physicalScene.connectWithGameOver(PlayerType.SECOND_PLAYER, new GameOver(this, victoryLabel, defeatLabel));
    }

    private void setUpArmoryStorage() {
        Table armoryTable = new Table();
        armoryCells = new Table();
        visibleArmory = false;
        armoryCells.setVisible(visibleArmory);

        earthSkin = new Skin(Gdx.files.internal("skin/flat-earth-ui.json"));

        for (int y = 0; y < armoryRowCount; y++) {
            armoryCells.row();
            for (int x = 0; x < armoryColumnCount; x++){
                TextButton cell = new TextButton("Cell " + y + ", " + x, earthSkin, "default");
                armoryCells.add(cell).
                        width(buttonWidth * armoryChooseButtonWidthFactor * Gdx.graphics.getWidth());
                buttons.add(cell);
            }

        }

        // Show armory button
        TextureRegionDrawable armoryUp      = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/armory_up.png"))));
        TextureRegionDrawable armoryDown    = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/armory_down.png"))));
        TextureRegionDrawable armoryChecked = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/armory_down.png"))));
        ImageButton showArmory = new ImageButton(armoryUp, armoryDown, armoryChecked);
        buttons.add(showArmory);

        showArmory.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                visibleArmory = !visibleArmory;
                armoryCells.setVisible(visibleArmory);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        armoryTable.row();
        armoryTable.bottom().left();
        armoryTable.add(showArmory).width(buttonWidth * Gdx.graphics.getWidth()).height(buttonHeight * Gdx.graphics.getHeight()).bottom();
        armoryTable.add(armoryCells);


        table.add(armoryTable);//.spaceBottom(Gdx.graphics.getHeight() -
        //(buttonHeight + armoryRowCount * buttonHeight * heightFactor) * Gdx.graphics.getHeight());
    }

    // TODO: set timer and wait for objects to sleep
    synchronized void switchTurn() {
        if (fireButton.isTouchable())
            fireButton.setTouchable(Touchable.disabled);
        else
            fireButton.setTouchable(Touchable.enabled);
        playerTurn = playerTurn.reverted();
        //EventSystem.get().emit(this, "switchTurn", playerTurn);
    }

    private void connectWithGraphicalScene() {
        /*EventSystem.get().connect(this, "switchTurn", graphicalScene, "generateAnimatedPlayerTurn");*/
    }

    /**
     * This is the main method that is called repeatedly in the game loop.
     * Renders all objects on the screen.
     */
    @Override
    synchronized public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        physicalScene.stepWorld();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        graphicalScene.draw(batch);
        batch.end();

        stageForUI.act(Gdx.graphics.getDeltaTime());
        stageForUI.draw();


        //debugRenderer.render(physicalScene.getWorld(), camera.combined);
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
        GybomlClient.getClient().removeListener(gameListener);
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
        float x = Gdx.input.getDeltaX() * SceneConstants.SCALE;
        float y = Gdx.input.getDeltaY() * SceneConstants.SCALE;

        float leftEdgePos = camera.position.x - SceneConstants.cameraWidth / 2;
        float rightEdgePos = leftEdgePos + SceneConstants.cameraWidth;
        if (leftEdgePos  - x < 0)
            x = leftEdgePos;
        else if (rightEdgePos - x > SceneConstants.worldWidth)
            x = rightEdgePos - SceneConstants.worldWidth;

        float topEdgePos = camera.position.y + SceneConstants.cameraHeight / 2;
        float bottomEdgePos = topEdgePos - SceneConstants.cameraHeight;
        if (topEdgePos + y > SceneConstants.worldHeight)
            y = SceneConstants.worldHeight - topEdgePos;
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

    @Override
    public void disableButtons() {
        for (Button button : buttons)
            button.setTouchable(Touchable.disabled);
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
