package ru.spbstu.gyboml.multiplayer

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import ru.spbstu.gyboml.core.Player

class GameActivity : AndroidApplication() {

    companion object {
        const val PLAYER_PARAM_KEY = "PLAYER_PARAM_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val player = intent.getSerializableExtra(PLAYER_PARAM_KEY) as Player
        setContentView(initializeForView(Game(this, player), AndroidApplicationConfiguration()))
    }

    override fun onBackPressed() {
    }
}
