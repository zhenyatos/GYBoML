package ru.spbstu.gyboml.core.destructible;

import ru.spbstu.gyboml.core.Constants;

public enum Material {
    WOOD(Constants.WOOD_DEFENCE_RATIO),
    STONE(Constants.STONE_DEFENCE_RATIO),
    GLASS(Constants.GLASS_DEFENCE_RATIO),
    NO(0.f);

    private float defenceRatio;

    Material(float defenceRatio) {
        this.defenceRatio = defenceRatio;
    }

    public float getDefenceRatio() {
        return defenceRatio;
    }
}
