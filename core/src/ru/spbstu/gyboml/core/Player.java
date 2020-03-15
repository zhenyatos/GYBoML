package ru.spbstu.gyboml.core;

public class Player {
    private boolean myTurn;
    private int numOfPoints;

    Player(int initPoints, boolean isFirst) {
        numOfPoints = initPoints;
        myTurn = isFirst;
    }

    // Passing turn, returns true if successful
    boolean passTurn(Player other) {
        if (myTurn) {
            myTurn = false;
            other.myTurn = true;
            return true;
        }
        return false;
    }

    boolean isMyTurn() {
        return myTurn;
    }
}
