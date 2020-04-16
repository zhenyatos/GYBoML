package ru.spbstu.gyboml.core.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import ru.spbstu.gyboml.core.net.Requests.ExitSession;
import ru.spbstu.gyboml.core.net.Requests.GetSessions;
import ru.spbstu.gyboml.core.net.Requests.Ready;


/*
 * Common network settings between client and server.
 */
public class Network {

    // server ports
    public static final int tcpPort = 4445;
    public static final int udpPort = 3335;

    // server address
    public static final String serverAddress = "34.91.65.96";

    // register request/response type for kryo serialization
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(Requests.RegisterName.class);
        kryo.register(Requests.ConnectSession.class);
        kryo.register(Requests.CreateSession.class);
        kryo.register(Responses.SessionCreated.class);
        kryo.register(Responses.SessionConnected.class);
        kryo.register(Responses.ServerError.class);
        kryo.register(SessionInfo.class);
        kryo.register(String[].class);
        kryo.register(GetSessions.class);
        kryo.register(Ready.class);
        kryo.register(ExitSession.class);
        kryo.register(Responses.ReadyApproved.class);
        kryo.register(Responses.SessionExited.class);
    }
}
