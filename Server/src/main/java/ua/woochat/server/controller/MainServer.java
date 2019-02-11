package ua.woochat.server.controller;

import org.apache.log4j.Logger;
import ua.woochat.server.model.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class MainServer {
    final static Logger logger = Logger.getLogger(MainServer.class);
    private ServerSocket serverSocket = null;
    private final int PORT = 5555;
    private ArrayList<Connection> connections = new ArrayList<>();

    public static void main(String[] args) {
        new MainServer();
    }

    private MainServer() {
        logger.debug("Server is running");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    new Connection(this, serverSocket.accept());
                    logger.debug("Server Socket was accepted");
                } catch (IOException e) {
                    logger.error("Connection exception " + e);
                }
            }
        } catch (IOException e) {
            logger.error("Server socket exception " + e);
        }
    }

    public synchronized void connectionOn(Connection connection) {
        connections.add(connection);
        sendAllConnections("Client connected " + connection);
    }

    public synchronized void connectionDisconnect(Connection connection) {
        connections.remove(connection);
        sendAllConnections("Client disconnected " + connection);
    }

    public synchronized void onReceiveMessage(Connection connection, String message) {
        sendAllConnections(message);
    }

    private void sendAllConnections(String message) {
        logger.info("Message to all from server: " + message);
        int size = connections.size();
        for (int i = 0; i < size; i++) {
            connections.get(i).sendMessage(message);
        }
    }
}
