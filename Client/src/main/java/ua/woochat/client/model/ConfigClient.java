package ua.woochat.client.model;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class handles client properties.
 */
public class ConfigClient {
    private static final Logger logger = Logger.getLogger(ConfigClient.class);
    private static ConfigClient configClient;
    private static Properties properties = new Properties();

    private ConfigClient() {
        properties = new Properties();
    }


    public static ConfigClient getConfigClient() {
        if (configClient == null) {
            configClient = new ConfigClient();
            loadClientConfig();
        }
        return configClient;
    }

    private static void loadClientConfig() {
        try {
            File file = new File("clientExtracted.properties");
            if (file.exists()) {
                properties.load(new FileInputStream("clientExtracted.properties"));
            } else {
                properties.load(ConfigClient.class.getClassLoader().getResourceAsStream("client.properties"));
                logger.debug("Client properties was extracted from jar file to: " + System.getProperty("user.dir") + File.separator + ("clientExtracted.properties"));
                properties.store(new FileOutputStream(file), null);
            }
        } catch (IOException e) {
            logger.error("File not found exceptions ", e);
        }
    }

    public static int getPortConnection() {
        try {
            properties.load(ConfigClient.class.getClassLoader().getResourceAsStream("client.properties"));
        } catch (IOException e) {
            logger.error("IOException error ", e);
        }
        return Integer.parseInt(properties.getProperty("portconnection"));
    }

    public static String getServerIP() {
        try {
            properties.load(ConfigClient.class.getClassLoader().getResourceAsStream("client.properties"));
        } catch (IOException e) {
            logger.error("IOException error ", e);
        }
        return properties.getProperty("serverip");
    }
}
