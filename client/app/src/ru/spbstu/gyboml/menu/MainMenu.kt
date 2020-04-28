package ru.spbstu.gyboml.menu

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.victor.loading.rotate.RotateLoading
import ru.spbstu.gyboml.GybomlClient
import ru.spbstu.gyboml.R
import ru.spbstu.gyboml.core.net.SessionRequests
import ru.spbstu.gyboml.lobby.Lobby
import kotlin.system.exitProcess

class MainMenu : AppCompatActivity() {
    private var startButton: Button? = null
    private var exitButton: Button? = null
    private var rotateLoading: RotateLoading? = null

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

        rotateLoading = findViewById(R.id.rotateloading)
    }

    private fun createDialogueWindow() {
        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        builder.setTitle("Pick username:")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") {dialog, which -> //try to connect
            val name = input.text.toString()
            rotateLoading?.start()

            GybomlClient.connect(this) {
                runOnUiThread {
                    val toast = Toast.makeText(this, "Connected to server", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
                    toast.show()
                }

                val intent = Intent(applicationContext, Lobby::class.java)
                startActivity(intent)

                GybomlClient.sendTCP(SessionRequests.RegisterName(name))
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }
}