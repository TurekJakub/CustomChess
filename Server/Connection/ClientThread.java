package org.connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

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
        UserAuthenticator p = new UserAuthenticator();
        String x = p.getPasswordHash("MamRadBocchi","rGNJTzTfz0UubTZjmXkUqi",390000);

        List<AuthenticationToken>y = new ArrayList<>();
        AuthenticationToken xxx =  new AuthenticationToken("UubTZjmXkUqi");

        y.add(xxx);

       ClientDataObject c = new ClientDataObject("Bocchi","pbkdf2_sha256$390000$rGNJTzTfz0UubTZjmXkUqi$"+x,"bocchi@testovadlo.cz",true,y);
       dbConnection.save(c);

       List<ClientDataObject> xx =  dbConnection.find(ClientDataObject.class).filter(eq("username","Bocchi")).iterator().toList();
        System.out.println(xx.get(0).getTokens().get(0).getTokenHash());
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
