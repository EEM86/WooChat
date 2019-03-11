package ua.woochat.server.model;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Class works with saving or loading Client properties into resources/client.properties.
 */
public class ConfigServer {
    private static final Logger logger = Logger.getLogger(ConfigServer.class);
    private static ConfigServer configServer;
    static Properties properties;
    //static String path = "Server/src/main/resources/server.properties"; // "Server" + File.separator + "src" + File.separator
//                            + "main" + File.separator + "resources" + File.separator
//                            + "server.properties";

    private ConfigServer() {
        properties = new Properties();
    }

    public static ConfigServer getConfigServer() {
        if (configServer == null) {
            configServer = new ConfigServer();
        }
        return configServer;
    }

    public static int getPort(String value) {
        try {
            properties.load(ConfigServer.class.getClassLoader().getResourceAsStream("server.properties"));
                    //(new FileInputStream(path));
        } catch (IOException e) {
            logger.error("IOException error " + e);
        }
        return Integer.parseInt(properties.getProperty(value));
    }

    public static String getRootAdmin() {
        try {
            properties.load(ConfigServer.class.getClassLoader().getResourceAsStream("server.properties"));
        } catch (IOException e) {
            logger.error("IOException error " + e);
        }
        return properties.getProperty("rootAdmin");
    }

    public static int getTimeOut() {
        try {
            properties.load(ConfigServer.class.getClassLoader().getResourceAsStream("server.properties"));
        } catch (IOException e) {
            logger.error("IOException error " + e);
        }
        return Integer.parseInt(properties.getProperty("timeout"));
    }


//    public static int getPortChatting() {
//        try {
//            properties.load(new FileInputStream(path));
//        } catch (IOException e) {
//            logger.error("IOException error " + e);
//        }
//        return Integer.parseInt(properties.getProperty("portchatting"));
//    }
}
