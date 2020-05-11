package ru.spbstu.gyboml.core.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.spbstu.gyboml.core.physical.Location;

public class AnimatedInstance implements Animated, Updatable {
    private final Animation<TextureRegion> animation;
    private Location location;
    private final float width;
    private final float height;
    private float stateTime = 0f;

    public AnimatedInstance(Animation<TextureRegion> animation, Location location) {
        this.animation = animation;
        this.location = new Location(location);
        width = animation.getKeyFrames()[0].getRegionWidth() * this.location.scale;
        height = animation.getKeyFrames()[0].getRegionHeight() * this.location.scale;
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(animation.getKeyFrame(stateTime, true), location.x, location.y, width, height);
        stateTime += Gdx.graphics.getDeltaTime();
    }

    @Override
    public boolean isFinished() {
        return animation.isAnimationFinished(stateTime);
    }

    @Override
    public void setUpdatablePartPosition(Vector2 position) {
        location.x = position.x;
        location.y = position.y;
    }

    @Override
    public void setUpdatablePartAngle(float angle) {
        location.angle = angle;
    }
}
