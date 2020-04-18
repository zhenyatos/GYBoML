package ru.spbstu.gyboml.server.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.spbstu.gyboml.core.Player;

@Getter
@Setter
@RequiredArgsConstructor
public class Game {

    // game players
    @NonNull private Player firstPlayer;
    @NonNull private Player secondPlayer;

    // game stage
    public static enum Stage {
        FISRT_PLAYER_ATTACK,
        SECOND_PLAYER_ATTACK,
        BUILDING;

        public Stage reverted() {
            if (this == Stage.FISRT_PLAYER_ATTACK) {
                return Stage.SECOND_PLAYER_ATTACK;
            } else if (this == Stage.SECOND_PLAYER_ATTACK) {
                return Stage.FISRT_PLAYER_ATTACK;
            } else return Stage.BUILDING;
        }
    }

    // current game stager
    private Stage currentStage = Stage.FISRT_PLAYER_ATTACK;

}