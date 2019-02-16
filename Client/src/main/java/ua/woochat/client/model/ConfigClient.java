package ua.woochat.client.model;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigClient {
    private static final Logger logger = Logger.getLogger(ConfigClient.class);
    static Properties properties = new Properties();
    static String path = "Client" + File.separator + "src" + File.separator
            + "main" + File.separator + "resources" + File.separator
            + "client.properties";

    public static int getPortConnection() {
        try {
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            logger.error("IOException error " + e);
        }
        return Integer.parseInt(properties.getProperty("portconnection"));
    }
    public static String getServerIP() {
        try {
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            logger.error("IOException error " + e);
        }
        return properties.getProperty("serverip");
    }
}
