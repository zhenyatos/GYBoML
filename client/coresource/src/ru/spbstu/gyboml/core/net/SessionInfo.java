package ru.spbstu.gyboml.core.net;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.spbstu.gyboml.core.Player;

/**
 * Class repersents one session lobby menu
 * Only for serialization and transfer between server and client.
 * */
@AllArgsConstructor
@NoArgsConstructor
public class SessionInfo {

    // id of related server session
    public @NonNull Integer sessionId;

    // number of free spaces in lobby
    public @NonNull Integer spaces;

    // lobby name
    public @NonNull String name;

    // players
    public Player firstPlayer;
    public Player secondPlayer;

    // is it started
    public @NonNull boolean isStarted;
}
