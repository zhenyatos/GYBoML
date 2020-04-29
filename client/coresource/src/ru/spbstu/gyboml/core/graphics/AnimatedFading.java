package ru.spbstu.gyboml.core.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimatedFading implements Animated {
    private final Sprite sprite;
    private final float maxAlpha;
    private final float minAlpha;
    private final float stepAlpha;
    private boolean fading = true;
    private boolean looped = false;

    public AnimatedFading(Sprite sprite, float stepAlpha) {
        this.sprite    = sprite;
        this.stepAlpha = stepAlpha;
        this.maxAlpha  = 1.0f;
        this.minAlpha  = 0.0f;
    }

    public AnimatedFading(Sprite sprite, float stepAlpha, float maxAlpha, float minAlpha, boolean looped) {
        this.sprite    = sprite;
        this.stepAlpha = stepAlpha;
        this.maxAlpha  = maxAlpha;
        this.minAlpha  = minAlpha;
        this.looped    = looped;
    }

    public void setLooped(boolean looped) {
        this.looped = looped;
    }

    @Override
    public void draw(Batch batch) {
        if (fading && sprite.getColor().a <= minAlpha + stepAlpha)
            fading = false;
        else if (!fading && sprite.getColor().a >= maxAlpha - stepAlpha)
            fading = true;

        int direction = fading ? -1 : 1;
        sprite.draw(batch);
        sprite.setAlpha(sprite.getColor().a + direction * stepAlpha);
    }

    @Override
    public boolean isFinished() {
        if (looped)
            return false;
        return (fading && sprite.getColor().a <= minAlpha + stepAlpha);
    }
}
