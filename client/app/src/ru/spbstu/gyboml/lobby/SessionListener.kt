package ru.spbstu.gyboml.lobby

import android.content.Intent
import android.view.Gravity
import android.view.View.*
import android.widget.Toast
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import ru.spbstu.gyboml.GybomlClient
import ru.spbstu.gyboml.core.net.SessionRequests
import ru.spbstu.gyboml.core.net.SessionResponses
import ru.spbstu.gyboml.game.GameActivity
import ru.spbstu.gyboml.lobby.Lobby.State.IN
import ru.spbstu.gyboml.menu.MainMenu

class SessionListener(private val lobby: Lobby) : Listener() {
    override fun disconnected(connection: Connection?) {
        lobby.runOnUiThread {
            val toast = Toast.makeText(lobby, "Disconnected from server", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
            toast.show()
        }

        val intent = Intent(lobby.applicationContext, MainMenu::class.java)
        lobby.startActivity(intent)
    }

    override fun received(connection: Connection, response: Any) {
        when (response) {
            is SessionResponses.TakeSessions -> takeSessions(connection, response)
            is SessionResponses.SessionConnected -> sessionConnected(connection, response)
            is SessionResponses.ReadyApproved -> readyApproved(connection, response)
            is SessionResponses.SessionCreated -> sessionCreated(connection, response)
            is SessionResponses.SessionExited -> sessionExited(connection, response)
            is SessionResponses.SessionStarted -> sessionStarted(connection, response)
        }
    }

    private fun takeSessions(connection: Connection, response: SessionResponses.TakeSessions) {
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
    private fun readyApproved(connection: Connection, response: SessionResponses.ReadyApproved) {
        lobby.runOnUiThread {
            val ready = response.ready

            lobby.lobbyInterface.binding.ready.isChecked = ready
            lobby.lobbyInterface.binding.exit.visibility = if (ready) GONE else VISIBLE
        }
    }
    private fun sessionStarted(connection: Connection, response: SessionResponses.SessionStarted) {
        val intent = Intent(lobby.applicationContext, GameActivity::class.java)
        intent.putExtra(GameActivity.PLAYER_PARAM_KEY, response.player)
        lobby.startActivity(intent)
    }
    private fun sessionExited(connection: Connection, response: SessionResponses.SessionExited) {
        lobby.runOnUiThread { lobby.out() }
    }
    private fun sessionCreated(connection: Connection, response: SessionResponses.SessionCreated) {
        GybomlClient.sendTCP(SessionRequests.ConnectSession(response.sessionId))

        lobby.runOnUiThread {
            val toast = Toast.makeText(lobby, "Session created", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
            toast.show()
        }
    }
    private fun sessionConnected(connection: Connection, response: SessionResponses.SessionConnected) {
        lobby.runOnUiThread {
            lobby.join(response.sessionId)
        }
    }
}

