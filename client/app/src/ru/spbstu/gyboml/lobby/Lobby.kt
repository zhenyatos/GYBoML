package ru.spbstu.gyboml.lobby

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.spbstu.gyboml.core.net.GybomlClient
import ru.spbstu.gyboml.R
import ru.spbstu.gyboml.core.net.SessionRequests
import ru.spbstu.gyboml.databinding.ActivityLobbyBinding
import ru.spbstu.gyboml.lobby.Lobby.State.IN
import ru.spbstu.gyboml.lobby.Lobby.State.OUT

class Lobby : AppCompatActivity() {
    lateinit var lobbyInterface: LobbyInterface

    enum class State {IN, OUT}
    var state: State = OUT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lobbyInterface = LobbyInterface(ActivityLobbyBinding.inflate(layoutInflater))
        setContentView(lobbyInterface.binding.root)

        // create dynamic interface
        lobbyInterface.layoutManager = LinearLayoutManager(this)
        lobbyInterface.binding.gameSessions.layoutManager = lobbyInterface.layoutManager
        lobbyInterface.sessionsAdapter = ButtonAdapter()
        lobbyInterface.binding.gameSessions.adapter = lobbyInterface.sessionsAdapter

        // set up listeners
        lobbyInterface.binding.ready.setOnCheckedChangeListener { buttonView, isChecked ->
            readyEvent(buttonView, isChecked)
        }
        lobbyInterface.binding.exit.setOnClickListener {v ->  exitEvent()}
        lobbyInterface.binding.createSession.setOnClickListener {v ->  createSessionEvent(v)}
        lobbyInterface.sessionsAdapter.onClickListener = OnClickListener { v: View -> connectSessionEvent(v) }
        lobbyInterface.binding.swipeContainer.setOnRefreshListener { refreshEvent() }

        GybomlClient.addListener(SessionListener(this))
    }
    override fun onBackPressed() = GybomlClient.disconnect()

    // listeners
    private fun createSessionEvent(view: View) {
        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        builder.setTitle("Create new session")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            GybomlClient.sendTCP(SessionRequests.CreateSession(input.text.toString().trim()))
            GybomlClient.sendTCP(SessionRequests.GetSessions())
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }
    private fun connectSessionEvent(view: View) {
        lobbyInterface.sessionsAdapter.chosenSessionId = view.id
        GybomlClient.sendTCP(SessionRequests.ConnectSession(view.id))
    }
    private fun readyEvent(view: View, isChecked: Boolean) = GybomlClient.sendTCP(SessionRequests.Ready())
    private fun refreshEvent() = GybomlClient.sendTCP(SessionRequests.GetSessions())
    private fun exitEvent() = GybomlClient.sendTCP(SessionRequests.ExitSession())

    // lobby actions
    fun join(id: Int) {
        lobbyInterface.inSession();
        state = IN

        lobbyInterface.sessionsAdapter.chosenSessionId = id
        lobbyInterface.sessionsAdapter.disableTouch()
    }
    fun out() {
        lobbyInterface.outSession();
        state = OUT

        lobbyInterface.sessionsAdapter.chosenSessionId = null
        lobbyInterface.sessionsAdapter.enableTouch()
    }
}