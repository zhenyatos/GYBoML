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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import ru.spbstu.gyboml.MainActivityOffline;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.scene.GameOver;
import ru.spbstu.gyboml.core.scene.GraphicalScene;
import ru.spbstu.gyboml.core.scene.HPBar;
import ru.spbstu.gyboml.core.scene.PhysicalSceneOffline;
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
public class GameOffline extends ApplicationAdapter implements InputProcessor, Winnable {
    //TODO: remove unnecessary
    private final MainActivityOffline activity;

    private static final int armoryRows = 4;
    private static final int armoryCols = 3;

    private SoundEffects soundEffects;
    private GraphicalScene graphicalScene;
    private PhysicalSceneOffline physicalScene;

    // drawing and stuff
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;

    //UI TODO: refactor
    private final List<Button> buttons = new ArrayList<>();
    private Stage stageForUI;
    private Skin UISkin;
    private Button fireButton;
    private ShotBar shotBar;
    private Table armoryCells;
    private boolean visibleArmory;
    private ImageButtonStyle fireStyle;
    private ImageButtonStyle aimStyle;
    private Image shotBasicTexture;
    private Image shotFireTexture;

    private PlayerType playerTurn = PlayerType.FIRST_PLAYER;
    private ShotType shotType = ShotType.BASIC;
    private boolean over = false;

    public GameOffline(MainActivityOffline activity) {
        this.activity = activity;
    }

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

        soundEffects = new SoundEffects();
        graphicalScene = new GraphicalScene();
        physicalScene = new PhysicalSceneOffline(graphicalScene, soundEffects);
        physicalScene.setTurn(playerTurn);

        // UI is setup after main game objects was created
        setUpUI();

