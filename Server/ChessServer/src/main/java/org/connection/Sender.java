package org.connection;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Sender {

    public static void send(Socket receiver, String data) throws IOException {
        PrintWriter out = new PrintWriter(receiver.getOutputStream());
        out.println(data);
        out.flush();
        if (out.checkError()) {
            throw new IOException();
        }

    }

    public static void sendFile(Socket receiver, File file) throws IOException {
        int i;
        String fileData = file.getName() + ":" + file.length();
        send(receiver, fileData);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[8 << 10];
        while ((i = fileInputStream.read(buffer)) != -1) {
            receiver.getOutputStream().write(buffer, 0, i);


        }
        receiver.getOutputStream().flush();

    }
    /*
    public static int sendToMultipleClients(List<Socket> receivers, String data, int startingIndex){
        for (int i = startingIndex; i<receivers.size(); i++) {
            try {
                send(receivers.get(i), data);
            } catch (IOException e) {
                return i;
            }
        }
        return -1;
    }
    */

}
