package ru.spbstu.gyboml.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

class GameActivity : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val game = Game(this)
        val config = AndroidApplicationConfiguration()
        setContentView(initializeForView(game, config))
    }

    override fun onBackPressed() {
    }
}
