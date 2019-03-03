package ua.woochat.server.controller;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.ConnectionAgent;
import ua.woochat.app.Message;
import ua.woochat.server.model.ConfigServer;
import ua.woochat.app.HandleXml;
import ua.woochat.app.User;
import ua.woochat.server.model.Group;

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
    public Set<Group> groupsList = new LinkedHashSet<>();

    /**
     * Constructor creates Server socket which waits for connections.
     */
    private Server() {
        ConfigServer.getConfigServer();
        logger.debug("Server is running");
        try {
            serverConnectSocket = new ServerSocket(ConfigServer.getPort("portconnection"));
            serverChattingSocket = new ServerSocket(ConfigServer.getPort("portchatting"));
            final Group groupMain = new Group("MainGroup", "group000");
            groupsList.add(groupMain);
            while (true) {
                try {
                    Socket clientConnectionSocket = serverConnectSocket.accept();
                    connection = new Connection(this, clientConnectionSocket);
                        logger.debug("Client's socket was accepted: [" + clientConnectionSocket.getInetAddress().getHostAddress()
                                + ":" + clientConnectionSocket.getPort() + "]. Connection success.");
                } catch (IOException e) {
                    logger.error("Connection exception " + e);
                }
            }
        } catch (IOException e) {
            //System.out.println("ConfigServer.setUserId(user.getId());" + user.getId());
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
        Set<String> currentUserGroups = data.user.getGroups();
        for (String entry: currentUserGroups) {
            for (Group group : groupsList) {
                if (entry.equalsIgnoreCase(group.getGroupName())) {
                    group.usersList.add(data);
                    group.addUser(data);
                    logger.debug("Users in group \"" + group.getGroupName() + "\": ");
                    group.getUsersList().stream().forEach(x -> System.out.println(x.user.getLogin())); //печатает в консоль список юзеров в группе
                }
            }
        }
       connections.add(data);
//       Message messageToSend = new Message(3, "Connection added " + data);
//       receivedMessage(connection, HandleXml.marshalling1(Message.class, messageToSend));
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
                connection.user = new User(message.getLogin(), message.getPassword());
                connection.user.saveUser();
                connectionCreated(connection);
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("true, port=" + ConfigServer.getPort("portchatting"));
                messageSend.setOnlineUsers(getOnlineUsers());
                connection.sendToOutStream(HandleXml.marshalling1(Message.class, messageSend));
                moveToChattingSocket();
            } else {
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("false");
                connection.sendToOutStream(HandleXml.marshalling1(Message.class, messageSend));
            }
        }

        // вход
        else if (message.getType() == 1) {
            Message messageSend = new Message(1,"");
            //messageSend.setType(1);
            if (verificationSingIn(message.getLogin(), message.getPassword())) { // проверка существует ли имя
                connection.user = new User(message.getLogin(), message.getPassword());
                connection.user.addGroup("MainChat");                 // сэтим юзеру главный чат как группу
                connectionCreated(connection);
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("true, port=" + ConfigServer.getPort("portchatting"));
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
        else if (message.getType() == 2)  {
//            Message messageSend = new Message(2, message.getMessage());
//            messageSend.setLogin(message.getLogin());
            String groupID = message.getGroupID();
            Set<Connection> result = null;
            for (Group entry: groupsList) {
                if (entry.getGroupID().equalsIgnoreCase(groupID)) {
                    result = entry.getUsersList();
                    for (Connection c: result) {
                        c.sendToOutStream(HandleXml.marshalling1(Message.class, message));
                    }
                }
            }
            logger.debug("Who writes from server side: " + connection.user.getLogin());
            sendToAll(HandleXml.marshalling1(Message.class, message));
        }

        else if (message.getType() == 3) {
            //Message messageToSend = new Message(3, message.getMessage());
            message.setOnlineUsers(getOnlineUsers());
            //connection.sendToOutStream(HandleXml.marshalling1(Message.class, messageToSend));
           sendToAll(HandleXml.marshalling1(Message.class, message));
        }

        else if (message.getType() == 6) {   //сделать чтобы в файл User.xml записывался
            Group group = new Group("group00" + groupsList.size());
            groupsList.add(group);
            message.setGroupID(group.getGroupID());
            ArrayList<String> tmp = message.getGroupList();
            for (String s: tmp) {
                for (Connection entry: connections) {
                    if (entry.user.getLogin().equals(s)) {
                        entry.user.groups.add(group.getGroupID());
                        entry.sendToOutStream(HandleXml.marshalling1(Message.class, message));
//                        Message msg = new Message(2, "You are connected to private chat "
//                                + group.getGroupName() + " " + group.getGroupID());
//                        msg.setLogin(entry.user.getLogin());
//                        entry.sendToOutStream(HandleXml.marshalling1(Message.class, msg));
                    }
                }
            }
        }

        else if (message.getType() == 7) {
            for (Connection entry : connections) {
                if (entry.user.getLogin().equals(message.getLogin())) {
                    //тут мы не добавили (некорректно добавили) юзара для рассылки ему сообщений
                    //на его стороне не срабатыввает "==2". Он может отправлять, а ему нет
                    entry.user.groups.add(message.getGroupID());


                    logger.debug("Group: " + message.getGroupID());
                    message.setType(7);
                    entry.sendToOutStream(HandleXml.marshalling1(Message.class, message));
                }
            }
        }
    }

    public void sendToAll(String text) {
        for (Connection entry:connections) {
            logger.info(entry.user.getLogin());
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