        camera = new OrthographicCamera(SceneConstants.minWidth, SceneConstants.minHeight);
        viewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    /** This function sets up the UI. The name speaks for itself, really.
     * Creates the UI table and creates the layout of the UI elements.
     */
    private void setUpUI() {
        // TODO: place buttons using table properly
        float buttonWidth  = 0.13f * Gdx.graphics.getWidth();
        float buttonHeight = buttonWidth / 2f;
        UISkin = new Skin(Gdx.files.internal("skin/flat-earth-ui.json"));

        // leave button
        ImageButtonStyle leaveStyle = new ImageButtonStyle();
        leaveStyle.up   = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/leave_up.png"))));
        leaveStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/leave_down.png"))));
        ImageButton leaveButton = new ImageButton(leaveStyle);
        leaveButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Dialog dialog = new Dialog("Leave", UISkin) {
                    @Override
                    protected void result(Object object) {
                        if ((boolean)object) {
                            activity.finish();
                        }
                    }
                };
                // TODO: refactor scales
                dialog.text("Are you sure you want to exit?").setScale(2f);
                dialog.button("Yes", true).setWidth(2f);
                dialog.button("No", false).setWidth(2f);
                dialog.show(stageForUI);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        leaveButton.setSize(buttonWidth, buttonHeight);
        leaveButton.setPosition(0, 0);
        stageForUI.addActor(leaveButton);

        // shot bar
        shotBar = new ShotBar(buttonWidth);
        shotBar.getShotPowerBar().setVisible(false);
        shotBar.getShotPowerBar().setPosition(
                Gdx.graphics.getWidth() - (buttonWidth + shotBar.getShotPowerBar().getWidth()) / 2,
                buttonHeight * 1.1f);
        stageForUI.addActor(shotBar.getShotPowerBar());

        // Fire button
        fireStyle = new ImageButtonStyle();
        aimStyle  = new ImageButtonStyle();
        fireStyle.up      = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/fire_up.png"))));
        fireStyle.down    = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/fire_down.png"))));
        aimStyle.up       = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/aim_up.png"))));
        aimStyle.down     = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/aim_down.png"))));
        fireButton = new ImageButton(aimStyle);
        fireButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // if aimed
                if (shotBar.getShotPowerBar().isVisible()) {
                    fireButton.setStyle(aimStyle);
                    physicalScene.generateShot(playerTurn, shotType, shotBar.getShotPowerBar().getValue());
                    physicalScene.setCannonStatus(playerTurn, true);
                    fireButton.setTouchable(Touchable.disabled);
                    shotBar.getShotPowerBar().setVisible(false);
                    visibleArmory = false;
                    armoryCells.setVisible(false);
                }
                else {
                    fireButton.setStyle(fireStyle);
                    physicalScene.setCannonStatus(playerTurn, false);
                    soundEffects.playLoadShot();
                    shotBar.resetValue();
                    shotBar.getShotPowerBar().setVisible(true);
                    visibleArmory = false;
                    armoryCells.setVisible(false);
                }
            }
        });
        fireButton.setSize(buttonWidth, buttonHeight);
        fireButton.setPosition(Gdx.graphics.getWidth() - buttonWidth,0);
        stageForUI.addActor(fireButton);
        buttons.add(fireButton);

        // TODO: shots folder with .png is temp, remake with Skin (same for buttons)
        shotBasicTexture = new Image(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/shots/shot_basic.png"))));
        shotBasicTexture.setSize(buttonHeight, buttonHeight);
        shotBasicTexture.setPosition(Gdx.graphics.getWidth() - buttonWidth - 1.1f * buttonHeight, 0);
        shotBasicTexture.setVisible(true);
        stageForUI.addActor(shotBasicTexture);

        shotFireTexture = new Image(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/shots/shot_fire.png"))));
        shotFireTexture.setSize(buttonHeight, buttonHeight);
        shotFireTexture.setPosition(Gdx.graphics.getWidth() - buttonWidth - 1.1f * buttonHeight, 0);
        shotFireTexture.setVisible(false);
        stageForUI.addActor(shotFireTexture);

        setUpArmory(buttonWidth, buttonHeight);

        // HP progress bars
        HPBar bar1 = new HPBar(SceneConstants.castleHP);
        HPBar bar2 = new HPBar(SceneConstants.castleHP);
        physicalScene.connectWithHPBar(PlayerType.FIRST_PLAYER, bar1);
        physicalScene.connectWithHPBar(PlayerType.SECOND_PLAYER, bar2);
        bar1.getHealthBar().setPosition(10, Gdx.graphics.getHeight() - 30);
        bar2.getHealthBar().setPosition(Gdx.graphics.getWidth() - HPBar.width - 10,Gdx.graphics.getHeight() - 30);
        stageForUI.addActor(bar1.getHealthBar());
        stageForUI.addActor(bar2.getHealthBar());

        //Game over labels
        Label won1stPlayer = new Label("Player 1 wins!", UISkin, "title");
        Label won2ndPlayer = new Label("Player 2 wins!", UISkin, "title");
        won1stPlayer.setPosition(Gdx.graphics.getWidth() / 2f - won1stPlayer.getWidth() / 2f,Gdx.graphics.getHeight() / 2f - won1stPlayer.getHeight() / 2f);
        won2ndPlayer.setPosition(Gdx.graphics.getWidth() / 2f - won2ndPlayer.getWidth() / 2f,Gdx.graphics.getHeight() / 2f - won2ndPlayer.getHeight() / 2f);
        stageForUI.addActor(won1stPlayer);
        stageForUI.addActor(won2ndPlayer);
        physicalScene.connectWithGameOver(new GameOver(this, won1stPlayer, won2ndPlayer));
    }

    private void setUpArmory(float buttonWidth, float buttonHeight) {
        float armoryCellsWidth  = buttonWidth  * armoryCols;
        float armoryCellsHeight = buttonHeight * armoryRows;
        armoryCells = new Table();
        armoryCells.setVisible(false);
        visibleArmory = false;

        // TODO: refactor
        ImageButtonStyle emptyCellStyle = new ImageButtonStyle();
        emptyCellStyle.up   = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/button.png"))));
        emptyCellStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/button.png"))));

        ImageButtonStyle shotBasicCellStyle = new ImageButtonStyle();
        shotBasicCellStyle.up   = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/armory/armory_shot_basic_up.png"))));
        shotBasicCellStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/armory/armory_shot_basic_down.png"))));

        ImageButtonStyle shotFireCellStyle = new ImageButtonStyle();
        shotFireCellStyle.up   = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/armory/armory_shot_fire_up.png"))));
        shotFireCellStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/armory/armory_shot_fire_down.png"))));

        ImageButton shotBasicCell = new ImageButton(shotBasicCellStyle);
        ImageButton shotFireCell  = new ImageButton(shotFireCellStyle);
        ImageButton emptyCell     = new ImageButton(emptyCellStyle);

        shotBasicCell.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                shotType = ShotType.BASIC;
                shotBasicTexture.setVisible(true);
                shotFireTexture.setVisible(false);
                visibleArmory = false;
                armoryCells.setVisible(false);
                soundEffects.playArmory();
            }
        });

        shotFireCell.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                shotType = ShotType.FIRE;
                shotFireTexture.setVisible(true);
                shotBasicTexture.setVisible(false);
                visibleArmory = false;
                armoryCells.setVisible(false);
                soundEffects.playArmory();
            }
        });

        armoryCells.add(shotBasicCell).width(buttonWidth).height(buttonHeight);
        armoryCells.add(shotFireCell).width(buttonWidth).height(buttonHeight);
        armoryCells.add(emptyCell).width(buttonWidth).height(buttonHeight);

        // left empty cells
        for (int y = 1; y < armoryRows; y++) {
            armoryCells.row();
            for (int x = 0; x < armoryCols; x++){
                // TODO: #134 shots shop - after checking a cell change shotType and shotBasicTexture fields
                ImageButton cell = new ImageButton(emptyCellStyle);
                armoryCells.add(cell).width(buttonWidth).height(buttonHeight);
            }
        }

        // Show armory button
        ImageButtonStyle armoryStyle = new ImageButtonStyle();
        armoryStyle.up   = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/armory_up.png"))));
        armoryStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/armory_down.png"))));
        ImageButton armoryButton = new ImageButton(armoryStyle);
        armoryButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                visibleArmory = !visibleArmory;
                armoryCells.setVisible(visibleArmory);
                soundEffects.playArmory();
            }
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        buttons.add(armoryButton);
        armoryButton.setSize(buttonWidth, buttonHeight);
        armoryButton.setPosition(buttonWidth,0);
        armoryCells.setPosition(2 * buttonWidth + armoryCellsWidth / 2, 0 + armoryCellsHeight / 2);
        stageForUI.addActor(armoryButton);
        stageForUI.addActor(armoryCells);
    }

    // TODO: set timer and wait for objects to sleep
    private void switchTurn() {
        playerTurn = playerTurn.reverted();
        physicalScene.setTurn(playerTurn);
        graphicalScene.generateAnimatedPlayerTurn(playerTurn);
        soundEffects.playPlayerTurn(playerTurn);
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

        if (shotBar.getShotPowerBar().isVisible()) {
            shotBar.update();
        }

        if (!over && !fireButton.isTouchable() && physicalScene.isStopped()) {
            fireButton.setTouchable(Touchable.enabled);
            switchTurn();
        }

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
        stageForUI.dispose();
        graphicalScene.dispose();
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

        float leftEdgePos = camera.position.x - SceneConstants.minWidth / 2;
        float rightEdgePos = leftEdgePos + SceneConstants.minWidth;
        if (leftEdgePos  - x < 0)
            x = leftEdgePos;
        else if (rightEdgePos - x > SceneConstants.worldWidth)
            x = rightEdgePos - SceneConstants.worldWidth;

        float topEdgePos = camera.position.y + SceneConstants.minHeight / 2;
        float bottomEdgePos = topEdgePos - SceneConstants.minHeight;
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
        over = true;
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