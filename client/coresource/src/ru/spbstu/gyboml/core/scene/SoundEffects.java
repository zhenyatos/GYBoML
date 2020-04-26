package ru.spbstu.gyboml.core.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundEffects {
    private Sound shot;
    private Sound wood;
    private Sound p1Turn;
    private Sound p2Turn;
    private float effectsVolume = 1.0f;

    private static SoundEffects instance = null;

    private SoundEffects() {
        shot = Gdx.audio.newSound(Gdx.files.internal("sound/shot.mp3"));
        wood = Gdx.audio.newSound(Gdx.files.internal("sound/wood.mp3"));
        p1Turn = Gdx.audio.newSound(Gdx.files.internal("sound/coin_p1_turn.wav"));
        p2Turn = Gdx.audio.newSound(Gdx.files.internal("sound/coin_p2_turn.wav"));
    }

    public static synchronized SoundEffects get() {
        if (instance == null) {
            instance = new SoundEffects();
        }
        return instance;
    }

    public void setEffectsVolume(float effectsVolume) {
        this.effectsVolume = effectsVolume;
    }

    public void playShot() { shot.play(effectsVolume);}
    public void playWood() {
        wood.play(effectsVolume);
    }
    public void playP1Turn() { p1Turn.play(effectsVolume); }
    public void playP2Turn() { p2Turn.play(effectsVolume); }
}
