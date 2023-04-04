package org.connection;

import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import java.net.Socket;

public class Client {

    private Socket clientSocket;
    private boolean isReconnecting;
    private boolean isInGame;
    private boolean authenticated;

    public boolean isReconnecting() {
        return isReconnecting;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    private String gameStamp;
    private  String name;
    public void setName(String name) {
        this.name = name;
    }


    public  Client(Socket clientSocket) {
        this.clientSocket = clientSocket;
        isReconnecting = false;
        isInGame = false;
        authenticated = false;
        gameStamp = null;
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
