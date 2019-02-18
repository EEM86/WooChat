package ua.woochat.server.controller;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.ConnectionAgent;
import ua.woochat.server.model.ConfigServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Set;

public class Server implements ConnectionAgent {
    final static Logger logger = Logger.getLogger(Server.class);
    private Set<Connection> connections = new LinkedHashSet<>();

    /**
     * Constructor creates Server socket which waits for connections.
     */
    public Server() {
        logger.debug("Server is running");
        try (ServerSocket serverSocket = new ServerSocket(ConfigServer.getPortConnection())) {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Connection connection = new Connection(this, clientSocket);
                    //где-то здесь надо проверить логин и пароль, после авторизации вызвать метод connectionCreated
                    // можно ли стартовать тред после проверки или всё же в конструкторе? connection.getThread().start();
                    connectionCreated(connection);
                    logger.debug("Client's socket was accepted: [" + clientSocket.getInetAddress().getHostAddress()
                            + ":" + clientSocket.getPort() + "]. Connection success.");
                } catch (IOException e) {
                    logger.error("Connection exception " + e);
                }
            }
        } catch (IOException e) {
            logger.error("Server socket exception " + e);
        }
    }
    @Override
    public synchronized void connectionCreated(Connection data) {
        connections.add(data);
        receivedMessage("Client connected " + data);
    }
    @Override
    public synchronized void connectionDisconnect(Connection data) {
        connections.remove(data);
        receivedMessage("Client disconnected " + data);
    }

    @Override
    public void receivedMessage(String text) {

        for (Connection entry : connections) {
            entry.sendToOutStream(text);
            logger.debug(text);
        }
    }

/*
    public boolean authorize(User user) {
        boolean result = false;
        try (FileInputStream fis = new FileInputStream("Server/src/main/resources/allusers.xml")) {
            Properties properties = new Properties();
            properties.loadFromXML(fis);
            if (user.getLogin().equals(properties.getProperty("name"))) {
                result =  true;
            }
        } catch (FileNotFoundException e) {
            logger.error("File not found " + e);
        } catch (IOException ioe) {
            logger.error("Error with inputstream " + ioe);
        }
        return result;
    }
*/
}
