package main.java.ru.spbstu.gyboml;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.esotericsoftware.kryonet.Client;
import com.google.gson.Gson;

import main.java.ru.spbstu.gyboml.clientcore.GybomlGame;

import main.java.ru.spbstu.gyboml.clientlobby.Lobby;
import ru.spbstu.gyboml.core.Player;

public class MainActivity extends AndroidApplication {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String clientJson = getIntent().getStringExtra(Lobby.clientExtraName);
        String playerJson = getIntent().getStringExtra(Lobby.playerExtraName);

        GybomlGame game = new GybomlGame();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        setContentView(initializeForView(game, config));
    }
}
