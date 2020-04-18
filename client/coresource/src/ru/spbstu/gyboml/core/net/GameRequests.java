package ru.spbstu.gyboml.core.net;

public class GameRequests {

    /**
     * Shoot request
     * Called when user pushed 'shoot' button
     * */
    public static class Shoot {
        public float ballPositionX;
        public float ballPositionY;

        public float ballVelocityX;
        public float ballVelocityY;
    }

    /**
     * Pass turn request
     * Called when user pushed 'pass turn' button
     * */
    public static class PassTurn {
    }
}
