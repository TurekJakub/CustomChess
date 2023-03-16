package org.connection;

import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private boolean isReconnecting;
    private boolean isInGame;
    private String gameStamp;

    public void setName(String name) {
        this.name = name;
    }

    private  String name;

    public Client(Socket clientSocket, String name) {
        this.clientSocket = clientSocket;
        isReconnecting = false;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Socket getClientSocket(){
        return clientSocket;
    }

    public String getGameStamp() {
        return gameStamp;
    }

    public void setGameStamp(String gameStamp) {
        this.gameStamp = gameStamp;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public void setInGame(boolean inGame) {
        isInGame = inGame;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void setReconnecting(boolean reconnecting) {
        isReconnecting = reconnecting;
    }

    public boolean IsReconnecting() {
        return isReconnecting;
    }
}
