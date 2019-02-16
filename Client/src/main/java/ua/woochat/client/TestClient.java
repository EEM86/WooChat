package ua.woochat.client;

import org.apache.log4j.Logger;
import ua.woochat.app.Connect;
import ua.woochat.app.Connection;
import ua.woochat.app.ConnectionAgent;
import ua.woochat.app.User;
import ua.woochat.client.model.ConfigClient;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class TestClient implements ConnectionAgent {
    private final static Logger logger = Logger.getLogger(TestClient.class);
    private Socket socket;
    private BufferedReader reader;
    private Connection connection;
    //private User user;

    public TestClient() throws IOException {
        try {
            socket = new Socket(ConfigClient.getServerIP(), ConfigClient.getPortConnection());
            reader = new BufferedReader(new InputStreamReader(System.in));
            this.connection = new Connection(this, socket);
            connectionCreated(connection);


                while (true) {
                    try {
                        if (reader.ready()) {
                            String text = reader.readLine();
                            if (!text.isEmpty()) {
                                connection.sendToOutStream(text);
                            }
                        }
                    } catch (IOException e) {
                        logger.error("Connection exception " + e);
                    }
                }
        } catch (Exception e) {
            logger.error("Exception " + e);
        }
    }

    @Override
    public void connectionCreated(Connection connection) {
        logger.debug("New connection has been created");
    }

    @Override
    public void connectionDisconnect(Connection data) {}

    @Override
    public void sendAllConnections(String text) {
        logger.debug(text.trim());
    }

/*
    public boolean authorize() throws IOException {
        boolean result = false;
        while (true) {
            System.out.println("Type login: ");
            String login = reader.readLine();
            if (!login.isEmpty()) {
                System.out.println("Type password: ");
                String password = reader.readLine();
                if (!password.isEmpty()) {
                    result = true;
                    this.user = new User(login, password);
                    break;
                }
            }
        }
        return result;
    }
*/
}
