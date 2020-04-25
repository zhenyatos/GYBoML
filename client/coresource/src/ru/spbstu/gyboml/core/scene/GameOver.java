package ru.spbstu.gyboml.core.scene;

import com.badlogic.gdx.scenes.scene2d.ui.Label;


import ru.spbstu.gyboml.core.Winnable;

public class GameOver {
    private final Winnable parent;
    private final Label victoryLabel;
    private final Label defeatLabel;
    private boolean firedBefore = false;


    public GameOver(Winnable parent, Label victoryLabel, Label defeatLabel) {
        this.parent = parent;
        this.victoryLabel = victoryLabel;
        this.defeatLabel = defeatLabel;
        victoryLabel.setVisible(false);
        defeatLabel.setVisible(false);
    }

    public void victoryCheck(float newHP) {
        if (!firedBefore && !(newHP > 0)) {
            parent.disableButtons();
            victoryLabel.setVisible(true);
        }
    }

    public void defeatCheck(float newHP) {
        if (!firedBefore && !(newHP > 0)) {
            parent.disableButtons();
            defeatLabel.setVisible(true);
        }
    }
}
