package ru.spbstu.gyboml.lobby

import android.content.Intent
import android.view.View.*
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import ru.spbstu.gyboml.AndroidUtils
import ru.spbstu.gyboml.core.net.GybomlClient
import ru.spbstu.gyboml.core.net.SessionRequests
import ru.spbstu.gyboml.core.net.SessionResponses
import ru.spbstu.gyboml.multiplayer.GameActivity
import ru.spbstu.gyboml.lobby.Lobby.State.IN
import ru.spbstu.gyboml.menu.MainMenu

class SessionListener(private val lobby: Lobby) : Listener() {
    override fun disconnected(connection: Connection?) {
        AndroidUtils.showToast(lobby, "Disconnected from server")

        val intent = Intent(lobby.applicationContext, MainMenu::class.java)
        lobby.startActivity(intent)
    }

    override fun received(connection: Connection, response: Any) {
        when (response) {
            is SessionResponses.TakeSessions -> takeSessions(response)
            is SessionResponses.SessionConnected -> sessionConnected(response)
            is SessionResponses.ReadyApproved -> readyApproved(response)
            is SessionResponses.SessionCreated -> sessionCreated(response)
            is SessionResponses.SessionExited -> sessionExited()
            is SessionResponses.SessionStarted -> sessionStarted(response)
        }
    }

    private fun takeSessions(response: SessionResponses.TakeSessions) {
        lobby.runOnUiThread {
            lobby.lobbyInterface.sessionsAdapter.update(response.sessions)
            lobby.lobbyInterface.binding.swipeContainer.isRefreshing = false

            if (lobby.state == IN) {
                with (lobby.lobbyInterface) {
                    sessionsAdapter.chosenSessionId?.let { sessionId ->
                        val info = sessionsAdapter.getSession(sessionId) ?: return@runOnUiThread
                        val firstPlayerExists = info.firstPlayer  != null
                        val secondPlayerExists = info.secondPlayer != null

                        binding.firstPlayerName.text = if (firstPlayerExists) info.firstPlayer!!.name else "None"
                        binding.secondPlayerName.text = if (secondPlayerExists) info.secondPlayer!!.name else "None"
                        binding.firstPlayerReady.visibility =
                                if (firstPlayerExists && info.firstPlayer!!.ready) VISIBLE else INVISIBLE
                        binding.secondPlayerReady.visibility =
                                if (secondPlayerExists && info.secondPlayer!!.ready) VISIBLE else INVISIBLE
                    }
                }
            }
        }
    }
    private fun readyApproved(response: SessionResponses.ReadyApproved) {
        lobby.runOnUiThread {
            val ready = response.ready

            lobby.lobbyInterface.binding.ready.isChecked = ready
            lobby.lobbyInterface.binding.exit.visibility = if (ready) GONE else VISIBLE
        }
    }
    private fun sessionStarted(response: SessionResponses.SessionStarted) {
        val intent = Intent(lobby.applicationContext, GameActivity::class.java)
        intent.putExtra(GameActivity.PLAYER_PARAM_KEY, response.player)
        lobby.startActivity(intent)
    }
    private fun sessionExited() {
        lobby.runOnUiThread { lobby.out() }
    }
    private fun sessionCreated(response: SessionResponses.SessionCreated) {
        GybomlClient.sendTCP(SessionRequests.ConnectSession(response.sessionId))
        AndroidUtils.showToast(lobby, "Session created")
    }
    private fun sessionConnected(response: SessionResponses.SessionConnected) {
        lobby.runOnUiThread {
            lobby.join(response.sessionId)
        }
    }
}

