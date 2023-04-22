package org.connection;

import java.io.IOException;

public class GameThread extends Thread {
    private final Game game;
    private final int timeout;

    public GameThread(Game game, int timeout) {
        this.game = game;
        this.timeout = timeout;
    }
    public synchronized boolean accept(Client player, String password){
       return game.acceptNewPlayer(player, password);
    }
    public void run(){
        game.runGame(timeout);
    }
    public synchronized String getRulesName(){
        return game.getRulesName();
    }
}
