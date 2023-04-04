package org.connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import org.bson.types.ObjectId;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static dev.morphia.query.filters.Filters.eq;
import static dev.morphia.query.filters.Filters.or;

public class ClientThread extends Thread {
    private final GamesManager gamesManager;
    private final Socket connection;
    private final Server server;
    private final Client client;
    private final int timeout;
    private final Datastore dbConnection;
    private final UserAuthenticator userAuthenticator;
    private final EmailSender emailSender;


    public ClientThread(GamesManager gamesManager, Server server, Client client, int timeout) {
        this.gamesManager = gamesManager;
        this.server = server;
        this.timeout = timeout;
        this.client = client;
        this.connection = client.getClientSocket();
        emailSender = new EmailSender();
        userAuthenticator = new UserAuthenticator();
        dbConnection = establishDbConnection("CustomChess", "org.connection", "mongodb+srv://UwU:MamRadTuhleDatabazi69@customchess.hmtwp1r.mongodb.net/?retryWrites=true&w=majority");

    }

    /*
     Establish connection with MongoDb database specified by given connectionString and name and also set up Morphia
     ORM mapper on classes of given package
     */
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
    public void authenticatedUser() throws IOException {
        boolean authenticated = false;
        ClientDataObject clientData;
        String username;
        String password;
        String email;
        while (!authenticated) {
            System.out.println("start");
            String[] authenticationString = Receiver.readBytes(connection).split(":"); // production
            //String[] authenticationString = "reset:OwO_UwU:123Hesl0:jakub.turek@student.gyarab.cz".split(":"); // test
            switch (authenticationString[0]) {
                case "signup": // sign up request handling
                    username = authenticationString[1];
                    password = authenticationString[2];
                    email = authenticationString[3];
                    // checking the validity of the username
                    if (dbConnection.find(ClientDataObject.class).filter(or(eq("username", username), eq("email", username))).count() != 0) {
                        Sender.send(connection, "err:username invalid");
                        break;
                    }
                    // checking if user does not have account already
                    if (dbConnection.find(ClientDataObject.class).filter(eq("email", email)).count() != 0) {
                        Sender.send(connection, "err:account exist");
                        System.out.println("again");
                        break;
                    }
                    // creating new user entry in database
                    ObjectId profilePicture = Receiver.readAndSaveFileToDatabase(dbConnection.getDatabase(), connection);

                    int id = dbConnection.find(ClientDataObject.class).iterator(new FindOptions().projection().include("id").sort(Sort.descending("id")).limit(1)).toList().get(0).getId() + 1;
                    String hashedPasswordString = userAuthenticator.getHashedPasswordString(password, 390000);
                    String tokenValueString = userAuthenticator.getAuthenticationTokenString(32);

                    clientData = new ClientDataObject(profilePicture, username, hashedPasswordString, email, false, new ArrayList<>(), id);
                    clientData.addToken(userAuthenticator.getAuthenticationToken(tokenValueString, 3000));

                    dbConnection.save(clientData);


                    emailSender.sendConfirmationEmail(email, userAuthenticator.getUrlEncodedId(id) + "/" + tokenValueString);
                    Sender.send(connection, "msg:success");
                    break;
                case "signin": // sign in request handling
                    username = authenticationString[1];
                    password = authenticationString[2];
                    // extracting user entry from database
                    try {
                        clientData = dbConnection.find(ClientDataObject.class).filter(or(eq("username", username), eq("email", username))).iterator().toList().get(0);
                    } catch (IndexOutOfBoundsException ex) {
                        Sender.send(connection,"err:authentication error");
                        break;
                    }
                    if (!clientData.isActive()) {
                         Sender.send(connection,"err:account inactive");
                        break;
                    }
                    // getting passwords hashes to compare
                    String[] clientPasswordString = clientData.getPassword().split("\\$");
                    String authenticationPasswordHash = userAuthenticator.getPasswordHash(password, clientPasswordString[2], Integer.parseInt(clientPasswordString[1]));
                    String clientPasswordHash = clientPasswordString[3];
                    if (clientPasswordHash.equals(authenticationPasswordHash)) {
                        // signing user in
                        client.setAuthenticated(true);
                        client.setName(username);
                        client.setGameStamp(Receiver.readData(connection));

                        if (server.reconnect(client.getGameStamp(), connection)) {
                            server.removeClosedConnection(client, this);
                            return;
                        }

                        clientData.setLast_login(new Date(System.currentTimeMillis()));
                        dbConnection.save(clientData);

                        Sender.send(connection, "msg:success");
                        Sender.sendFileFromDatabase(dbConnection.getDatabase(), clientData.getProfilePicture(), connection);
                        break;
                    }
                    // sign in failed
                    Sender.send(connection, "err:authentication error");
                    // TODO add authentication throttling (optional)

                    break;
                case "reset": // password reset request handling
                    username = authenticationString[1];
                    try {
                        clientData = dbConnection.find(ClientDataObject.class).filter(or(eq("username", username), eq("email", username))).iterator().toList().get(0);
                    } catch (IndexOutOfBoundsException ex) {
                        Sender.send(connection, "err:authentication error");
                        break;
                    }
                    if (!clientData.isActive()) {
                        Sender.send(connection, "err:account inactive");
                        break;
                    }
                    tokenValueString = userAuthenticator.getAuthenticationTokenString(32);
                    clientData.addToken(userAuthenticator.getAuthenticationToken(tokenValueString, 3000));
                    dbConnection.save(clientData);
                    emailSender.sendResetPasswordEmail(clientData.getEmail(), userAuthenticator.getUrlEncodedId(clientData.getId()) + "/" + tokenValueString);
                    authenticated = true; //debug

                    break;

            }
        }

    }

    /*
     Main thread method that start client authentication sequence and after successful authentication start client
     requests evaluation loop
    */
    @Override
    public void run() {
        if (!client.isAuthenticated()) {
            try {
                authenticatedUser();
            } catch (IOException e) {
                try {
                    Sender.send(connection,"err:authentication failed");
                } catch (IOException ignored) {
                }
                try {
                    connection.close();
                } catch (IOException ignored) {

                }
                server.removeClosedConnection(client,this);
            }
        }
        handelClientsRequest();
        server.removeDeadThread(this);
        try {
            connection.close();
        } catch (IOException ignored) {

        }
        System.out.println("konec");
    }
    // Main loop that hand over clients request to by handled
    private void handelClientsRequest() {
        boolean run = true;
        while (run) {

            try {
                String input = Receiver.readData(connection);
                System.out.println(input); // debug
                run = evaluateClientInput(input);
            } catch (IOException e) {
                System.out.println("Umřel :(");
                server.removeClosedConnection(client, this);
                return;
            }


        }
    }
    // Evaluate client requests after authentication
    // TODO replace receiving files over socket with loading from database
    private boolean evaluateClientInput(String input) throws IOException {
        String[] splitInput = input.split(":");
        switch (splitInput[0]) {
            case "crtg":

                List<File> gameFiles = receiveGameFiles(connection);
                List<File> gameResources = receiveGameFiles(connection);
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
            case "add":
                // TODO add adding new games definitions to database
            default:
                return true;

        }

    }

    // TODO will be removed in future versions
    public List<File> receiveGameFiles(Socket creatorsSocket) throws IOException {
        List<File> files = new ArrayList<>();
        int numberOfFiles;
        numberOfFiles = Integer.parseInt(Receiver.readData(creatorsSocket));
        for (int i = 0; i < numberOfFiles; i++) {

            files.add(Receiver.readFile(creatorsSocket));

        }
        return files;
    }
}
