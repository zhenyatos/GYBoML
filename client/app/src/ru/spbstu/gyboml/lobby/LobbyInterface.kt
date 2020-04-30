package ru.spbstu.gyboml.lobby

import android.view.View.*
import androidx.recyclerview.widget.RecyclerView
import ru.spbstu.gyboml.databinding.ActivityLobbyBinding

class LobbyInterface(val binding: ActivityLobbyBinding) {
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var sessionsAdapter: ButtonAdapter

    // different view models
    fun inSession() = with(binding) {
        inSessionLayout.visibility = VISIBLE
        ready.visibility = VISIBLE
        exit.visibility = VISIBLE

        gameSessions.visibility = INVISIBLE
        createSession.visibility = GONE
    }
    fun outSession() = with(binding) {
        gameSessions.visibility = VISIBLE
        createSession.visibility = VISIBLE

        inSessionLayout.visibility = INVISIBLE
        ready.visibility = GONE
        exit.visibility = GONE
    }
}