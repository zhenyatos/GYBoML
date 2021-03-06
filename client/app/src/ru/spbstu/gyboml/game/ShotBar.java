package ru.spbstu.gyboml.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ShotBar {
    private ProgressBar shotPowerBar;
    private int width;
    private int height;
    private boolean increasing = true;

    private final static float min = 0.5f;
    private final static float max = 1.2f;
    private final static float step = 0.018f;

    public ShotBar(float buttonWidth) {
        width = (int)(buttonWidth / 4f);
        height = width * 3;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = drawable;

        pixmap = new Pixmap(width, 0, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knob = drawable;

        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knobBefore = drawable;

        shotPowerBar = new ProgressBar(min, max, step, true, progressBarStyle);
        shotPowerBar.setBounds(0,0, width, height);
    }

    public void update() {
        if (increasing && shotPowerBar.getValue() >= max - step)
            increasing = false;
        else if (!increasing && shotPowerBar.getValue() <= min + step)
            increasing = true;

        int direction = increasing ? 1 : -1;
        shotPowerBar.setValue(shotPowerBar.getValue() + direction * step);
    }

    public float getValue() {
        return shotPowerBar.getValue();
    }

    public void resetValue() {
        shotPowerBar.setValue(min);
    }

    public ProgressBar getShotPowerBar() {
        return shotPowerBar;
    }
}
