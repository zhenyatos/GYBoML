package ru.spbstu.gyboml.server;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import ru.spbstu.gyboml.core.net.*;

/**
 * Server main class 
 *
 */
public class Main 
{
    // kryonet server object
    Server server;

    // mapping from int to session with same id
    Map<Integer, Session> sessionMap;

    private Main() throws IOException {

        // init fields
        sessionMap = new TreeMap<>();

        // bootstrap server
        server = new Server() {
            protected Connection newConnection() {
                return new GybomlConnection();
            }
        };
        Network.register(server);
        server.addListener(new SessionListener(this));
        server.bind(Network.tcpPort, Network.udpPort);
    }

    private void start() { 
        server.start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("control> ");
            String line = scanner.nextLine();

            if (line.equals("q")) {
                server.stop();
                scanner.close();
                break;
            }
        }
    }

    public static void main(final String[] args) throws IOException {
        new Main().start();
    }
}
