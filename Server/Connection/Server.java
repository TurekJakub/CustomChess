package org.connection;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private final List<Client> queue;
    private final List<Client> clients;
    private final List<Client> reconnectingClients;
    private final List<ClientThread> clientsThreads;
    private final ServerSocket serverSocket;
    private final int QUEUE_LENGTH;
    private final int CLIENTS;
    private final QueueManager queueManager;
    private final GamesManager gamesManager;
    private final int timeout;
    private final String databaseName;
    private final String connectionString;
    private final String email;
    private final String emailPassword;

    public Server(ServerParameters serverParameters) throws IOException {
        SecureConnectionManager secureConnectionManager = new SecureConnectionManager(serverParameters);
        gamesManager = new GamesManager(this);
        serverSocket = secureConnectionManager.getSecureServerSocket(serverParameters.getPort());
        queue = new ArrayList<>();
        clients = new ArrayList<>();
        clientsThreads = new ArrayList<>();
        QUEUE_LENGTH = serverParameters.getQueueLength();
        CLIENTS = serverParameters.getNumberOfClients();
        reconnectingClients = new ArrayList<>();
        timeout = serverParameters.getTimeout();
        queueManager = new QueueManager(this);
        databaseName = serverParameters.getDatabaseName();
        connectionString = serverParameters.getConnectionString();
        email = serverParameters.getEmail();
        emailPassword = serverParameters.getEmailPassword();
    }

    @Override
    public void run() {

        queueManager.start();
        Socket socket;
        while (true) {

            try {
                socket = serverSocket.accept();

            } catch (IOException e) {
                System.out.println("Connection interrupted");
                continue;
            }
            handelNewClientConnection(new Client(socket));
        }
    }

    public boolean reconnect(String gameStamp, Socket newConnection) {
        for (int i = 0; i < reconnectingClients.size(); i++) {
            Client client = reconnectingClients.get(i);
            if (client.getGameStamp().equals(gameStamp)) {
                clients.get(clients.indexOf(client)).setClientSocket(newConnection);
                reconnectingClients.remove(client);

                try {
                    Sender.send(newConnection, "Welcome back" + "\n");
                    System.out.println("Znovu připojen");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }

        }

        return false;
    }

    private void handelNewClientConnection(Client client) {

        if (queue.size() < 1) {
            if (clients.size() < CLIENTS) {
                connectNewClient(client);
            } else if (queue.size() < QUEUE_LENGTH) {
                addToQueue(client);
            } else {
                refuseConnection(client);
            }
        } else {
            if (queue.size() < QUEUE_LENGTH) {
                addToQueue(client);
            } else {
                refuseConnection(client);
            }
        }
    }


    public synchronized void connectNewClient(Client client) {

        clients.add(client);
        startClientThread(client);
    }



    // start ClientThread for new client
    public synchronized void startClientThread(Client client) {
        ClientThread clientThread = new ClientThread(gamesManager, this, client, timeout,connectionString,databaseName,email,emailPassword);
        clientThread.start();
        clientsThreads.add(clientThread);

    }
    // remove pointer of already interrupted ClientThread from list of managed threads
    public synchronized void removeDeadThread(ClientThread clientThread) {
        clientsThreads.remove(clientThread);
    }
    // add client to queue
    private void addToQueue(Client client) {
        try {
            Sender.send(client.getClientSocket(), "Ve frontě");
        } catch (IOException e) {
            return;
        }
        queue.add(client);
    }
    // refuse connection of client - when max connections limit is exceeded
    private void refuseConnection(Client client) {
        try {
            Sender.send(client.getClientSocket(), "K serveru se momentálně nedá připojit z důvodu nadměrného zatížení");
        } catch (IOException e) {
            return;
        }
    }

    public synchronized void removeClosedConnection(Client inactiveClient, ClientThread clientThread) {
        clientThread.interrupt();
        try {
            inactiveClient.getClientSocket().close();
        } catch (IOException ignored) {

        }
        clientsThreads.remove(clientThread);
        clients.remove(inactiveClient);
    }
    // remove already closed connections from queue
    public synchronized void removeClosedQueueConnection(Client inactiveClient) {
        try {
            inactiveClient.getClientSocket().close();
        } catch (IOException ignored) {

        }
        queue.remove(inactiveClient);

    }

    public synchronized void setClientAsReconnecting(Client client) {
        client.setReconnecting(true);
        reconnectingClients.add(client);
    }

    public synchronized boolean hasFreeCapacity() {
        return clients.size() < CLIENTS;
    }

    public synchronized List<Client> getQueue() {
        return queue;
    }
}
