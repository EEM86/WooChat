package ua.woochat.server.model;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Class works with saving or loading Client properties into resources/client.properties.
 */
public class ConfigServer {
    private static final Logger logger = Logger.getLogger(ConfigServer.class);
    static Properties properties = new Properties();
    static String path = "Server" + File.separator + "src" + File.separator
                            + "main" + File.separator + "resources" + File.separator
                            + "server.properties";

    public static int getPortConnection() {
        try {
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            logger.error("IOException error " + e);
        }
        return Integer.parseInt(properties.getProperty("portconnection"));
    }
}
