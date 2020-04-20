package ru.spbstu.gyboml.core.net;

public class GameResponses {

    /**
     * Shoot request
     * Called when server accepted shoot request
     * */
    public static class Shooted {
        public Float ballPositionX;
        public Float ballPositionY;

        public Float ballVelocityX;
        public Float ballVelocityY;
    }

    /**
     * Pass turn response
     * Called when server accepted pass turn
     * */
    public static class PassTurned {

        // is it still your turn (if you cant pass turn now), or turn successfully passed
        // and now it it opponent's turn
        public boolean yourTurn;
    }
}
