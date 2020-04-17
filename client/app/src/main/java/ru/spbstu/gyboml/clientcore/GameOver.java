package main.java.ru.spbstu.gyboml.clientcore;

import com.badlogic.gdx.scenes.scene2d.ui.Label;


import ru.spbstu.gyboml.core.destructible.DestructionListener;

public class GameOver {
    private final GameClient parent;
    private final Label victoryLabel;
    private final Label defeatLabel;
    private boolean firedBefore = false;


    GameOver(GameClient parent, Label victoryLabel, Label defeatLabel) {
        this.parent = parent;
        this.victoryLabel = victoryLabel;
        this.defeatLabel = defeatLabel;
        victoryLabel.setVisible(false);
        defeatLabel.setVisible(false);
    }

    public DestructionListener victoryListener() {
        return new DestructionListener() {
            @Override
            public void destructionOccured(float newHP) {
                if (!firedBefore && !(newHP > 0)) {
                    parent.disableButtons();
                    victoryLabel.setVisible(true);
                }
            }
        };
    }

    public DestructionListener defeatListener() {
        return new DestructionListener() {
            @Override
            public void destructionOccured(float newHP) {
                if (!firedBefore && !(newHP > 0)) {
                    parent.disableButtons();
                    defeatLabel.setVisible(true);
                }
            }
        };
    }
}
