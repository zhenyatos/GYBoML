package ru.spbstu.gyboml.core;

/**
 * Class represents player in game.
 * */
public class Player {

    // is now my turn
    private boolean isTurn;

    // current number of points
    private int points;

    /**
     * Class constructor.
     * @param initialPoints - initial number of points
     * @param isTurn - initial turn
     * */
    public Player(int initialPoints, boolean isTurn) {
        points = initialPoints;
        this.isTurn = isTurn;
    }

    /**
     * Pass turn to other player.
     * @param other - link to other player
     * @return true if turn passed, false otherwise
     * */
    public boolean passTurn(Player other) {
        if (isTurn) {
            isTurn = false;
            other.isTurn = true;
            return true;
        }
        return false;
    }

    /**
     * @return true if it is my turn, false otherwise
     * */
    public boolean isMyTurn() {
        return isTurn;
    }
}
