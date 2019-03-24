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
    private static Properties properties;
    private static final String EXTERNAL_PROPERTIES = "clientExtracted.properties";

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
            File file = new File(EXTERNAL_PROPERTIES);
            if (file.exists()) {
                properties.load(new FileInputStream(EXTERNAL_PROPERTIES));
            } else {
                properties.load(ConfigClient.class.getClassLoader().getResourceAsStream("client.properties"));
                logger.debug("Client properties was extracted from jar file to: " + System.getProperty("user.dir") + File.separator + (EXTERNAL_PROPERTIES));
                properties.store(new FileOutputStream(file), null);
            }
        } catch (IOException e) {
            logger.error("File not found exceptions ", e);
        }
    }

    public static int getPortConnection() {
        try {
            properties.load(new FileInputStream(EXTERNAL_PROPERTIES));
        } catch (IOException e) {
            logger.error("IOException error ", e);
        }
        return Integer.parseInt(properties.getProperty("portconnection"));
    }

    public static String getServerIP() {
        try {
            properties.load(new FileInputStream(EXTERNAL_PROPERTIES));
        } catch (IOException e) {
            logger.error("IOException error ", e);
        }
        return properties.getProperty("serverip");
    }
}
