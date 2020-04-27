package ru.spbstu.gyboml.core.net;

import com.badlogic.gdx.math.Vector2;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.spbstu.gyboml.core.Player;

public class GameResponses {

    /**
     * Shoot request
     * Called when server accepted shoot request
     * */
    @RequiredArgsConstructor
    @NoArgsConstructor
    public static class Shooted {
        public @NonNull Vector2 ballPostition;
        public @NonNull Vector2 ballVelocity;
    }

    /**
     * Pass turn response
     * Called when server accepted pass turn
     * */
    @RequiredArgsConstructor
    @NoArgsConstructor
    public static class PassTurned {

        // is it still your turn (if you cant pass turn now), or turn successfully passed
        // and now it it opponent's turn
        public @NonNull boolean yourTurn;
    }

    /**
     * Game exited message
     * Send when game finished or one of the players
     */
    public static class GameExited {
    }

}
