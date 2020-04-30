package ru.spbstu.gyboml.core.net

import ru.spbstu.gyboml.core.Player

class SessionResponses {
    class SessionCreated(val sessionId: Int = 0)
    class NameRegistred
    class TakeSessions(val sessions: List<SessionInfo> = listOf())
    class SessionConnected(val sessionId: Int = 0)
    class ReadyApproved(val ready: Boolean = false)
    class SessionExited

    class SessionStarted(val player: Player)
}