package ua.woochat.server.controller;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.ConnectionAgent;
import ua.woochat.app.Message;
import ua.woochat.server.model.ConfigServer;
import ua.woochat.app.HandleXml;
import ua.woochat.server.model.User;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Server implements ConnectionAgent {
    final static Logger logger = Logger.getLogger(Server.class);
    private Set<Connection> connections = new LinkedHashSet<>();
    private Message message;
    private ArrayList<User> listRegisteredUsers = new ArrayList<>();
    private Set<File> listFilesUsers = new HashSet<>();
    private User user;
    private Connection connection;
    /**
     * Constructor creates Server socket which waits for connections.
     */
    HandleXml handleXml = new HandleXml();
    public Server() {
        logger.debug("Server is running");
        try (ServerSocket serverSocket = new ServerSocket(ConfigServer.getPortConnection())) {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    connection = new Connection(this, clientSocket);
                    //где-то здесь надо проверить логин и пароль, после авторизации вызвать метод connectionCreated
                    // можно ли стартовать тред после проверки или всё же в конструкторе? connection.getThread().start();

                    //if (userCreated(connection)) {
                        connectionCreated(connection);
                        logger.debug("Client's socket was accepted: [" + clientSocket.getInetAddress().getHostAddress()
                                + ":" + clientSocket.getPort() + "]. Connection success.");
                    //}
                } catch (IOException e) {
                    logger.error("Connection exception " + e);
                }
            }
        } catch (IOException e) {
            System.out.println("ConfigServer.setUserId(user.getId());" + user.getId());
            logger.error("Server socket exception " + e);
        }
    }

    public synchronized boolean userCreated(Connection data) {
        return true;
    }

    @Override
    public synchronized void connectionCreated(Connection data) {
        System.out.println("data" + data);
       // connections.add(data);
       // receivedMessage("Client connected " + data);
    }
    @Override
    public synchronized void connectionDisconnect(Connection data) {
        connections.remove(data);
        receivedMessage("Client disconnected " + data);
    }

    @Override
    public void receivedMessage(String text) {
        try {
            message = handleXml.unMarshallingMessage(text);
        } catch (JAXBException e) {
            logger.error("unMarshallingMessage " + e);
        }
        // регистрация
        if (message.getType() == 0) {
            Message messageSend = new Message(0,"");
            //        messageSend.setType(0);
            if (verificationName(message.getLogin())) { // проверка существует ли имя
                User user = new User(message.getLogin(), message.getPassword());
                user.saveUser();
                messageSend.setMessage("true");
            }
            connection.sendToOutStream(handleXml.marshalling1(Message.class, messageSend));
        }

        // вход
        if (message.getType() == 1) {
            Message messageSend = new Message(1,"");
            //messageSend.setType(1);
            if (verificationSingIn(message.getLogin(), message.getPassword())) { // проверка существует ли имя
                User user = new User(message.getLogin(), message.getPassword());
                messageSend.setMessage("true");
                System.out.println("Соединение");
            }
            connection.sendToOutStream(handleXml.marshalling1(Message.class, messageSend));
        }

        // сообщение
        if (message.getType() == 2) {
            Message messageSend = new Message(2,"");
            messageSend.setType(2);
            for (Connection entry : connections) {
                entry.sendToOutStream(text);
            }
        }
    }

    private boolean verificationName(String login) {
        String path = new File("").getAbsolutePath();
        File file = new File(path + "/Server/src/main/resources/User/" + login.hashCode() + ".xml");
        if (file.isFile()) {
            return false;
        }
        return true;
    }

    private boolean verificationSingIn(String login, String password) {
        String path = new File("").getAbsolutePath();
        File file = new File(path + "/Server/src/main/resources/User/" + login.hashCode() + ".xml");

        if (file.isFile()) {
            user = (User) handleXml.unMarshalling(file, User.class);
            if (password.equals(user.getPassword())) {
                return true;
            }
        }
        return false;
    }

    // files in folder
    public Set<File> filesFromFolder() {
        String path = new File("").getAbsolutePath();
        File folder = new File(path + "/Server/src/main/resources/User");
        File[] folderEntries = folder.listFiles();
        for (File entry : folderEntries) {
            listFilesUsers.add(entry);
            //listRegisteredUsers.add(handleXml.unMarshalling(entry));
        }
        return listFilesUsers;
    }

}
