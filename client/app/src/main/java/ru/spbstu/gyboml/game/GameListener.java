package main.java.ru.spbstu.gyboml.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import main.java.ru.spbstu.gyboml.GybomlClient;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.net.GameResponses;

public class GameListener extends Listener {

    private Game game;

    GameListener(Game game) { this.game = game; }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof GameResponses.Shooted) { shoted(connection, (GameResponses.Shooted)object); }
    }

    private void shoted(Connection connection, GameResponses.Shooted object) {
        PlayerType whoShoted = object.yourShoot ? GybomlClient.getPlayerType() : GybomlClient.getPlayerType().reverted();
        float angle = object.angle;

        synchronized (game) {
            game.physicalScene.generateShot(whoShoted, game.shotType, angle);
            game.soundEffects.shot.play(1.f);
        }
    }
}
