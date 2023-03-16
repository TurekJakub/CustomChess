package org.connection;

import java.io.IOException;

public class GameThread extends Thread {
    private final Game game;
    private final int timeout;

    public GameThread(Game game, int timeout) {
        this.game = game;
        this.timeout = timeout;
    }
    public synchronized boolean accept(Client player){
       return game.acceptNewPlayer(player);
    }
    public void run(){
        game.runGame(timeout);
    }
}
