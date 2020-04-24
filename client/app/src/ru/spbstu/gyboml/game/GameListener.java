package ru.spbstu.gyboml.game;

import android.content.Intent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import ru.spbstu.gyboml.GybomlClient;
import ru.spbstu.gyboml.MainActivity;
import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.GameResponses;
import ru.spbstu.gyboml.lobby.Lobby;

public class GameListener extends Listener {

    private Game game;

    GameListener(Game game) { this.game = game; }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof GameResponses.Shooted) { shoted(connection, (GameResponses.Shooted)object); }
        if (object instanceof GameResponses.GameExited) { gameExited(connection, (GameResponses.GameExited)object); }
    }

    private void gameExited(Connection connection, GameResponses.GameExited object) {
       Intent intent = new Intent(game.mainActivity.getApplicationContext(), Lobby.class);
       game.mainActivity.startActivity(intent);
    }

    private void shoted(Connection connection, GameResponses.Shooted object) {
        synchronized (game) {
            game.physicalScene.generateShot(GybomlClient.getPlayerType().reverted(), game.shotType);
            game.physicalScene.getLastShot().getBody().setTransform(object.ballPositionX, object.ballPositionY, 0);
            game.physicalScene.getLastShot().setVelocity(new Vector2(object.ballVelocityX, object.ballVelocityY));
            game.graphicalScene.generateGraphicalShot(game.physicalScene.getLastShot());
            game.soundEffects.shot.play(1.f);
            synchronized (game.fireButton) {
                game.fireButton.setTouchable(Touchable.enabled);
            }
        }
    }
}
