package ru.spbstu.gyboml.core.shot;

public enum ShotType {
    BASIC("basic");

    private String name;

    ShotType(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}
