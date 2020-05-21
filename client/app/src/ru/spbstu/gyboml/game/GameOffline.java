package ru.spbstu.gyboml.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.collision.BoundingBox;
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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ru.spbstu.gyboml.MainActivityOffline;
import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.event.Events;
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
public class GameOffline extends ApplicationAdapter implements Winnable {
    //TODO: remove unnecessary
    private final MainActivityOffline activity;

    private static final int armoryRows = 4;
    private static final int armoryCols = 3;
    private static final int initPoints = 500;

    private SoundEffects soundEffects;
    private GraphicalScene graphicalScene;
    private PhysicalSceneOffline physicalScene;

    // drawing and stuff
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;

    //UI TODO: refactor
    private GestureDetector gestureDetector;
    private final List<Button> buttons = new ArrayList<>();
    private Stage stageForUI;
    private Skin UISkin;
    private Button fireButton;
    private Button armoryButton;
    private Button soundOffButton;
    private ShotBar shotBar;
    private Table armoryCells;
    private boolean visibleArmory;
    private ImageButtonStyle soundOffStyle, soundOnStyle;
    private ImageButtonStyle fireStyle;
    private ImageButtonStyle aimStyle;
    private Image shotBasicTexture;
    private Image shotFireTexture;
    private Label shotBasicCostLabel;
    private Label shotFireCostLabel;

    int shotBasicCost = 10;
    int shotFireCost = 50;

    private PlayerType playerTurn = PlayerType.FIRST_PLAYER;
    private Player player1, player2, current;

    private ShotType shotType = ShotType.BASIC;
    private boolean over = false;
    private boolean stepOver = false;

    private Player getPlayer(PlayerType type) {
        if (type == PlayerType.FIRST_PLAYER)
            return player1;
        else
            return player2;
    }

    Score score1;
    Score score2;

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
        player1 = new Player("First", initPoints);
        player2 = new Player("Second", initPoints);

        camera = new OrthographicCamera(SceneConstants.cameraWidth, SceneConstants.cameraHeight);
        viewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.zoom = 1.0f;
        camera.update();

