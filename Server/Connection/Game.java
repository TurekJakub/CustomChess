package org.connection;


import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import static java.lang.Thread.sleep;


public class Game {
    // TODO create and implement GameLogic
    private final List<Client> players;
    private final String name;
    private final String password;
    private final int NUMBER_OF_PLAYERS;
    private final String rulesName;
    private final Server server;
    private final GamesManager gamesManager;

   // private final GameLogic gameLogic;

    public Game(int numberOfPlayers, String name, String password, rulesName,Server server, GamesManager gamesManager) {
        this.players = new ArrayList<>();
        this.name = name;
        this.password = password;
        NUMBER_OF_PLAYERS = numberOfPlayers;
        this.rulesName = rulesName;
        this.server = server;
        this.gamesManager = gamesManager;

        //gameLogic = new GameLogic(gameInfoFiles);
    }
    public String getRulesName() {
        return rulesName;
    }
    private void sendGameFiles(List<File> files, Socket player) {

        try {
            Sender.send(player, "file:" + files.size());
            for (File file : files) {
                Sender.sendFile(player, file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public  boolean acceptNewPlayer(Client player, String password) {
        if(!this.password.equals(password)){
            throw new InputMismatchException();
        }
        if (players.size() < NUMBER_OF_PLAYERS) {
            try {
                Sender.send(player.getClientSocket(), name + ":" + System.currentTimeMillis());
            } catch (IOException e) {
                return false;
            }
            players.add(player);
            player.setInGame(true);          
            if (players.size() == NUMBER_OF_PLAYERS) {
                return true;
            }
            return false;
        }

        throw new InputMismatchException();
    }
    public void runGame(int timeout){
        int a =0;

        // while (gameLogic.isInProgress)
        while (a<10){ // debug

            handelOneRound(timeout);
            a++; // debug
        }
       // Sender.sendToMultipleClients(getPlayersSockets(),gameLogic.getResult);
        sendToAllClients("Game ended",timeout);
        disconnectClientsFromGame();
    }
    private void handelOneRound(int timeout){
        // int indexOfFirst = gameLogic.getIndexOfFirstPlayer();
        int indexOfFirst = 1; // debug
        for (int i = indexOfFirst; i < NUMBER_OF_PLAYERS ; i++) {
            handelOneMove(players.get(i),timeout);
        }
        for (int i = 0; i < indexOfFirst; i++) {
            handelOneMove(players.get(i),timeout);
        }

    }
    private void disconnectClientsFromGame(){
        for(Client client : players){
            server.startClientThread(client);
        }
    }
    private void handelOneMove(Client player, int timeout){
        Socket playersSocket = player.getClientSocket();
        try {
            Sender.send(playersSocket,"notify");
            //Sender.sendFile(playersSocket,gameLogic.getPossibleMoves(player));
            // sendToAllClients(gameLogic.evaluateMove(Receiver.readData(playersSocket)),timeout);
            sendToAllClients(Receiver.readData(playersSocket),timeout); // debug
            Sender.send(playersSocket,"wait");

        } catch (Exception e) {
            if(player.IsReconnecting()){
                sendToAllClients("Spojení s hráčem " + player.getName() + " ztraceno. Hra bude ukončena.",timeout);

            }
            server.setClientAsReconnecting(player);
            try {
                sleep(timeout);
            } catch (InterruptedException ex) {
                return;
            }
            handelOneMove(player,timeout);
        }

    }
    private List<Socket> getPlayersSockets(){
        List<Socket> playersSockets = new ArrayList<>();
        for (Client c : players){
            playersSockets.add(c.getClientSocket());
        }
        return playersSockets;
    }

    private void sendToAllClients(String data, int timeout){

        for(Client c : players){
            try {
                Sender.send(c.getClientSocket(),data);
            } catch (IOException e) {
                server.setClientAsReconnecting(c);
                try {
                    sleep(timeout);
                } catch (InterruptedException ignored) {
                }
                try {
                    Sender.send(c.getClientSocket(),data);
                } catch (IOException ex) {
                    throw new RuntimeException("Spojení s hráčem " + c.getName() + " ztraceno. Hra bude ukončena.");
                }
            }
        }
    }


}
