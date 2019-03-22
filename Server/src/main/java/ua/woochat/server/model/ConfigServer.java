package ua.woochat.server.model;

import org.apache.log4j.Logger;
import java.io.*;
import java.util.Properties;

/**
 * Class works with saving or loading Server properties.
 */
public class ConfigServer {
    private static ConfigServer configServer;
    private static Properties properties;
    private static final Logger logger = Logger.getLogger(ConfigServer.class);
    private ConfigServer() {
        properties = new Properties();
    }

    public static ConfigServer getConfigServer() {
        if (configServer == null) {
            configServer = new ConfigServer();
            loadServerConfig();
        }
        return configServer;
    }

    private static void loadServerConfig() {
        try {
            File file = new File("serverExtracted.properties");
            if (file.exists()) {
                properties.load(new FileInputStream("serverExtracted.properties"));
            } else {
                properties.load(ConfigServer.class.getClassLoader().getResourceAsStream("server.properties"));
                logger.debug("Server properties was extracted from jar file to: " + System.getProperty("user.dir") + File.separator + ("serverExtracted.properties"));
                properties.store(new FileOutputStream(file), null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getRootAdmin() {
        try {
            properties.load(new FileInputStream("serverExtracted.properties"));
        } catch (IOException e) {
            logger.error("IOException error " + e);
        }
        return properties.getProperty("rootAdmin");
    }

    public static String getPortConnection() {
        try {
            properties.load(new FileInputStream("serverExtracted.properties"));
        } catch (IOException e) {
            logger.error("IOException error " + e);
        }
        return (properties.getProperty("portConnection"));
    }

    public static String getPortChatting() {
        try {
            properties.load(new FileInputStream("serverExtracted.properties"));
        } catch (IOException e) {
            logger.error("IOException error " + e);
        }
        return (properties.getProperty("portChatting"));
    }

    public static String getTimeOut() {
        try {
            properties.load(new FileInputStream("serverExtracted.properties"));
        } catch (IOException e) {
            logger.error("IOException error " + e);
        }
        return (properties.getProperty("timeout"));
    }

    public static void setConfig(String type, String value) {
        try {
            properties.load(new FileInputStream("serverExtracted.properties"));
            properties.setProperty(type, value);
            File file = new File(System.getProperty("user.dir") + File.separator + "serverExtracted.properties");
            logger.debug("Path to server.properties: " + ConfigServer.class.getClassLoader().getResourceAsStream("server.properties").toString());
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
