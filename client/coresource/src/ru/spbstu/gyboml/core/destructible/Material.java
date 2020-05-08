package ru.spbstu.gyboml.core.destructible;

public enum Material {
    WOOD(0.3f, "wood"),
    STONE(0.6f, "stone");

    private float defenceRatio;
    private String name;

    Material(float defenceRatio, String name) {
        this.defenceRatio = defenceRatio;
        this.name = name;
    }

    public float getDefenceRatio() {
        return defenceRatio;
    }

    public String getName() { return name; }
}
