package ru.spbstu.gyboml.core.net;

public class GameRequests {

    /**
     * Shoot request
     * Called when user pushed 'shoot' button
     * */
    public static class Shoot {
        public Float ballPositionX;
        public Float ballPositionY;

        public Float ballVelocityX;
        public Float ballVelocityY;
    }

    /**
     * Pass turn request
     * Called when user pushed 'pass turn' button
     * */
    public static class PassTurn {
    }
}
