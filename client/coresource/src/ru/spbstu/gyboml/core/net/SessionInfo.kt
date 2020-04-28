package ru.spbstu.gyboml.core.net

data class SessionInfo(val id: Int                       = 0,
                       val spaces: Int                   = 0,
                       val name: String                  = "default_session",
                       val firstPlayer: SessionPlayer?   = null,
                       val secondPlayer: SessionPlayer?  = null,
                       val isStarted: Boolean            = false)