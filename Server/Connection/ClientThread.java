package org.connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.Sort;
import dev.morphia.query.experimental.filters.Filters;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import static dev.morphia.query.experimental.filters.Filters.eq;

public class ClientThread extends Thread {
    private GamesManager gamesManager;
    private Socket connection;
    private Object lock;
    private final Server server;
    private Client client;
    private final int timeout;
   private final Datastore dbConnection;

    public ClientThread(GamesManager gamesManager, Server server, Socket socket, int timeout) {
        this.gamesManager = gamesManager;
        this.server = server;
        // lock = clientThreadsLock;
        this.timeout = timeout;
        this.client = null;
        this.connection = socket;
        dbConnection = establishDbConnection("CustomChess","org.connection","mongodb+srv://UwU:MamRadTuhleDatabazi69@customchess.hmtwp1r.mongodb.net/?retryWrites=true&w=majority");
    }
    public ClientThread(GamesManager gamesManager, Server server,Client client, int timeout) {
        this.gamesManager = gamesManager;
        this.server = server;
        // lock = clientThreadsLock;
        this.timeout = timeout;
        this.client = client;
        this.connection = client.getClientSocket();
        dbConnection = establishDbConnection("CustomChess","org.connection","mongodb+srv://UwU:MamRadTuhleDatabazi69@customchess.hmtwp1r.mongodb.net/?retryWrites=true&w=majority");

    }

    public ClientThread() {
        dbConnection = establishDbConnection("CustomChess","org.connection","mongodb+srv://UwU:MamRadTuhleDatabazi69@customchess.hmtwp1r.mongodb.net/?retryWrites=true&w=majority");
        server = null;
        timeout =1;
    }

    private Datastore establishDbConnection(String dbName, String packageName, String connectionString){
        String uri = connectionString;
        MongoClient mongoClient = MongoClients.create(uri);
        final Datastore datastore = Morphia.createDatastore(mongoClient, dbName);
        datastore.getMapper().mapPackage(packageName);
        return datastore;
    }

    public void authenticatedUser() throws IOException {
      // String authenticationString = Receiver.readBytes(connection);
       ClientDataObject c = new ClientDataObject("skdjkjs","dskjfdskf","ajdkDJ@JDSAHJF",false);
       dbConnection.save(c);

       List<ClientDataObject> x =  dbConnection.find(ClientDataObject.class).filter(eq("active",false)).iterator().toList();
        System.out.println(x.get(0).email);
    }

    public static void main(String[] args) throws IOException {
        ClientThread c = new ClientThread();
        c.authenticatedUser();
    }

    @Override
    public void run() {
        if(client == null){
            try {
                authenticatedUser();
            } catch (IOException e) {
                return;
            }
        }
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
                gamesManager.creatGame(splitInput[1], Integer.valueOf(splitInput[2]), gameFiles, gameResources, timeout);
                gamesManager.joinGame(splitInput[1], client);
                return false;
            case "joig":
                try {
                    gamesManager.joinGame(splitInput[1], client);
                } catch (InputMismatchException exception) {
                    Sender.send(connection, "err:GameIsFull");
                    return true;
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
