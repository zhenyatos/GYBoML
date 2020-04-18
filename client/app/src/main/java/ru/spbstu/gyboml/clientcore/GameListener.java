package main.java.ru.spbstu.gyboml.clientcore;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import main.java.ru.spbstu.gyboml.GybomlClient;
import ru.spbstu.gyboml.core.PlayerType;
import ru.spbstu.gyboml.core.net.GameResponses;

public class GameListener extends Listener {

    private GybomlGame game;

    GameListener(GybomlGame game) { this.game = game; }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof GameResponses.Shooted) { shoted(connection, (GameResponses.Shooted)object); }
    }

    private void shoted(Connection connection, GameResponses.Shooted object) {
        PlayerType whoShoted = object.yourShoot ? GybomlClient.getPlayerType() : GybomlClient.getPlayerType().reverted();
        game.physicalScene.generateShot(GybomlClient.getPlayerType(), game.shotType);
        game.soundEffects.shot.play(1.f);
    }
}
