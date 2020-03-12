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

public class GameClient extends ApplicationAdapter implements InputProcessor {
    private static final float SCALE = 1f / 20f; // 1 meter per 20 pixels
    private static final float minRatio = 3f / 2f;
    private static final float minWidth = 50;
    private static final float minHeight = minWidth / minRatio;
    private static final float worldScale = 1.0f; //how many cameras can fit into the gameplay area
    private static final float worldWidth = minWidth * worldScale;
    private static final float worldHeight = minHeight * worldScale;
    private static final float maxXRatio = 19.5f / 9f;
    private static final float maxYRatio = 4f / 3f;

    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Sprite background;
    private OrthographicCamera camera;
    private ExtendViewport viewport;


    @Override
    public void create () {
        Gdx.input.setInputProcessor(this);

        batch = new SpriteBatch();
        backgroundTexture = new Texture("background.png");
        background = new Sprite(backgroundTexture);
        float width = worldWidth * maxXRatio / minRatio;
        float height = worldHeight * (1 / maxYRatio) / (1 / minRatio);

        background.setSize(width, height);
        background.setOrigin(0, 0);
        background.setPosition(0 - (width - worldWidth) / 2,0 - (height - worldHeight) / 2);

        camera = new OrthographicCamera(minWidth, minHeight);
        viewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(1, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        background.draw(batch);
        batch.end();
    }

    @Override
    public void dispose () {
        batch.dispose();
        backgroundTexture.dispose();
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float x = Gdx.input.getDeltaX() * SCALE;
        float y = Gdx.input.getDeltaY() * SCALE;

        float leftEdgePos = camera.position.x - camera.viewportWidth / 2;
        float rightEdgePos = leftEdgePos + camera.viewportWidth;
        if (leftEdgePos  - x < 0)
            x = leftEdgePos;
        else if (rightEdgePos - x > worldWidth)
            x = rightEdgePos - worldWidth;

        float topEdgePos = camera.position.y + camera.viewportHeight / 2;
        float bottomEdgePos = topEdgePos - camera.viewportHeight;
        if (topEdgePos + y > worldHeight)
            y = worldHeight - topEdgePos;
        else if (bottomEdgePos + y < 0)
            y = -bottomEdgePos;

        camera.position.add(-x, y, 0);
        camera.update();

        return true;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    public boolean keyDown (int keycode) {return true;}

    public boolean keyUp (int keycode) {return true;}

    public boolean keyTyped (char character) {return true;}

    public boolean touchDown (int screenX, int screenY, int pointer, int button) {return true;}

    public boolean touchUp (int screenX, int screenY, int pointer, int button) {return true;}

    public boolean mouseMoved (int screenX, int screenY) {return true;}

    public boolean scrolled (int amount) { return true;}
}
