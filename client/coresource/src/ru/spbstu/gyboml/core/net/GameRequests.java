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
     * Exit game request
     * Called when one of player exited game via button
     * */
    public static class GameExit {
    }
}
