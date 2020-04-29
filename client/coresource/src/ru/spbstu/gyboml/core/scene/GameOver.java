package ru.spbstu.gyboml.core.scene;

import com.badlogic.gdx.scenes.scene2d.ui.Label;


import ru.spbstu.gyboml.core.Winnable;

public class GameOver {
    private final Winnable parent;
    private final Label won1stPlayer;
    private final Label won2ndPlayer;
    private boolean firedBefore = false;
    private boolean over = false;


    public GameOver(Winnable parent, Label won1stPlayer, Label won2ndPlayer) {
        this.parent = parent;
        this.won1stPlayer = won1stPlayer;
        this.won2ndPlayer = won2ndPlayer;
        won1stPlayer.setVisible(false);
        won2ndPlayer.setVisible(false);
    }

    public void victory1st(float hp2nd) {
        if (!(hp2nd > 0) && !over) {
            parent.disableButtons();
            won1stPlayer.setVisible(true);
            over = true;
        }
    }

    public void victory2nd(float hp1st) {
        if (!(hp1st > 0) && !over) {
            parent.disableButtons();
            won2ndPlayer.setVisible(true);
            over = true;
        }
    }
}
