package org.connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;

import static dev.morphia.query.experimental.filters.Filters.eq;
import static dev.morphia.query.experimental.filters.Filters.or;

public class ClientThread extends Thread {
    private GamesManager gamesManager;
    private Socket connection;
    private Object lock;
    private final Server server;
    private Client client;
    private final int timeout;
    private final Datastore dbConnection;
    private final UserAuthenticator userAuthenticator;

    public ClientThread(GamesManager gamesManager, Server server, Socket socket, int timeout) {
        this.gamesManager = gamesManager;
        this.server = server;
        // lock = clientThreadsLock;
        this.timeout = timeout;
        this.client = null;
        this.connection = socket;
        userAuthenticator = new UserAuthenticator();
        dbConnection = establishDbConnection("CustomChess", "org.connection", "mongodb+srv://UwU:MamRadTuhleDatabazi69@customchess.hmtwp1r.mongodb.net/?retryWrites=true&w=majority");
    }

    public ClientThread(GamesManager gamesManager, Server server, Client client, int timeout) {
        this.gamesManager = gamesManager;
        this.server = server;
        // lock = clientThreadsLock;
        this.timeout = timeout;
        this.client = client;
        this.connection = client.getClientSocket();
        userAuthenticator = new UserAuthenticator();
        dbConnection = establishDbConnection("CustomChess", "org.connection", "mongodb+srv://UwU:MamRadTuhleDatabazi69@customchess.hmtwp1r.mongodb.net/?retryWrites=true&w=majority");

    }

    public ClientThread() {
        dbConnection = establishDbConnection("CustomChess", "org.connection", "mongodb+srv://UwU:MamRadTuhleDatabazi69@customchess.hmtwp1r.mongodb.net/?retryWrites=true&w=majority");
        server = null;
        userAuthenticator = new UserAuthenticator();
        timeout = 1;
    }

    private Datastore establishDbConnection(String dbName, String packageName, String connectionString) {
        MongoClient mongoClient = MongoClients.create(connectionString);
        final Datastore datastore = Morphia.createDatastore(mongoClient, dbName);
        datastore.getMapper().mapPackage(packageName);
        return datastore;
    }
    /*
         Start handling clients requests until is properly authenticated, or authentication throttling end the session
         Accepted request format is "request type:username:password:email" - all parameters except request type
         are optional according to given request type
    */
    // TODO lines starting with Sender are commented for testing purposes only
    public void authenticatedUser() throws IOException {
        boolean authenticated = false;
        while (!authenticated) {
            //  String[] authenticationString = Receiver.readBytes(connection).split(":"); // production
            String[] authenticationString = "signin:OwO:123Hesl0:OwO@gmail.com".split(":"); // test
            // Extraction of data from request
            String username = authenticationString[1];
            String password = authenticationString[2];
            ClientDataObject clientData;
            switch (authenticationString[0]) {
                case "signup": // sign up request handling
                    String email = authenticationString[3];
                    // checking the validity of the username
                    if (dbConnection.find(ClientDataObject.class).filter(or(eq("username", username), eq("email", username))).count() != 0) {
                       // Sender.send(connection, "err:username invalid");
                        break;
                    }
                    // checking if user does not have account already
                    if (dbConnection.find(ClientDataObject.class).filter(eq("email", email)).count() != 0) {
                      //  Sender.send(connection, "err:account exist");
                        break;
                    }
                    // creating new user entry in database
                    String hashedPasswordString = userAuthenticator.getHashedPasswordString(password, 390000);
                    clientData= new ClientDataObject(username, hashedPasswordString, email, false, new ArrayList<>());
                    dbConnection.save(clientData);
                    // TODO emailSender.sendAuthenticationEmail(authenticationString[2]); // send registration confirmation email
                    break;
                case "signin": // sign in request handling
                    // extracting user entry from database
                    try{
                      clientData = dbConnection.find(ClientDataObject.class).filter(or(eq("username", username), eq("email", username))).iterator().toList().get(0);
                    }catch (IndexOutOfBoundsException ex){
                       // Sender.send(connection,"err:authentication error");
                        break;
                    }
                    if(!clientData.isActive()){
                       // Sender.send(connection,"err:account inactive");
                    }
                    // getting passwords hashes to compare
                    String[] clientPasswordString = clientData.getPassword().split("\\$");
                    String authenticationPasswordHash = userAuthenticator.getPasswordHash(password,clientPasswordString[2],Integer.parseInt(clientPasswordString[1]));
                    String clientPasswordHash = clientPasswordString[3];
                    if(clientPasswordHash.equals(authenticationPasswordHash)){
                        // signing user in
                        // TODO create Client instance
                        authenticated = true;
                        clientData.setLast_login(new Date(System.currentTimeMillis()));
                        dbConnection.save(clientData);
                        //  Sender.send(connection,"msg:success");
                        break;
                    }
                    // sign in failed
                    //  Sender.send(connection,"err:authentication error");
                    // TODO add authentication throttling

                    break;
                case "reset": // password reset request handling
                    //TODO
                    break;

            }
        }
        /* test
        UserAuthenticator p = new UserAuthenticator();
        String x = p.getPasswordHash("MamRadBocchi", "rGNJTzTfz0UubTZjmXkUqi", 390000);

        List<AuthenticationToken> y = new ArrayList<>();
        AuthenticationToken xxx = new AuthenticationToken("UubTZjmXkUqi");

        y.add(xxx);

        ClientDataObject c = new ClientDataObject("Bocchi", "pbkdf2_sha256$390000$rGNJTzTfz0UubTZjmXkUqi$" + x, "bocchi@testovadlo.cz", true, y);
        dbConnection.save(c);

        List<ClientDataObject> xx = dbConnection.find(ClientDataObject.class).filter(eq("username", "Bocchi")).iterator().toList();
        System.out.println(xx.size());
        System.out.println(xx.get(0).getTokens().get(0).getTokenHash());
         */
    }

    public static void main(String[] args) throws IOException {
        ClientThread c = new ClientThread();
        c.authenticatedUser();
    }

    @Override
    public void run() {
        if (client == null) {
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
                gamesManager.creatGame(splitInput[1], Integer.parseInt(splitInput[2]), gameFiles, gameResources, timeout);
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
