package org.connection;

import java.io.IOException;
import java.util.ArrayList;
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

    private void checkClientsConnection(Client client) {
        try {
            String x = Receiver.readData(client.getClientSocket());
            if (client.getGameStamp().equals(null)) {
                client.setGameStamp(x);
                if (server.reconnect(x, client.getClientSocket()))
                    server.removeClosedQueueConnection(client);
            }
        } catch (IOException e) {
            server.removeClosedQueueConnection(client);
        }
        if (server.hasFreeCapacity()) {
            server.connectNewClient(client);
        }
    }

}
