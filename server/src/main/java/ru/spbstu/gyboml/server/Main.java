package ru.spbstu.gyboml.server;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import ru.spbstu.gyboml.core.net.*;
import ru.spbstu.gyboml.core.scene.PhysicalScene;
import ru.spbstu.gyboml.server.game.GameListener;
import ru.spbstu.gyboml.server.session.Session;
import ru.spbstu.gyboml.server.session.SessionListener;
import static com.esotericsoftware.minlog.Log.*;


/**
 * Server main class
 *
 */
public class Main
{
    // kryonet server object
    public Server server;
    PhysicalScene scene = new PhysicalScene(null);

    // mapping from int to session with same id
    public Map<Integer, Session> sessionMap;

    private static long nextAvailablePlayerId = 0;

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
        server.addListener(new GameListener(this));

        try {
            server.bind(Network.tcpPort/*TODO: , Network.udpPort*/);
        } catch (IOException ex) {
            info("Could not open socket", ex);
            System.exit(2);
        }
    }

    private void start() {
        server.start();

        while (true) {
            scene.stepWorld();
        }
    }

    public static void main(final String[] args) throws IOException {
        Main main = new Main();
        Log.setLogger(new GybomlLogger());

        try {
            info("GYBoML server started");
            main.start();
            info("GYBoML server finished");
        } catch (Error | Exception error) {
            error("Error occured in main thread", error);
            main.server.close();
            System.exit(2);
        }
    }

    public static long nextAvailablePlayerId() { return nextAvailablePlayerId++; }
}
