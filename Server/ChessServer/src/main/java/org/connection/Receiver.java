package org.connection;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Receiver {
    public static String readData(Socket source) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(source.getInputStream()));
        return in.readLine();
    }
    public static String readBytes(Socket source) throws IOException {
        InputStream in = source.getInputStream();
        byte[] data  = new byte[4];
        in.read(data);
        int i = ByteBuffer.wrap(data).getInt();
        data = new byte[i];
        in.read(data);
        return new String(data);
    }
    public static File readFile(Socket socket) throws IOException{
        int i;
        String[] fileData = readData(socket).split(":");
        int length = Integer.valueOf(fileData[1]);
        File file = new File("t"+fileData[0]);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] buffer = new byte[8<<10];
        while (length>0 &&(i = socket.getInputStream().read(buffer,0,Math.min(length,buffer.length))) != -1) {
            fileOutputStream.write(buffer,0,i);
            length -= i;
        }

        return file;
    }
}
