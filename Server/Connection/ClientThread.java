package org.connection;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class ClientThread extends Thread {
    private GamesManager gamesManager;
    private Socket connection;
    private Object lock;
    private final Server server;
    private final Client client;
    private final int timeout;

    public ClientThread(GamesManager gamesManager, Server server, Client client, int timeout) {
        this.gamesManager = gamesManager;
        this.server = server;
        // lock = clientThreadsLock;
        this.timeout = timeout;
        this.client = client;
        this.connection = client.getClientSocket();
    }

    @Override
    public void run() {

        handelClientsRequest();
        server.removeDeadThread(this);
        System.out.println("konec");
    }

    private void handelClientsRequest() {
        boolean run = true;
        while (run) {

            try {
                String input = Receiver.readData(connection);
                System.out.println(input);
                run = evaluateClientInput(input);
            } catch (IOException e) {
                System.out.println("Umřel :(");
                server.removeClosedConnection(client, this);
                return;
            }


        }
    }

    private boolean evaluateClientInput(String input) throws IOException {
        String[] splitInput = input.split(":");
        switch (splitInput[0]) {
            case "crtg":
                System.out.println("UwU");
                List<File> gameFiles = recievGameFiles(connection);
                List<File> gameResources = recievGameFiles(connection);
                gamesManager.creatGame(splitInput[1], Integer.valueOf(splitInput[2]), gameFiles, gameResources,timeout);
                gamesManager.joinGame(splitInput[1],client);
                return false;
            case "joig":
                try {
                    gamesManager.joinGame(splitInput[1],client);
                }catch (InputMismatchException exception){
                    Sender.send(connection,"err:GameIsFull");
                    return  true;
                }

                return false;
            default:
                System.out.println("wtf");
                return true;

        }

    }

    public List<File> recievGameFiles(Socket creatorsSocket) throws IOException {
        List<File> files = new ArrayList<>();
        int numberOfFiles;
        numberOfFiles = Integer.parseInt(Receiver.readData(creatorsSocket));
        for (int i = 0; i < numberOfFiles; i++) {

            files.add(Receiver.readFile(creatorsSocket));

        }
        return files;
    }
}
