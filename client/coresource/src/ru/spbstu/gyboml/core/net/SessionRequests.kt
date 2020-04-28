package ru.spbstu.gyboml.core.net

class SessionRequests {
    class RegisterName(val name: String = "default_user")

    class CreateSession(val sessionName: String = "default_session")
    class ConnectSession(val sessionId: Int = 0)
    class ExitSession
    class GetSessions

    class Ready
}