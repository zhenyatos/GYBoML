package ru.spbstu.gyboml.core.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimatedExplosion implements Animated {
    private final Animation<TextureRegion> animation;
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private float stateTime = 0f;

    public AnimatedExplosion(Animation<TextureRegion> animation, float x, float y, float scale) {
        this.animation = animation;
        this.x = x;
        this.y = y;
        width = height = animation.getKeyFrames()[0].getRegionWidth() * scale;
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(animation.getKeyFrame(stateTime, true),  x, y, width, height);
        stateTime += Gdx.graphics.getDeltaTime();
    }

    @Override
    public boolean isFinished() {
        return animation.isAnimationFinished(stateTime);
    }
}
