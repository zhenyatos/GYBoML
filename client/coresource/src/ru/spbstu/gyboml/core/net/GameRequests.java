package ru.spbstu.gyboml.core.net;

public class GameRequests {

    /**
     * Shoot request
     * Called when user pushed 'shoot' button
     * */
    public static class Shoot {
        // angle of cannon at the moment when button 'shoot' pushed
        public float angle;
    }

    /**
     * Pass turn request
     * Called when user pushed 'pass turn' button
     * */
    public static class PassTurn {
    }
}
