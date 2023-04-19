package org.connection;

import org.bson.types.ObjectId;

public class FigureDataObject {
    String name;
    ObjectId icon;
    ObjectId figureInfoFile;

    public FigureDataObject(String name, ObjectId icon, ObjectId figureInfoFile) {
        this.name = name;
        this.icon = icon;
        this.figureInfoFile = figureInfoFile;
    }

    public FigureDataObject() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId getIcon() {
        return icon;
    }

    public void setIcon(ObjectId icon) {
        this.icon = icon;
    }

    public ObjectId getFigureInfoFile() {
        return figureInfoFile;
    }

    public void setFigureInfoFile(ObjectId figureInfoFile) {
        this.figureInfoFile = figureInfoFile;
    }
}
