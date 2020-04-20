package ru.spbstu.gyboml.core;

/**
 * Player identifier enum.
 * It cannot be to have 2 players with same type.
 * */
public enum PlayerType {

    // first player id
    FIRST_PLAYER(0),

    // second player id
    SECOND_PLAYER(1),

    // server side
    GODFATHER(2);

    PlayerType( int id ) {
        this.id = id;
    }

    public PlayerType reverted() {
        if (this == FIRST_PLAYER) { return SECOND_PLAYER; }
        else { return FIRST_PLAYER; }
    }

    private int id;
}
