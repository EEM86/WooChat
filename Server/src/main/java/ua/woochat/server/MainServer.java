package ua.woochat.server;

import ua.woochat.server.controller.Server;

public class MainServer {
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
