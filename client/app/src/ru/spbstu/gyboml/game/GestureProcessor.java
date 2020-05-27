package ru.spbstu.gyboml.game;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

import ru.spbstu.gyboml.core.scene.SceneConstants;

public class GestureProcessor implements GestureDetector.GestureListener {
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private float currentZoom = 1;

    public GestureProcessor(OrthographicCamera camera, Viewport viewport) {
        this.camera = camera;
        this.viewport = viewport;

    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        currentZoom = camera.zoom;
       /* if (currentZoom == 1.0f) {
            camera.updatePosition(SceneConstants.worldWidth / 2, SceneConstants.worldHeight / 2);
        }*/
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        return false;
    }

    @Override
    public boolean longPress(float x, float y) {

        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {

        return false;
    }


    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {

        float worldDeltaX = -deltaX * SceneConstants.SCALE;
        float worldDeltaY = /*currentZoom < 1 ? deltaY * SceneConstants.SCALE : */0;

        float distanceToLeftBound = distanceToLeftBound(currentZoom);
        float distanceToRightBound = distanceToRightBound(currentZoom);
        if (worldDeltaX < distanceToLeftBound)
            worldDeltaX = distanceToLeftBound;
        else if (worldDeltaX > distanceToRightBound)
            worldDeltaX = distanceToRightBound;
        /*
        float distanceToUpperBound = distanceToUpperBound(currentZoom);
        float distanceToLowerBound = distanceToLowerBound(currentZoom);
        if (worldDeltaY > distanceToUpperBound)
            worldDeltaY = distanceToUpperBound;
        else if (worldDeltaY < distanceToLowerBound)
            worldDeltaY = distanceToLowerBound;
         */
        camera.translate(worldDeltaX, worldDeltaY);
        camera.update();

        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {

        return false;
    }

    @Override
    public boolean zoom (float originalDistance, float currentDistance) {
        float zoom = originalDistance / currentDistance * currentZoom;
        if (zoom > SceneConstants.worldScale)
            zoom = SceneConstants.worldScale;
        else if (zoom < 1)
            zoom = 1;
        camera.zoom = zoom;
        camera.update();

        float distanceToLeftBound = distanceToLeftBound(zoom);
        float distanceToRightBound = distanceToRightBound(zoom);

        if (distanceToLeftBound > 0)
            camera.translate(distanceToLeftBound, 0);
        else if (distanceToRightBound < 0)
            camera.translate(distanceToRightBound, 0);

        float distanceToUpperBound = distanceToUpperBound(zoom);
        float distanceToLowerBound = distanceToLowerBound(zoom);

        if (distanceToUpperBound < 0)
            camera.translate(0, distanceToUpperBound);
        if (distanceToLowerBound > 0)
            camera.translate(0, distanceToLowerBound);
        camera.update();


        return true;
    }

    @Override
    public boolean pinch (Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop () {
    }

    private float distanceToLeftBound(float zoom) {
        return 0 - (camera.position.x - SceneConstants.cameraWidth / 2 * zoom);
    }
    private float distanceToRightBound(float zoom) {
        return SceneConstants.worldWidth - (camera.position.x + SceneConstants.cameraWidth / 2 * zoom);
    }

    private float distanceToUpperBound(float zoom) {
        return SceneConstants.worldHeight * (1 + (SceneConstants.worldScale - 1) / 2)
                - (camera.position.y + SceneConstants.cameraHeight / 2 * zoom);
    }

    private float distanceToLowerBound(float zoom) {
        return -SceneConstants.worldHeight * (SceneConstants.worldScale - 1) / 2
                - (camera.position.y - SceneConstants.cameraHeight / 2 * zoom);
    }
}
