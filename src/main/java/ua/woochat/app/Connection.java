package ua.woochat.app;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class Connection implements Connect, Runnable {

    private final Socket socket;
    private final Thread thread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final ConnectionAgent connectionAgent;
    final static Logger logger = Logger.getLogger(Connection.class);

    public Connection(ConnectionAgent connectionAgent, Socket socket) throws IOException {
        this.connectionAgent = connectionAgent;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Reads socket's input stream data and sends it to all clients.
     */
    @Override
    public void run() {
        while(true) {
            try {
                if (in.ready()) {
                    String text = in.readLine();
                    connectionAgent.sendAllConnections(text.trim());
                }
            } catch (IOException e) {
                logger.error("Error with connection creation" + e);
            }
        }
    }

    /**
     * Sends data to socket's output stream.
     * @param text - data for send to output stream.
     */
    @Override
    public void sendToOutStream(String text) {
        try {
            out.write(text + "\r\n");
            out.flush();
        } catch (IOException e) {
            logger.error("Error with socket output stream" + e);
            disconnect();
        }
    }

    @Override
    public void disconnect() {
        thread.interrupt();
        logger.debug("Thread was interrupted");
        try {
            socket.close();
            in.close();
            out.close();
            logger.debug("Client socket has closed");
        } catch (IOException e) {
            logger.error(Connection.this, e);
        }
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    public String toString() {
        return "[" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + "]";
    }
}
