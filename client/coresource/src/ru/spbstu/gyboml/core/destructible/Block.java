package ru.spbstu.gyboml.core.destructible;

public class Block extends Destructible {
    private static int BASE_HP = 100;

    public Block(Material material) {
        super((int)(BASE_HP * material.getDefenceRatio()), material);
    }
}
