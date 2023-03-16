package org.connection;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;

public class SSLClient {
    public static void main(String[] args) throws Exception {
        SSLClient c = new SSLClient();
        c.request();
    }

    public void request() throws Exception {

//        SecureConnectionManager ss = new SecureConnectionManager();
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null,"password".toCharArray());
        //trustStore.setCertificateEntry("uwu",ss.loadCertificate("C:/Users/jakub/desktop/c.pem"));
        //trustStore.load(new FileInputStream("./AppData/keystore.jks"), "password".toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), SecureRandom.getInstanceStrong());
        SocketFactory factory = ctx.getSocketFactory();

        try  {
            Socket connection = factory.createSocket("127.0.0.1", 443)  ;
            ((SSLSocket) connection).setEnabledProtocols(new String[]{"TLSv1.3"});
           // SSLParameters sslParams = new SSLParameters();
            // sslParams.setEndpointIdentificationAlgorithm("HTTPS");
          //  ((SSLSocket) connection).setSSLParameters(sslParams);
            System.out.println(Receiver.readData(connection));
            Sender.send(connection, "timestamp:dfhwjkgjh");
            Sender.send(connection, "name:UwU");
            Sender.send(connection,"crt:uwu:2");

            Sender.send(connection,"1");
            Sender.sendFile(connection,new File("./AppData/config.json"));
            Sender.send(connection,"1");
            Sender.sendFile(connection,new File("./AppData/config.json"));


            while (true){
                String s = Receiver.readData(connection);
                if(s.equals("notify")){
                    Sender.send(connection,"hraje hráč 1");
                }
                System.out.println(s);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
