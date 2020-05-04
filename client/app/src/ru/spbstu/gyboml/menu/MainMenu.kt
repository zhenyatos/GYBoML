package ru.spbstu.gyboml.menu

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.victor.loading.rotate.RotateLoading
import ru.spbstu.gyboml.AndroidUtils
import ru.spbstu.gyboml.core.net.GybomlClient
import ru.spbstu.gyboml.R
import ru.spbstu.gyboml.core.net.SessionRequests
import ru.spbstu.gyboml.lobby.Lobby
import kotlin.system.exitProcess

class MainMenu : AppCompatActivity() {
    private var startButton: Button? = null
    private var exitButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_menu)

        startButton = findViewById(R.id.multiplayerButton)
        startButton?.setOnClickListener { createDialogueWindow() }

        exitButton = findViewById(R.id.exitButton)
        exitButton?.setOnClickListener {
            finishAffinity()
            exitProcess(0)
        }
    }

    private fun createDialogueWindow() {
        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        builder.setTitle("Pick username:")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ -> //try to connect
            val name = input.text.toString()

            val connectedReaction = {
                AndroidUtils.showToast(this, "Connected!")

                val intent = Intent(applicationContext, Lobby::class.java)
                startActivity(intent)

                GybomlClient.sendTCP(SessionRequests.RegisterName(name))
            }

            val notConnectedReaction = {
                AndroidUtils.showToast(this, "Couldn't connect to server")
            }

            AndroidUtils.showToast(this, "Connecting...")
            GybomlClient.connect(connectedReaction, notConnectedReaction)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}