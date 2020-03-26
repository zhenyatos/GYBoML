package ru.spbstu.gyboml.core;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class MainTest {
    public static void main (String[] arg) {
        //System.setProperty("user.name","\\xD0\\x90\\xD1\\x80\\xD1\\x81\\xD0\\xB5\\xD0\\xBD"); //На случай, если имя пользователя в винде написано кириллицей
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.x = config.y = 0;
        new LwjglApplication(new TestClient(), config);
    }
}
