package org.connection;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class GamesManager extends Thread {
    private final HashMap<String,GameThread> games;
    private final Server server;
    public GamesManager(Server server) {
       games = new HashMap<>();
       this.server = server;
    }
    public synchronized void joinGame(String name, Client player){
        GameThread gameThread = games.get(name);

        if(gameThread.accept(player)){
            gameThread.start();
        }
    }
    public synchronized void creatGame(String name, int numberOfPlayers, List<File> gameFiles, List<File> gameResources, int timeout){
        Game game = new Game(numberOfPlayers,name,gameFiles,gameResources,server,this);
        games.put(name, new GameThread(game,timeout));
        System.out.println("New");
    }
    public synchronized void interrupt(GameThread gameThread){
        gameThread.interrupt();
        games.remove(gameThread.getName());
    }

    @Override
    public void run() {

        for(String key : games.keySet()){
            if(!games.get(key).isAlive()){
                games.remove(key);
            }
        }
    }
}
