package ru.spbstu.gyboml;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ru.spbstu.gyboml.game.GameOffline;
public class MainActivityOffline extends AndroidApplication {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameOffline game = new GameOffline(this);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        setContentView(initializeForView(game, config));
    }
}
