package org.connection;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ParametersParser {
    private ServerParameters serverParameters;
    private final Gson gson;
    private final File configFile;

    public ParametersParser() {
        gson = new Gson();
        configFile = new File("./AppData/config.json");
    }

    public ServerParameters getServerParameters() throws IOException {
        if (serverParameters == null) {
            serverParameters = gson.fromJson(Files.readString(configFile.toPath()), ServerParameters.class);
        }
        return serverParameters;
    }
}
