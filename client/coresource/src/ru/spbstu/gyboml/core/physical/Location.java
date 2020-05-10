package ru.spbstu.gyboml.core.physical;

public class Location {
    public Location(float x, float y, float angle, float scale) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.scale = scale;
    }

    public Location(Location location) {
        this.x = location.x;
        this.y = location.y;
        this.angle = location.angle;
        this.scale = location.scale;
    }

    public float x;
    public float y;
    public float angle;
    public float scale;
}
