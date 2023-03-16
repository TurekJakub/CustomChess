package org.connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyStore;

import static java.lang.Thread.sleep;

public class TestClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(KeyStore.getDefaultType());
        Socket s = new Socket("localhost", 80);
        BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));

        File f = new File("test.txt");
        Sender.sendFile(s, f);


        Sender.send(s, "ss\n");


        System.out.println(f.length());
      //  sleep(100);
       // Sender.send(s, "crt:j:2");

        while (true){

        }


    }
}
