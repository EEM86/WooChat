package ua.woochat.server.controller;

import org.apache.log4j.Logger;
import ua.woochat.server.model.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class MainServer {
    final static Logger logger = Logger.getLogger(MainServer.class);
    private ServerSocket serverSocket = null;
    private final int PORT = 8989;
    private ArrayList<Connection> connections = new ArrayList<Connection>();

    public static void main(String[] args) {
        new MainServer();
    }

    public MainServer() {
        logger.info("Server running");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                try {
                    new Connection(this, serverSocket.accept());
                } catch (IOException e) {
                    logger.error("Connection exception " + e);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void connectionOn(Connection connection) {
        connections.add(connection);
        sendAllConnection("Client connected " + connection);
    }

    public synchronized void connectionDisconect(Connection connection) {
        connections.remove(connection);
        sendAllConnection("Client disconnected " + connection);
    }

    public synchronized void onReseiveMessage (Connection connection, String message) {
        sendAllConnection(message);
    }

    private void sendAllConnection (String message) {
        logger.info("Message: " + message);
        int size = connections.size();
        for (int i = 0; i < size; i++) {
            connections.get(i).sendMessage(message);
        }

    }

}
