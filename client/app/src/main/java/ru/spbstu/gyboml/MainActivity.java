package main.java.ru.spbstu.gyboml;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import main.java.ru.spbstu.gyboml.clientcore.GameClient;

import main.java.ru.spbstu.gyboml.clientlobby.Lobby;

public class MainActivity extends AndroidApplication {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        setContentView(initializeForView(new GameClient(), config));
    }
}
