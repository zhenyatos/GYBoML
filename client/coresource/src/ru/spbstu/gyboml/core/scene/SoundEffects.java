package ru.spbstu.gyboml.core.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundEffects {
    public final Sound shot;
    private Sound wood;
    private Sound p1Turn;
    private Sound p2Turn;

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

    public void playWood() {
        wood.play(1.f);
    }
    public void playP1Turn() { p1Turn.play(); }
    public void playP2Turn() { p2Turn.play(); }
}
