package ua.woochat.server;

import ua.woochat.server.controller.Server;

import java.io.File;
import java.net.URISyntaxException;

/**
 * class MainServer to run the Server
 */
public class MainServer {

    /**
     * Main method for Server
     */
    public static void main(String[] args) {
        Server.startServer();
    }
}
