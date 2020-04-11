package ru.spbstu.gyboml.core.physical;

public class Location {
    public Location(float x, float y, float angle, float scale) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.scale = scale;
    }

    float x;
    float y;
    float angle;
    float scale;
}
