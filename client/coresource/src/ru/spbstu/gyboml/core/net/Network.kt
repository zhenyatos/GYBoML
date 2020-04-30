package ru.spbstu.gyboml.core.net

import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.kryonet.EndPoint
import ru.spbstu.gyboml.core.Player
import ru.spbstu.gyboml.core.PlayerType
import ru.spbstu.gyboml.core.destructible.Material
import ru.spbstu.gyboml.core.net.SessionRequests.*
import ru.spbstu.gyboml.core.shot.ShotType
import java.util.*

object Network {
    const val tcpPort = 4445
    const val udpPort = 3335

    const val address = "192.168.1.177"//"34.91.65.96"

    fun register(endpoint: EndPoint) {
        with (endpoint.kryo) {
            register(Array<String>::class.java)
            register(List::class.java)
            register(ArrayList::class.java)
            register(Player::class.java)
            register(PlayerType::class.java)
            register(Integer::class.java)
            register(Float::class.java)
            register(Vector2::class.java)
            register(SessionPlayer::class.java)

            register(SessionRequests.RegisterName::class.java)
            register(SessionRequests.ConnectSession::class.java)
            register(SessionRequests.CreateSession::class.java)
            register(SessionInfo::class.java)
            register(GetSessions::class.java)
            register(Ready::class.java)
            register(ExitSession::class.java)
            register(ExitSession::class.java)
            register(GameRequests.GameExit::class.java)
            register(GameRequests.GameLoaded::class.java)

            register(SessionResponses.ReadyApproved::class.java)
            register(SessionResponses.SessionExited::class.java)
            register(SessionResponses.SessionCreated::class.java)
            register(SessionResponses.SessionConnected::class.java)
            register(SessionResponses.TakeSessions::class.java)
            register(SessionResponses.NameRegistred::class.java)
            register(SessionResponses.SessionStarted::class.java)
            register(GameResponses.GameExited::class.java)
            register(GameResponses.PassTurned::class.java)
            register(GameResponses.GameStarted::class.java)

            register(GameMessage.CreateShot::class.java)
            register(GameMessage.UpdateShot::class.java)
            register(GameMessage.RemoveShot::class.java)

            register(GameMessage.CreateBlock::class.java)
            register(GameMessage.UpdateBlock::class.java)
            register(GameMessage.RemoveBlock::class.java)

            register(Material::class.java)
            register(ShotType::class.java)
        }
    }
}