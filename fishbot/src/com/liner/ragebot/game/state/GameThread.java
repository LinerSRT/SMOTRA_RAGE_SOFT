package com.liner.ragebot.game.state;

import com.liner.ragebot.Settings;
import com.liner.ragebot.game.Config;
import com.liner.ragebot.jna.RageMultiplayer;
import com.liner.ragebot.utils.Worker;

import java.awt.image.BufferedImage;

public class GameThread extends Worker {
    private final RageMultiplayer rageMultiplayer;
    private BufferedImage gameBuffer;
    private GameState gameState;
    private final GameCallback gameCallback;

    public GameThread(RageMultiplayer rageMultiplayer, GameCallback gameCallback) {
        this.rageMultiplayer = rageMultiplayer;
        this.gameCallback = gameCallback;
        this.gameBuffer = rageMultiplayer.getBuffer();
        this.gameState = GameState.WAITING;
    }

    @Override
    public void execute() {
        gameBuffer = rageMultiplayer.getBuffer();
        gameState = GameState.WAITING;
        if (Config.isCoordinatesPresent(gameBuffer, Config.HD.baitSelect)) {
            gameState = GameState.SELECTING_BAIT;
        } else if (Config.isCoordinatesPresent(gameBuffer, Config.HD.throwRod)) {
            gameState = GameState.THROWING_ROD;
        } else if (Config.isCoordinatesPresent(gameBuffer, Config.HD.waitFish)) {
            gameState = GameState.WAITING_FISH;
        } else if ((Config.isCoordinatesPresent(gameBuffer, Config.HD.pickFish)) &&
                (
                        !Config.isCoordinatesPresent(gameBuffer, gameCallback.getSettings().isQteFix() ?
                                Config.HD.pickFishNoEFix : Config.HD.pickFishNoE)
                )
        ) {
            gameState = GameState.PICKING_FISH;
        } else if (
                (Config.isCoordinatesPresent(gameBuffer, gameCallback.getSettings().isQteFix() ?
                        Config.HD.pickFishQTEFix : Config.HD.pickFishQTE)) &&
                        !Config.isCoordinatesPresent(gameBuffer, Config.HD.pickFishQTENoE
                        )
        ) {
            if (Config.isCoordinatesPresent(gameBuffer, Config.HD.pickFishQ, 50)) {
                gameState = GameState.PICKING_QTE_Q;
            } else {
                gameState = GameState.PICKING_QTE_E;
            }
        } else if (Config.isCoordinatesPresent(gameBuffer, Config.HD.finishFish)) {
            gameState = GameState.FINISH;
        } else if (Config.isCoordinatesPresent(gameBuffer, Config.HD.failFish)) {
            gameState = GameState.FAIL;
        } else if (Config.isCoordinatesPresent(gameBuffer, Config.HD.inventory)) {
            gameState = GameState.INVENTORY;
        }
        gameCallback.onChanged(this, gameState);
    }

    @Override
    public long delay() {
        return 16;
    }

    public BufferedImage getGameBuffer() {
        return gameBuffer;
    }

    public interface GameCallback {
        void onChanged(GameThread gameThread, GameState gameState);

        Settings getSettings();
    }
}
