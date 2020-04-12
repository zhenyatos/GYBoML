package ru.spbstu.gyboml.core.physical;

public enum Type {
    BLOCK(2), SHOT(3), BACKGROUND(5);

    private int id;
    Type(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
