package main.java.ru.spbstu.gyboml;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.esotericsoftware.kryonet.Client;
import com.google.gson.Gson;

import main.java.ru.spbstu.gyboml.clientcore.GameClient;
import main.java.ru.spbstu.gyboml.clientlobby.Lobby;
import ru.spbstu.gyboml.core.Player;

public class MainActivity extends AndroidApplication {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String clientJson = getIntent().getStringExtra(Lobby.clientExtraName);
        String playerJson = getIntent().getStringExtra(Lobby.playerExtraName);

        GameClient game;
        if (clientJson != null && playerJson != null) {
            Gson jsonPacker  = new Gson();
            game = new GameClient(jsonPacker.fromJson(clientJson, Client.class), jsonPacker.fromJson(playerJson, Player.class));
        }
        else
            game = new GameClient(null, null);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(game, config);

    }
}
