package ru.spbstu.gyboml.server;

import com.esotericsoftware.kryonet.Connection;

public class GybomlConnection extends Connection {
    // player's name
    private String name;

    // getters
    public String name() { return this.name; }

    // setters
    public void setName(String name) { this.name = name; }


    // log new connection
    public GybomlConnection() {
        System.out.println("New connection");
    }
}