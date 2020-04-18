package ru.spbstu.gyboml.server.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.server.GybomlConnection;

@Getter
@Setter
@AllArgsConstructor
public class NetPlayer {
    // player object
    private Player player;
    //kryonet connection
    private GybomlConnection connection;
}