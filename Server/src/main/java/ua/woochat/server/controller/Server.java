package ua.woochat.server.controller;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.ConnectionAgent;
import ua.woochat.app.Message;
import ua.woochat.server.model.ConfigServer;
import ua.woochat.app.HandleXml;
import ua.woochat.app.User;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public final class Server implements ConnectionAgent {
    final static Logger logger = Logger.getLogger(Server.class);
    private static Server server;
    private Set<Connection> connections = new LinkedHashSet<>();
    private Message message;
    private ArrayList<User> listRegisteredUsers = new ArrayList<>();
    private Set<File> listFilesUsers = new HashSet<>();
    private User user;
    private Connection connection;
    private Map<Integer, ArrayList<String>> onlineUsers = new HashMap<>();
    ServerSocket serverConnectSocket;
    ServerSocket serverChattingSocket;

    /**
     * Constructor creates Server socket which waits for connections.
     */
    private Server() {
        ConfigServer.getConfigServer();
        logger.debug("Server is running");
        try {
            serverConnectSocket = new ServerSocket(ConfigServer.getPort("portconnection"));
            serverChattingSocket = new ServerSocket(ConfigServer.getPort("portchatting"));
            while (true) {
                try {
                    Socket clientConnectionSocket = serverConnectSocket.accept();
                    connection = new Connection(this, clientConnectionSocket);
                    //где-то здесь надо проверить логин и пароль, после авторизации вызвать метод connectionCreated
                    // можно ли стартовать тред после проверки или всё же в конструкторе? connection.getThread().start();

                    //if (userCreated(connection)) {

                        //connectionCreated(connection);
                        logger.debug("Client's socket was accepted: [" + clientConnectionSocket.getInetAddress().getHostAddress()
                                + ":" + clientConnectionSocket.getPort() + "]. Connection success.");
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

    public static Server startServer() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    public synchronized boolean userCreated(Connection data) {
        return true;
    }

    @Override
    public synchronized void connectionCreated(Connection data) {
       connections.add(data);
       Message messageToSend = new Message(3, "Connection added " + data);
       receivedMessage(connection, HandleXml.marshalling1(Message.class, messageToSend));
    }
    @Override
    public synchronized void connectionDisconnect(Connection data) {
        connections.remove(data);
        //receivedMessage("Client disconnected " + data);
    }

    @Override
    public void receivedMessage(Connection data, String text) {
        connection = data;
        try {
            message = HandleXml.unMarshallingMessage(text);
        } catch (JAXBException e) {
            logger.error("unMarshallingMessage " + e);
        }
        // регистрация
        if (message.getType() == 0) {
            Message messageSend = new Message(0,"");
            //        messageSend.setType(0);
            if (verificationName(message.getLogin())) { // проверка существует ли имя
                User user = new User(message.getLogin(), message.getPassword());
                connection.user = user;
                user.saveUser();
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("true, port=" + ConfigServer.getPort("portchatting"));
                connection.sendToOutStream(HandleXml.marshalling1(Message.class, messageSend));
                moveToChattingSocket();
            } else {
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("false");
                connection.sendToOutStream(HandleXml.marshalling1(Message.class, messageSend));
            }
        }

        // вход
        if (message.getType() == 1) {
            Message messageSend = new Message(1,"");
            //messageSend.setType(1);
            if (verificationSingIn(message.getLogin(), message.getPassword())) { // проверка существует ли имя
                User user = new User(message.getLogin(), message.getPassword());
                connection.user = user;
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("true, port=" + ConfigServer.getPort("portchatting"));
                connectionCreated(connection);
                messageSend.setOnlineUsers(getOnlineUsers());
                System.out.println("Соединение");
                connection.sendToOutStream(HandleXml.marshalling1(Message.class, messageSend)); // format of message: <?xml version="1.0" encoding="UTF-8" standalone="yes"?><message><password>1qa</password><login>Zhe</login><type>1</type></message>
                moveToChattingSocket();
            } else {
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("false");
                connection.sendToOutStream(HandleXml.marshalling1(Message.class, messageSend));
            }
        }

        // сообщение
        if (message.getType() == 2) {
            Message messageSend = new Message(2, message.getMessage());
            messageSend.setLogin(message.getLogin());
            sendToAll(HandleXml.marshalling1(Message.class, messageSend));
        }

        if (message.getType() == 3) {
            Message messageToSend = new Message(3, message.getMessage());
            messageToSend.setOnlineUsers(getOnlineUsers());
            //connection.sendToOutStream(HandleXml.marshalling1(Message.class, messageToSend));
           sendToAll(HandleXml.marshalling1(Message.class, messageToSend));
        }
    }

    public void sendToAll(String text) {
        for (Connection entry:connections) {
            entry.sendToOutStream(text);
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
            user = (User) HandleXml.unMarshalling(file, User.class);
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

    private void moveToChattingSocket() {
        try {
            Socket clientChatSocket = serverChattingSocket.accept();
            logger.debug("Connection has moved to new socket for chatting: " + clientChatSocket.getInetAddress() + " " +clientChatSocket.getLocalPort());
            connection.setSocket(clientChatSocket);
        } catch (IOException e) {
            logger.error("Error socket creation" + e);
        }
    }
/*
Временно сделал вывод строки, в дальнейшем хмл файл со списком надо будет передавать.
 */
    public String getOnlineUsers() {
        String result = "";
//        Integer kee = 1;
//        ArrayList<String> val = new ArrayList<>();
        for (Connection entry : connections) {
            result += (entry.user.getLogin()) + " ";
        }
        //onlineUsers.put(kee, val);
        return result;
    }
}
