package ua.woochat.client;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TestClient {
    private final String IP = "localhost";
    private final int PORT = 5555;
    private final static Logger logger = Logger.getLogger(TestClient.class);

    public static void main(String[] args) throws IOException {
        new TestClient();
    }

    private TestClient() throws IOException {

        try (Socket socket = new Socket(IP, PORT)) {
            logger.info("Client has connected to socket");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            BufferedWriter socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
            String message = reader.readLine();
            socketOut.write(message);
            socketOut.flush();
            logger.info("Client has sent a message " + message);
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
            String serverMessage = socketIn.readLine();
            logger.debug("Client has received a message" + serverMessage);
        } catch (Exception e) {
            logger.error("Exception " + e);
        }
    }
}