        stageForUI = new Stage(new ScreenViewport());
        gestureDetector = new GestureDetector(new GestureProcessor(camera, viewport));
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stageForUI);
        inputMultiplexer.addProcessor(gestureDetector);
        Gdx.input.setInputProcessor(inputMultiplexer);

        debugRenderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();

        soundEffects = new SoundEffects();
        graphicalScene = new GraphicalScene();
        physicalScene = new PhysicalSceneOffline(graphicalScene, soundEffects);
        physicalScene.setTurn(playerTurn);


        // UI is setup after main game objects was created
        setUpUI();

        /*camera.zoom = 2.0f;
        camera.update();*/
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
                    stepOver = true;
                    shotBar.getShotPowerBar().setVisible(false);
                }
                else {
                    fireButton.setStyle(fireStyle);
                    physicalScene.setCannonStatus(playerTurn, false);
                    soundEffects.playLoadShot();
                    shotBar.resetValue();
                    shotBar.getShotPowerBar().setVisible(true);
                    visibleArmory = false;
                }

                shotFireCostLabel.setVisible(false);
                shotBasicCostLabel.setVisible(false);
                visibleArmory = false;
                armoryCells.setVisible(false);

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
        shotBasicTexture.setVisible(false);
        stageForUI.addActor(shotBasicTexture);

        shotFireTexture = new Image(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/shots/shot_fire.png"))));
        shotFireTexture.setSize(buttonHeight, buttonHeight);
        shotFireTexture.setPosition(Gdx.graphics.getWidth() - buttonWidth - 1.1f * buttonHeight, 0);
        shotFireTexture.setVisible(false);
        stageForUI.addActor(shotFireTexture);
        fireButton.setTouchable(Touchable.disabled);

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

        // sound button
        soundOffStyle      = new ImageButtonStyle();
        soundOffStyle.up   = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/sound_off.png"))));
        soundOffStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/sound_off.png"))));

        soundOnStyle       = new ImageButtonStyle();
        soundOnStyle.up    = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/sound_on.png"))));
        soundOnStyle.down  = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/sound_on.png"))));

        soundOffButton = new ImageButton(soundOnStyle);

        soundOffButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                float volume = 1.0f - soundEffects.getEffectsVolume();
                soundEffects.setEffectsVolume(volume);
                soundOffButton.setStyle(volume > 0.5 ? soundOnStyle : soundOffStyle);
            }
        });

        float hbSize = bar1.getHealthBar().getHeight();
        float soundOffSize = 3.5f * hbSize;
        soundOffButton.setSize(soundOffSize, soundOffSize);
        soundOffButton.setPosition(Gdx.graphics.getWidth() - soundOffSize, bar1.getHealthBar().getY() - soundOffSize);

        stageForUI.addActor(soundOffButton);

        //Game over labels
        Label won1stPlayer = new Label("Player 1 wins!", UISkin, "title");
        Label won2ndPlayer = new Label("Player 2 wins!", UISkin, "title");
        won1stPlayer.setPosition(Gdx.graphics.getWidth() / 2f - won1stPlayer.getWidth() / 2f,Gdx.graphics.getHeight() / 2f - won1stPlayer.getHeight() / 2f);
        won2ndPlayer.setPosition(Gdx.graphics.getWidth() / 2f - won2ndPlayer.getWidth() / 2f,Gdx.graphics.getHeight() / 2f - won2ndPlayer.getHeight() / 2f);
        stageForUI.addActor(won1stPlayer);
        stageForUI.addActor(won2ndPlayer);
        physicalScene.connectWithGameOver(new GameOver(this, won1stPlayer, won2ndPlayer));

        // Scores
        score1 = new Score(initPoints, Color.GOLD);
        score2 = new Score(initPoints, Color.BLUE);
        score1.getText().setPosition(Gdx.graphics.getWidth() - buttonWidth - 3f * buttonHeight, 10f);
        score1.getText().setVisible(true);
        score2.getText().setPosition(Gdx.graphics.getWidth() - buttonWidth - 3f * buttonHeight, 10f);
        score2.getText().setVisible(false);
        stageForUI.addActor(score1.getText());
        stageForUI.addActor(score2.getText());
        connectScoring();
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

        // Show armory button
        ImageButtonStyle armoryStyle = new ImageButtonStyle();
        armoryStyle.up   = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/armory_up.png"))));
        armoryStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("skin/buttons/armory_down.png"))));
        armoryButton = new ImageButton(armoryStyle);
        armoryButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                visibleArmory = !visibleArmory;
                armoryCells.setVisible(visibleArmory);
                soundEffects.playArmory();
                shotBasicCostLabel.setVisible(visibleArmory);
                shotFireCostLabel.setVisible(visibleArmory);
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
                shotFireTexture.setVisible(false);
                shotBasicCostLabel.setVisible(false);
                shotFireCostLabel.setVisible(false);
                visibleArmory = false;
                armoryCells.setVisible(false);
                soundEffects.playArmory();
                if (getPlayer(playerTurn).spentPoints(shotBasicCost)) {
                    shotBasicTexture.setVisible(true);
                    fireButton.setTouchable(Touchable.enabled);
                    armoryButton.setTouchable(Touchable.disabled);
                }
            }
        });

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 22;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle textStyle = new Label.LabelStyle();
        textStyle.font = font;
        textStyle.fontColor = new Color().set(1, 1, 0, 1);

        shotBasicCostLabel = new Label(String.valueOf(shotBasicCost), textStyle);
        shotBasicCostLabel.setBounds(0, 0f, 290, 20);
        shotBasicCostLabel.setFontScale(1f, 1f);
        shotBasicCostLabel.setPosition(2 * buttonWidth + (buttonWidth - 2 * shotBasicCostLabel.getMinWidth()),
                (int)((armoryRows - 1 + 0.25) * buttonHeight));
        shotBasicCostLabel.setVisible(false);

        shotFireCostLabel = new Label(String.valueOf(shotFireCost), textStyle);
        shotFireCostLabel.setBounds(0, 0f, 290, 20);
        shotFireCostLabel.setFontScale(1f, 1f);
        shotFireCostLabel.setPosition(3 * buttonWidth + (buttonWidth - 2 * shotFireCostLabel.getMinWidth()),
                (int)((armoryRows - 1 + 0.25) * buttonHeight));
        shotFireCostLabel.setVisible(false);


        shotFireCell.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                shotType = ShotType.FIRE;
                shotBasicTexture.setVisible(false);
                visibleArmory = false;
                armoryCells.setVisible(false);
                shotBasicCostLabel.setVisible(false);
                shotFireCostLabel.setVisible(false);
                soundEffects.playArmory();
                if (getPlayer(playerTurn).spentPoints(shotFireCost)) {
                    shotFireTexture.setVisible(true);
                    fireButton.setTouchable(Touchable.enabled);
                    armoryButton.setTouchable(Touchable.disabled);
                }
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

        stageForUI.addActor(shotBasicCostLabel);
        stageForUI.addActor(shotFireCostLabel);
    }

    // TODO: set timer and wait for objects to sleep
    private void switchTurn() {
        playerTurn = playerTurn.reverted();
        score1.getText().setVisible(!score1.getText().isVisible());
        score2.getText().setVisible(!score2.getText().isVisible());
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

        if (!over /*&& !fireButton.isTouchable() */&& physicalScene.isStopped() && stepOver) {
            stepOver = false;
            shotBasicTexture.setVisible(false);
            shotFireTexture.setVisible(false);
            armoryButton.setTouchable(Touchable.enabled);
            //fireButton.setTouchable(Touchable.enabled);
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

    private void connectScoring() {
        Method gotPoints = Events.get().find(Player.class, "gotPoints", int.class);
        Method spentPoints = Events.get().find(Player.class, "spentPoints", int.class);
        Method player1scores = Events.get().find(PhysicalSceneOffline.class, "player1scores", int.class);
        Method player2scores = Events.get().find(PhysicalSceneOffline.class, "player2scores", int.class);
        Events.get().connect(physicalScene, player1scores, player1, gotPoints);
        Events.get().connect(physicalScene, player2scores, player2, gotPoints);

        Method changeValue = Events.get().find(Score.class, "changeValue", int.class);
        Events.get().connect(player1, gotPoints, score1, changeValue);
        Events.get().connect(player2, gotPoints, score2, changeValue);

        Events.get().connect(player1, spentPoints, score1, changeValue);
        Events.get().connect(player2, spentPoints, score2, changeValue);
    }
}
