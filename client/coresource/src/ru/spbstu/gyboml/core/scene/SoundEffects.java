package ru.spbstu.gyboml.core.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import ru.spbstu.gyboml.core.PlayerType;

public class SoundEffects {
    private Sound shot;
    private Sound wood;
    private Sound woodBroken;
    private Sound p1Turn;
    private Sound p2Turn;
    private float effectsVolume = 1.0f;

    public SoundEffects() {
        shot = Gdx.audio.newSound(Gdx.files.internal("sound/shot.mp3"));
        wood = Gdx.audio.newSound(Gdx.files.internal("sound/wood.mp3"));
        woodBroken = Gdx.audio.newSound(Gdx.files.internal("sound/wood_broken.wav"));
        p1Turn = Gdx.audio.newSound(Gdx.files.internal("sound/coin_p1_turn.wav"));
        p2Turn = Gdx.audio.newSound(Gdx.files.internal("sound/coin_p2_turn.wav"));
    }

    public void setEffectsVolume(float effectsVolume) {
        this.effectsVolume = effectsVolume;
    }

    public void playShot() { shot.play(effectsVolume); }
    public void playWood() {
        wood.play(effectsVolume);
    }
    public void playWoodBroken() { woodBroken.play(effectsVolume); }
    public void playPlayerTurn(PlayerType playerType) {
        if (playerType == PlayerType.FIRST_PLAYER)
            p1Turn.play(effectsVolume);
        else
            p2Turn.play(effectsVolume);
    }
}
