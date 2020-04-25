package ru.spbstu.gyboml.core.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.ArrayList;
import java.util.List;

import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.net.SessionRequests.ExitSession;
import ru.spbstu.gyboml.core.net.SessionRequests.GetSessions;
import ru.spbstu.gyboml.core.net.SessionRequests.Ready;


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
        kryo.register(String[].class);
        kryo.register(List.class);
        kryo.register(ArrayList.class);
        kryo.register(Player.class);
        kryo.register(PlayerType.class);
        kryo.register(Integer.class);
        kryo.register(Float.class);

        kryo.register(SessionRequests.RegisterName.class);
        kryo.register(SessionRequests.ConnectSession.class);
        kryo.register(SessionRequests.CreateSession.class);
        kryo.register(SessionInfo.class);
        kryo.register(GetSessions.class);
        kryo.register(Ready.class);
        kryo.register(ExitSession.class);
        kryo.register(ExitSession.class);
        kryo.register(GameRequests.GameExit.class);
        kryo.register(GameRequests.Shoot.class);

        kryo.register(SessionResponses.ReadyApproved.class);
        kryo.register(SessionResponses.SessionExited.class);
        kryo.register(SessionResponses.SessionCreated.class);
        kryo.register(SessionResponses.SessionConnected.class);
        kryo.register(SessionResponses.ServerError.class);
        kryo.register(SessionResponses.TakeSessions.class);
        kryo.register(SessionResponses.SessionStarted.class);
        kryo.register(GameResponses.Shooted.class);
        kryo.register(GameResponses.GameExited.class);
        kryo.register(GameResponses.PassTurned.class);
    }
}
