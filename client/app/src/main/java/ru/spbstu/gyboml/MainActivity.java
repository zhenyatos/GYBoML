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

        FrameLayout fl = new FrameLayout(this);
        LayoutInflater inflater = getLayoutInflater();
        View uiView = inflater.inflate(R.layout.ui, null, false);
        View mainView = initializeForView(new GameClient(uiView), config);
        fl.addView(mainView);
        fl.addView(uiView);
        setContentView(fl);
    }
}
