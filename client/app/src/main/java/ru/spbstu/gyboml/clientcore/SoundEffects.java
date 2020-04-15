package main.java.ru.spbstu.gyboml.clientcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundEffects {
    public final Sound shot;
    public final Sound wood;
    private static SoundEffects instance = null;

    private SoundEffects() {
        shot = Gdx.audio.newSound(Gdx.files.internal("sound/shot.mp3"));
        wood = Gdx.audio.newSound(Gdx.files.internal("sound/wood.mp3"));
    }

    public static synchronized SoundEffects get() {
        if (instance == null) {
            instance = new SoundEffects();
        }
        return instance;
    }
}
