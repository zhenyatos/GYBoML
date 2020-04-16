package main.java.ru.spbstu.gyboml.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimatedExplosion implements Animated {
    private static final Animation<TextureRegion> ANIMATION;
    public static final float FRAME_WIDTH;
    public static final float FRAME_HEIGHT;

    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private float stateTime = 0f;

    static {
        final int EXPLOSION_COLS = 8;
        final int EXPLOSION_ROWS = 8;
        Texture explosionSheet = new Texture(Gdx.files.internal("sprites/explosion.png"));
        TextureRegion[][] tempTextureRegions = TextureRegion.split(
                explosionSheet,
                explosionSheet.getWidth() / EXPLOSION_COLS,
                explosionSheet.getHeight() / EXPLOSION_ROWS);
        TextureRegion[] explosionFrames = new TextureRegion[EXPLOSION_COLS * EXPLOSION_ROWS];
        int ind = 0;
        for (int i = 0; i < EXPLOSION_ROWS; ++i) {
            for (int j = 0; j < EXPLOSION_COLS; ++j)
                explosionFrames[ind++] = tempTextureRegions[i][j];
        }

        ANIMATION    = new Animation<TextureRegion>(0.02f, explosionFrames);
        FRAME_WIDTH  = ANIMATION.getKeyFrames()[0].getRegionWidth();
        FRAME_HEIGHT = ANIMATION.getKeyFrames()[0].getRegionHeight();
    }

    public AnimatedExplosion(float x, float y, float scale) {
        this.x = x;
        this.y = y;
        width = height = ANIMATION.getKeyFrames()[0].getRegionWidth() * scale;
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(ANIMATION.getKeyFrame(stateTime, true),  x, y, width, height);
        stateTime += Gdx.graphics.getDeltaTime();
    }

    @Override
    public boolean isFinished() {
        return ANIMATION.isAnimationFinished(stateTime);
    }
}
