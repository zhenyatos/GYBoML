package main.java.ru.spbstu.clientcore;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * The GameClient class handles rendering, camera movement,
 * user input and the creation and disposal of graphic resources
 * implements methods that are invoked in the LibGDX game loop.
 * @since   2020-03-11
 */
public class GameClient extends ApplicationAdapter implements InputProcessor {
    private static final float SCALE = 1f / 20f;
    private static final float minRatio = 3f / 2f;
    private static final float minWidth = 50;
    private static final float minHeight = minWidth / minRatio;
    private static final float worldScale = 1.5f;
    private static final float worldWidth = minWidth * worldScale;
    private static final float worldHeight = minHeight;
    private static final float maxXRatio = 19.5f / 9f;
    private static final float maxYRatio = 4f / 3f;

    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Sprite background;
    private OrthographicCamera camera;
    private ExtendViewport viewport;


    /**
     * This is the method that is called on client's creation.
     * It loads the graphical resources, and sets up the sprites, background,
     * viewport and camera.
     */
    @Override
    public void create() {
        Gdx.input.setInputProcessor(this);

        batch = new SpriteBatch();
        backgroundTexture = new Texture("bg_scheme.png");
        background = new Sprite(backgroundTexture);
        float width = worldWidth + minWidth * (maxXRatio / minRatio - 1);
        float height = worldHeight + minHeight * (minRatio / maxYRatio - 1);

        background.setSize(width, height);
        background.setOrigin(0, 0);
        background.setPosition(0 - (width - worldWidth) / 2,0 - (height - worldHeight) / 2);

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
        background.draw(batch);
        batch.end();
    }

    /**
     * This method is called upon the game loop's completion. Disposes of the graphical resources
     * that were set up in the create() method.
     */
    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
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
    }

    public boolean keyDown(int keycode) {return true;}

    public boolean keyUp(int keycode) {return true;}

    public boolean keyTyped(char character) {return true;}

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {return true;}

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {return true;}

    public boolean mouseMoved(int screenX, int screenY) {return true;}

    public boolean scrolled(int amount) { return true;}
}
