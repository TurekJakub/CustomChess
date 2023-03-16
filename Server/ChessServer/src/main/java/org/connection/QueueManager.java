package org.connection;

import java.io.IOException;
import java.util.List;

public class QueueManager extends Thread {

    private final List<Client> queue;
    Server server;

    public QueueManager(Server server) {
        this.server = server;
        queue = server.getQueue();
    }

    @Override
    public void run() {
        while (true) {
            for (Client client : queue) {
               checkClientsConnection(client);
            }


        }
    }
    private void checkClientsConnection(Client client){
        try {
            Receiver.readData(client.getClientSocket());
        } catch (IOException e) {
            server.removeClosedQueueConnection(client);
        }
        if (server.hasFreeCapacity()) {
            server.connectNewClient(client);
        }
    }

}
