package ua.woochat.client;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.ConnectionListener;
import ua.woochat.app.Message;
import ua.woochat.app.ConnectionImpl;
import ua.woochat.app.MessageImpl;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestClient implements ConnectionListener {
    private Socket socket;
    private final String IP = "localhost";
    private final static Logger logger = Logger.getLogger(TestClient.class);
    private String login;
    BufferedReader reader;
    BufferedReader socketIn;
    BufferedWriter socketOut;
    Connection connection;
    Thread threadRead;
    Thread threadWrite;

    public static void main(String[] args) throws IOException {
        new TestClient();
    }

    private TestClient() throws IOException {
        try {
            socket = new Socket(IP, Connection.PORTCONNECT);
        } catch (Exception e) {
            logger.error("Exception " + e);
        }
        logger.info("Client has connected to socket");
        connection = new ConnectionImpl(socket, this);
        connectionCreated(connection);
        reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Type login: ");
        login = reader.readLine();
        while (true) {
            socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
            Message message = new MessageImpl(login,
                    reader.readLine(), Message.CONTENT_TYPE);
            connection.send(message);
        }

//        threadRead = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String serverMessage = null;
//                try {
//                    while (true) {
//                        serverMessage = socketIn.readLine();
//                        System.out.println("Client has received a message: " + serverMessage);
//                    }
//                } catch (IOException e) {
//                    System.out.println(e);
//                }
//            }
//        });
//        threadRead.start();
//        threadWrite = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (true) {
//                        String message = reader.readLine();
//                        socketOut.write(message + "\r\n");
//                        socketOut.flush();
//                        //logger.info("Client has sent a message: " + message);
//                    }
//                } catch (IOException e) {
//                    System.out.println(e);
//                }
//            }
//        });
//        threadWrite.start();
    }

    @Override
    public void connectionCreated(Connection connection) {
        logger.info("Client connection was created");
        this.connection = connection;
    }

    @Override
    public void connectionClosed(Connection connection) {
        logger.info("Client connection was closed");
    }

    @Override
    public void connectionException(Connection connection, Exception exception) {
        exception.printStackTrace();
    }

    @Override
    public void sendToAll(Message message) {
        Date current = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        System.out.println(sdf.format(current) + " " + message);
    }
}
