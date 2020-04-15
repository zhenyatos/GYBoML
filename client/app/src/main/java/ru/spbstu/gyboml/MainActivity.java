package main.java.ru.spbstu.gyboml;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import main.java.ru.spbstu.gyboml.clientcore.GameClient;
import ru.spbstu.gyboml.R;

import android.view.View;
import android.widget.FrameLayout;
import android.view.LayoutInflater;


public class MainActivity extends AndroidApplication {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        setContentView(initializeForView(new GameClient(), config));
    }
}
