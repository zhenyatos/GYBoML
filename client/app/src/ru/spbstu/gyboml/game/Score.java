package ru.spbstu.gyboml.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import ru.spbstu.gyboml.core.Player;

public class Score {
    public static final float width = 290;
    public static final float height = 20;

    private Label text;
    private int points;

    public Score(int points, Color color) {
        Label.LabelStyle textStyle = new Label.LabelStyle();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        textStyle.font = font;
        textStyle.fontColor = color;


        text = new Label(Integer.toString(points), textStyle);
        text.setBounds(0, 0f, width, height);
        text.setFontScale(1f, 1f);
        this.points = points;
    }

    public void changeValue(int points) {
        this.points = points;
        text.setText(points);
    }

    public Label getText() {
        return text;
    }
}
