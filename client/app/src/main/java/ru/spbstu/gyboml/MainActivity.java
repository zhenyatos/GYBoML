package ru.spbstu.gyboml;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

<<<<<<< HEAD
import main.java.ru.spbstu.gyboml.clientcore.GameClient;
=======
import ru.spbstu.gyboml.clientcore.GameClient;
>>>>>>> Migrate from maven to gradle

public class MainActivity extends AndroidApplication {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new GameClient(), config);
    }
}
