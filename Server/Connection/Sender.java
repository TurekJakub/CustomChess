package org.connection;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Sender {

    public static void send(Socket receiver, String data) throws IOException {
        BufferedStreamWriter out = new BufferedWriter(new OutputStreamWriter(receiver.getOutputStream()));
        byte[] length = byte[] bytes = ByteBuffer.allocate(4).putInt(data.length).array(); 
        out.write(length);
        out.write(data.getBytes(StandardCharsets.UTF_8));    

    }
    // Try to find file of given id in given MongoDatabase and if find it send it over the given Socket
    public static void sendFileFromDatabase(MongoDatabase sourceDatabase, ObjectId fileId , Socket receiver) throws IOException {
        GridFSBucket gridFSBucket = GridFSBuckets.create(sourceDatabase);
        Bson query = Filters.eq("_id",fileId);
        GridFSFile file= gridFSBucket.find().filter(query).first();
        Sender.send(receiver,file.getFilename() +":"+file.getLength());
        gridFSBucket.downloadToStream(fileId, receiver.getOutputStream());
        receiver.getOutputStream().flush();
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
