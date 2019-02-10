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

    public Connection(final MainServer mainServer, Socket socket) throws IOException {
        this.mainServer = mainServer;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    mainServer.connectionOn(Connection.this);
                    while (!thread.isInterrupted()) {
                        mainServer.onReseiveMessage(Connection.this, in.readLine());
                    }
                }catch (IOException e) {
                    logger.error("Not reading");
                }finally {
                    mainServer.connectionDisconect(Connection.this);
                }
            }
        });
        thread.start();
    }

    public synchronized void sendMessage(String message) {
        try {
            out.write(message);
            out.flush();
        } catch (IOException e) {
            logger.error("Error send:" + e);
            disconnect();
        }
    }

    private synchronized void disconnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            logger.error(Connection.this, e);
        }
    }

    @Override
    public String toString() {
        return "Connection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
