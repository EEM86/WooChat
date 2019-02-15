package ua.woochat.server.controller;

import org.apache.log4j.Logger;
import ua.woochat.app.*;
import ua.woochat.app.ConnectionImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedHashSet;
import java.util.Set;

public class Server implements ConnectionListener {
    final static Logger logger = Logger.getLogger(Server.class);
    private Set<Connection> listConnections = new LinkedHashSet<>();
    private ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(Connection.PORTCONNECT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        logger.debug("Server started and is waiting for connection.");
        while (true) {
            try {
                connectionCreated(new ConnectionImpl(serverSocket.accept(), this));
            } catch (IOException e) {
                logger.error("Can't create connection " + e);
            }
        }
    }

    public void stopServer() {
        try {
            serverSocket.close();
            logger.debug("Server was stopped");
        } catch (IOException e) {
            logger.error("Server socket can't be closed " + e);
        }
    }

    @Override
    public synchronized void connectionCreated(Connection connection) {
        listConnections.add(connection);
        logger.debug("Connection was added");
    }

    @Override
    public synchronized void connectionClosed(Connection connection) {
        listConnections.remove(connection);
        connection.close();
        logger.debug("Connection was closed");

    }

    @Override
    public synchronized void connectionException(Connection connection, Exception exception) {
        connection.close();
        logger.error("Exception with connection " + exception);
    }

    @Override
    public synchronized void sendToAll(Message message) {
        for (Connection entry : listConnections) {
            entry.send(message);
            logger.debug("message from server was sent: " + message + "\r");
        }
    }
}
