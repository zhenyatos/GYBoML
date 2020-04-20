package ru.spbstu.gyboml.server;

import com.esotericsoftware.kryonet.Connection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GybomlConnection extends Connection {
    // player's name
    private String name;

    // player's id (may be null)
    private Long playerId;

    // sessions's id (may be null if not connected to)
    private Integer sessionId;

    @Override
    public String toString() {
        return super.toString() + " " + name + "#" + playerId + "#" + sessionId;
    }
}