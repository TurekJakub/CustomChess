package org.connection;

public class ServerParameters {
    private int port;
    private int queueLength;
    private int numberOfClients;

    private int timeout;
    private String privateKey;
    private String certificate;
    private String certificateChain;
    private String keyStore;
    private String keyStorePassword;
    private String method;
    private String connectionString;
    private String databaseName;
    private String emailPassword;
    private String email;

    public ServerParameters() {
    }

    public ServerParameters(int port, int queueLength, int numberOfClients, int timeout, String privateKey, String certificate, String certificateChain, String keyStore, String keyStorePassword, String method, String connectionString, String databaseName, String emailPassword, String email) {
        this.port = port;
        this.queueLength = queueLength;
        this.numberOfClients = numberOfClients;
        this.timeout = timeout;
        this.privateKey = privateKey;
        this.certificate = certificate;
        this.certificateChain = certificateChain;
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        this.method = method;
        this.connectionString = connectionString;
        this.databaseName = databaseName;
        this.emailPassword = emailPassword;
        this.email = email;
    }
    public getEmail(){
        return email;
    }
    public getEmailPassword(){
        return emailPassword;
    }
    public getConnectionString(){
        return connectionString;
    }
    public getDatabaseName(){
        return databaseName;
    }      
    public int getPort() {
        return port;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public int getNumberOfClients() {
        return numberOfClients;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getCertificate() {
        return certificate;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getMethod() {
        return method;
    }

    public String getCertificateChain() {
        return certificateChain;
    }
}
