package ru.spbstu.gyboml.core.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class HPBar {
    private ProgressBar healthBar;
    private float basicHP;
    public static final float width = 290;
    public static final float height = 20;
    public static final float animateDuration = 0.25f;

    public HPBar(float basicHP) {
        this.basicHP = basicHP;
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(
                new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = drawable;

        pixmap = new Pixmap(0, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        drawable = new TextureRegionDrawable(
                new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        progressBarStyle.knob = drawable;

        pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        drawable = new TextureRegionDrawable(
                new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        progressBarStyle.knobBefore = drawable;

        healthBar = new ProgressBar(0.f, 1.f, 0.01f, false, progressBarStyle);
        healthBar.setValue(1.f);
        healthBar.setAnimateDuration(animateDuration);
        healthBar.setBounds(0,0, width, height);
    }

    public void update(float newHP) {
        healthBar.setValue(newHP / basicHP);
    }

    public ProgressBar getHealthBar() {
        return healthBar;
    }
}
