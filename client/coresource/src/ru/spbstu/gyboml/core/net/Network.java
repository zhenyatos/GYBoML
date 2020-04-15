package ru.spbstu.gyboml.core.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;


/*
 * Common network settings between client and server.
 */
public class Network {

    // server ports
    public static final int tcpPort = 4445;
    public static final int udpPort = 3335;

    // register request/response type for kryo serialization
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(Requests.RegisterName.class);
        kryo.register(Requests.ConnectLobby.class);
        kryo.register(Requests.CreateLobby.class);
        kryo.register(Responses.LobbyCreated.class);
        kryo.register(Responses.ServerError.class);
        kryo.register(String[].class);
    }
}
