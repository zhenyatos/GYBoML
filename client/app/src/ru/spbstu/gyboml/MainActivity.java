package ru.spbstu.gyboml;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ru.spbstu.gyboml.game.Game;

import ru.spbstu.gyboml.lobby.Lobby;

public class MainActivity extends AndroidApplication {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String clientJson = getIntent().getStringExtra(Lobby.clientExtraName);
        String playerJson = getIntent().getStringExtra(Lobby.playerExtraName);

        Game game = new Game();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        setContentView(initializeForView(game, config));
    }

    @Override
    public void onBackPressed() {
        // doing nothing
    }
}
