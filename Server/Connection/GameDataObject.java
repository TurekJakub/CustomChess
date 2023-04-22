package org.connection;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity("game")
public class GameDataObject {
    @Id
    ObjectId _id;
    ObjectId generalGameInfoFile;
    ArrayList<ObjectId> playersInfoFiles;
    ArrayList<FigureDataObject> figures;
    String name;
    int numberOfPlayers;
    public GameDataObject(String name,ObjectId generalGameInfoFile, ArrayList<ObjectId> playersInfoFiles, ArrayList<FigureDataObject> figures,int numberOfPlayers) {
        this.generalGameInfoFile = generalGameInfoFile;
        this.playersInfoFiles = playersInfoFiles;
        this.figures = figures;
        this.name = name;
        this.numberOfPlayers = numberOfPlayers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameDataObject() {
    }

    public ObjectId getGeneralGameInfoFile() {
        return generalGameInfoFile;
    }

    public void setGeneralGameInfoFile(ObjectId generalGameInfoFile) {
        this.generalGameInfoFile = generalGameInfoFile;
    }

    public ArrayList<ObjectId> getPlayersInfoFiles() {
        return playersInfoFiles;
    }

    public void setPlayersInfoFiles(ArrayList<ObjectId> playersInfoFiles) {
        this.playersInfoFiles = playersInfoFiles;
    }

    public ArrayList<FigureDataObject> getFigures() {
        return figures;
    }

    public void setFigures(ArrayList<FigureDataObject> figures) {
        this.figures = figures;
    }
    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
