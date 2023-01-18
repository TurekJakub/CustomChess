package org.connection;


import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        ParametersParser parametersParser = new ParametersParser();
        ServerParameters serverParameters = null;
        try {
            serverParameters = parametersParser.getServerParameters();
        } catch (IOException e) {
            System.err.println("Nepodařilo se načíst konfigurační soubor: ./AppData/config.json");
            System.exit(1);
        }
        Server server = null;
        try {
            server = new Server(serverParameters);
        } catch (IOException e) {
            System.err.println("Při spouštení serveru došlo k chybě, zkuste to znovu později");
            System.exit(1);
        }
        server.start();
    }
}