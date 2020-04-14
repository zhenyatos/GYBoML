package main.java.ru.spbstu.gyboml.clientcore;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ru.spbstu.gyboml.core.destructible.DestructionListener;

public class HPBar implements DestructionListener {
    private ProgressBar healthBar;
    private int basicHP;

    public HPBar(int basicHP) {
        this.basicHP = basicHP;
        Pixmap pixmap = new Pixmap(100, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(
                new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = drawable;

        pixmap = new Pixmap(0, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        drawable = new TextureRegionDrawable(
                new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        progressBarStyle.knob = drawable;

        pixmap = new Pixmap(100, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        drawable = new TextureRegionDrawable(
                new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        progressBarStyle.knobBefore = drawable;

        healthBar = new ProgressBar(0.f, 1.f, 0.01f, false, progressBarStyle);
        healthBar.setValue(1.f);
        healthBar.setAnimateDuration(0.25f);
        healthBar.setBounds(0,0, 290, 20);
    }

    @Override
    public void destructionOccured(int newHP) {
        healthBar.setValue((float)newHP / basicHP);
    }

    public ProgressBar getHealthBar() {
        return healthBar;
    }
}
