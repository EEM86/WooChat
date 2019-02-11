package ua.woochat.server.model;

import org.apache.log4j.Logger;
import ua.woochat.server.controller.MainServer;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class Connection {

    private final Socket socket;
    private final Thread thread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final MainServer mainServer;
    final static Logger logger = Logger.getLogger(Connection.class);

    public Connection(MainServer mainServer, Socket socket) throws IOException {
        this.mainServer = mainServer;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mainServer.connectionOn(Connection.this);
                    logger.debug("Method MainServer.connectionOn started on Connection.class");
                    while (!thread.isInterrupted()) {
                        mainServer.onReceiveMessage(Connection.this, in.readLine());
                        logger.debug("*********Server read a message from a Client**************");
                    }
                }catch (IOException e) {
                    logger.error("Not reading" + e);
                }finally {
                    mainServer.connectionDisconnect(Connection.this);
                    logger.debug("Client has been disconnected");
                }
            }
        });
        thread.start();
        logger.debug("New thread has been started");
    }

    public synchronized void sendMessage(String message) {
        try {
            out.write(message + "\r\n");
            out.flush();
            logger.debug("Server wrote to outstream the message from the Client");
        } catch (IOException e) {
            logger.error("Error send:" + e);
            disconnect();
        }
    }

    private synchronized void disconnect() {
        thread.interrupt();
        logger.debug("Thread was interrupted");
        try {
            socket.close();
            logger.debug("Client's socket has been closed");
        } catch (IOException e) {
            logger.error(Connection.this, e);
        }
    }

    @Override
    public String toString() {
        return "[" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + "]";
    }
}
