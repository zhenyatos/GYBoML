package ru.spbstu.gyboml.core.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.destructible.Material;

public class SoundEffects {
    private final Sound shot;
    private final Sound loadShot;
    private final Sound woodImpact;
    private final Sound woodBroken;
    private final Sound stoneImpact;
    private final Sound stoneBroken;
    private final Sound p1Turn;
    private final Sound p2Turn;
    private final Sound armory;
    private float effectsVolume = 1.0f;

    public SoundEffects() {
        shot = Gdx.audio.newSound(Gdx.files.internal("sound/shot.mp3"));
        loadShot = Gdx.audio.newSound(Gdx.files.internal("sound/load_shot.wav"));
        woodImpact = Gdx.audio.newSound(Gdx.files.internal("sound/wood_impact.wav"));
        woodBroken = Gdx.audio.newSound(Gdx.files.internal("sound/wood_broken.wav"));
        stoneImpact = Gdx.audio.newSound(Gdx.files.internal("sound/stone_impact.wav"));
        stoneBroken = Gdx.audio.newSound(Gdx.files.internal("sound/stone_broken.wav"));
        p1Turn = Gdx.audio.newSound(Gdx.files.internal("sound/coin_p1_turn.wav"));
        p2Turn = Gdx.audio.newSound(Gdx.files.internal("sound/coin_p2_turn.wav"));
        armory = Gdx.audio.newSound(Gdx.files.internal("sound/armory.wav"));
    }

    public void setEffectsVolume(float effectsVolume) {
        this.effectsVolume = effectsVolume;
    }

    public float getEffectsVolume() {
        return this.effectsVolume;
    }

    public void playShot() { shot.play(effectsVolume); }

    public void playLoadShot() { loadShot.play(effectsVolume); }

    public void playImpact(Material material) {
        switch (material) {
            case WOOD:
                synchronized (woodImpact) {
                    woodImpact.play(effectsVolume);
                }
                break;
            case STONE:
                synchronized (stoneImpact) {
                    stoneImpact.play(effectsVolume);
                }
        }
    }

    public void playBroken(Material material) {
        switch (material) {
            case WOOD:
                synchronized (woodBroken) {
                    woodBroken.play(effectsVolume);
                }
                break;
            case STONE:
                synchronized (stoneBroken) {
                    stoneBroken.play(effectsVolume);
                }
        }
    }

    public void playPlayerTurn(PlayerType playerType) {
        if (playerType == PlayerType.FIRST_PLAYER)
            p1Turn.play(effectsVolume);
        else if (playerType == PlayerType.SECOND_PLAYER)
            p2Turn.play(effectsVolume);
    }

    public void playArmory() {
        armory.play(effectsVolume);
    }
}
