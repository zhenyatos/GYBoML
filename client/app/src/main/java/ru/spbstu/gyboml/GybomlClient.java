package main.java.ru.spbstu.gyboml;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.net.Network;

/**
 * All client network operations class
 * Consists of static fields, common for all activitites
 * */
public class GybomlClient {
    // network common for all activities objects
    private static Client client = null;
    private static Player player = null;
    private static PlayerType playerType = null;
    /**
     * Connect to server static method
     * If connection was already established, it will be reseted
     * @param listener - listener, that will be added to client
     * */
    public static void connect(Listener listener) {

        // reset connection if needed
        if (client != null) {client.close();}

        // create new client
        client = new Client();
        client.start();

        // register requests and response types
        Network.register(client);

        // add new listener to client
        client.addListener(listener);

        // run client in separate thread
        new Thread("ClientThread") {
            @Override
            public void run() {
                try { client.connect(5000, Network.serverAddress, Network.tcpPort /*, Network.udpPort*/); }
                catch (IOException error) { error.printStackTrace(); client.close(); }
            }
        }.start();
    }

    /**
     * Disconnect from server (close sockets) static method
     * */
    public static void disconnect() {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    /**
     * Send any object, registered with Network.registerTypes
     * in separate Thread
     * */
    public static void sendTCP( Object object ) {
        new Thread("Handle") {
            public void run() { client.sendTCP(object); }
        }.start();
    }

    public static void setPlayer(Player player) {
        GybomlClient.player = player;
    }

    public static void setPlayerType(PlayerType type) {
        GybomlClient.playerType = type;
    }

    public static Player getPlayer() {return player;}
    public static PlayerType getPlayerType() {return playerType;}
    public static Client getClient() {return client;}
}
