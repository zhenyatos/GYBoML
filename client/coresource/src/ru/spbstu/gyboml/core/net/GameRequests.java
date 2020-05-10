package ru.spbstu.gyboml.core.net;

import com.badlogic.gdx.math.Vector2;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class GameRequests {

    /**
     * Shoot request
     * Called when user pushed 'shoot' button
     * */
    @RequiredArgsConstructor
    @NoArgsConstructor
    public static class Shoot {
        public @NonNull Vector2 ballPosition;
        public @NonNull Vector2 ballVelocity;
    }

    /**
     * Exit game request
     * Called when one of player exited game via button
     * */
    public static class GameExit {
    }
}
