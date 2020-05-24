package ru.spbstu.gyboml.game;




import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.viewport.Viewport;



import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.spbstu.gyboml.core.scene.SceneConstants;

public class GestureProcessor implements GestureDetector.GestureListener {
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private float currentZoom = 1;
    private final ScheduledExecutorService momentumTimer;
    private final Interpolation momentumLossInterpolation = getInterpolation("fade");
    private ScheduledFuture<?> currentVelocityTask;
    private static final long velocityUpdatePeriod = 15L;
    private static final float velocityReductionRate = 10 / 15f;
    private static final float minVelocity = 1f;

    public GestureProcessor(OrthographicCamera camera, Viewport viewport) {
        this.camera = camera;
        this.viewport = viewport;
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.setRemoveOnCancelPolicy(true);
        momentumTimer = executor;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        currentZoom = camera.zoom;
        cancelVelocityTask();
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
        if (currentZoom == SceneConstants.worldScale)
            return false;
        float velocity = -velocityX * SceneConstants.SCALE / 15f;
        if (Math.abs(velocity) < minVelocity)
            return false;
        cancelVelocityTask();
        currentVelocityTask = momentumTimer.scheduleAtFixedRate(new Runnable() {
            private int interpolationCounter = 0;
            @Override
            public void run() {
                if (currentZoom != camera.zoom) {
                    currentVelocityTask.cancel(true);
                    return;
                }
                int interpolationIterationNum = 500 + (int)(velocity / velocityReductionRate);
                float currentVelocity = velocity * momentumLossInterpolation.
                        apply((float)(interpolationIterationNum - interpolationCounter) / interpolationIterationNum);
                move(currentVelocity, 0);
                interpolationCounter++;
            }
        }, 0, velocityUpdatePeriod, TimeUnit.MILLISECONDS);
        return true;
    }


    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (currentVelocityTask != null && !currentVelocityTask.isCancelled() || currentZoom == SceneConstants.worldScale)
            return true;
        move(-deltaX * SceneConstants.SCALE * 1.5f, 0);
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        cancelVelocityTask();
        return false;
    }

    @Override
    public boolean zoom (float originalDistance, float currentDistance) {
        float zoom = originalDistance / currentDistance * currentZoom;
        if (zoom > SceneConstants.worldScale)
            zoom = SceneConstants.worldScale;
        else if (zoom < 1)
            zoom = 1;
        cancelVelocityTask();
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

    private void move(float worldDeltaX, float worldDeltaY) {
        float distanceToLeftBound = distanceToLeftBound(currentZoom);
        float distanceToRightBound = distanceToRightBound(currentZoom);
        if (worldDeltaX < distanceToLeftBound) {
            worldDeltaX = distanceToLeftBound;
            cancelVelocityTask();
        }
        else if (worldDeltaX > distanceToRightBound) {
            worldDeltaX = distanceToRightBound;
            cancelVelocityTask();
        }

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
    }

    private Interpolation getInterpolation (String name) {
        try {
            return (Interpolation)ClassReflection.getField(Interpolation.class, name).get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void cancelVelocityTask() {
        if (currentVelocityTask != null && !currentVelocityTask.isCancelled())
            currentVelocityTask.cancel(true);
    }
}
