package ru.spbstu.gyboml.server;

import com.esotericsoftware.kryonet.Connection;

public class GybomlConnection extends Connection {
    // player's name
    private String name;

    // player's id (may be null)
    private Long id;

    // sessions's id (may be null if not connected to)
    private Integer sessionId;

    // getters
    public String  name() { return this.name; }
    public Long id() { return this.id; }
    public Integer sessionId() { return this.sessionId; }

    // setters
    public void setName(String name) { this.name = name; }
    public void setId(Long id) { this.id = id; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

    // log new connection
    public GybomlConnection() {
    }
}