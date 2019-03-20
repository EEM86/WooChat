package ua.woochat.server.controller;

import org.apache.log4j.Logger;
import ua.woochat.app.*;
import ua.woochat.server.model.ConfigServer;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class implements ConnectionAgent. Crates server socket and wait connection from a clients. After connection and authorization throws clients to the chatting socket.
 * Each connection creates in new thread.
 * Server checks activity of connections and disconnects inactive users. Timeout is defined in server.properties file.
 */
public final class Server implements ConnectionAgent {

    private final static Logger logger = Logger.getLogger(Server.class);
    private static Server server;

    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private Set<Connection> connections = new LinkedHashSet<>();
    private Message message;
    private User user;
    private Connection connection;
    private ServerSocket serverConnectSocket;
    private ServerSocket serverChattingSocket;
    private Socket clientConnectionSocket;
    public Set<Group> groupsList = new LinkedHashSet<>();
    private boolean socketListensForConnections = true;

    private Server() {
        ConfigServer.getConfigServer();
        logger.debug("Server started .... ");
        verifyUsersActivityTimer();

        try {
            serverConnectSocket = new ServerSocket(Integer.parseInt(ConfigServer.getPortConnection()));
            serverChattingSocket = new ServerSocket(Integer.parseInt(ConfigServer.getPortChatting()));
            final Group groupMain = new Group("group000", "Main chat");
            groupMain.saveGroup();
            groupsList.add(groupMain);
            while (socketListensForConnections) {
                try {
                    clientConnectionSocket = serverConnectSocket.accept();
                    connection = new Connection(this, clientConnectionSocket);
                    logger.debug("Client's socket was accepted: [" + clientConnectionSocket.getInetAddress().getHostAddress()
                            + ":" + clientConnectionSocket.getPort() + "]. Connection success.");
                } catch (IOException e) {
                    logger.error("Connection exception " + e);
                }
            }
        } catch (IOException e) {
            logger.error("Server socket exception " + e);
        }
    }

    /**
     * Uses Singleton pattern.
     * @return created Server object or new Server object if server is not yet created.
     */
    public static Server startServer() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    /**
     * Stops the server. Only admin can started this method.
     */
    private void stopServer() {
        for (Connection entry : connections) {
            if (!entry.getUser().getLogin().equals(ConfigServer.getRootAdmin())) {
                Message msg = new Message(Message.QUIT_TYPE, "Server was stopped. Try to connect later");
                msg.setLogin(entry.getUser().getLogin());
                entry.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                connectionDisconnect(entry);
            }
        }
    }

    @Override
    public synchronized void connectionCreated(Connection data) {
        connections.add(data);
        updateUserActivity(data);
        Set<String> currentUserGroups = data.getUser().getGroups();
        for (String entry: currentUserGroups) {
            for (Group group: groupsList) {
                if (entry.equalsIgnoreCase(group.getGroupID())) {
                    group.addUser(data.getUser().getLogin());
                    group.saveGroup();
                }
            }
        }
    }

    @Override
    public synchronized void connectionDisconnect(Connection data) {
        connections.remove(data);
        logger.debug("Sending to all info about connection closed from " + data.getUser().getLogin());
        Message msg = new Message(Message.EXIT_TYPE, " has disconnected.");
        msg.setLogin(data.getUser().getLogin());
        sendToAll(HandleXml.marshallingWriter(Message.class, msg));
        data.disconnect();
    }

    /**
     * Server logic of work with inputstreams from clients.
     * @param data current connection.
     * @param text text from a current connection (client).
     */
    @Override
    public synchronized void receivedMessage(Connection data, String text) {
        connection = data;
        try {
            message = HandleXml.unMarshallingMessage(text);
        } catch (JAXBException e) {
            logger.error("unMarshallingMessage " + e);
        }

        /* Registration block */
        if (message.getType() == Message.REGISTER_TYPE) {
            Message messageSend = new Message(Message.REGISTER_TYPE,"");
            if (verificationName(message.getLogin())) {
                connection.setUser(new User(message.getLogin(), message.getPassword()));
                connection.getUser().addGroup("group000");
                connectionCreated(connection);
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("true, port=" + ConfigServer.getPortChatting());
                messageSend.setGroupList(getOnlineUsers());
                connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, messageSend));
                moveToChattingSocket();
            } else {
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("false");
                connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, messageSend));
            }
        }

        /* Sign in block */
        else if (message.getType() == Message.SINGIN_TYPE) {
            Message messageSend = new Message(Message.SINGIN_TYPE,"");
            if (verificationSingIn(message.getLogin(), message.getPassword())) {
                connection.setUser(new User(message.getLogin(), message.getPassword()));
                connection.getUser().setGroups(user.getGroups());
                connectionCreated(connection);
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("true, port=" + ConfigServer.getPortChatting());
                messageSend.setGroupList(getOnlineUsers());
                connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, messageSend));
                moveToChattingSocket();

                Message messageSend1 = new Message(Message.SINGIN_TYPE,"update");
                Set<Group> groupSet = Group.groupUser(user.getGroups());
                groupsList.addAll(groupSet);
                logger.debug("GroupsList after user connected: " + groupsList.toString());

                messageSend1.setGroupListUser(groupSet);
                connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, messageSend1));

            } else {
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("false");
                connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, messageSend));
            }
        }

        /* Here chatting between users works.*/
        else if (message.getType() == Message.CHATTING_TYPE)  {

            /* Admin's block of commands*/
            if ((message.getLogin().equals(ConfigServer.getRootAdmin()))
                    && (message.getMessage().equals("/stopServer"))) {
                message.setMessage("Server was stopped. To change server config, use: /set portconnection int, /set portchatting int, /set timeout int");
                connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
                stopServer();
                socketListensForConnections = false;
                try {
                    serverConnectSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if ((message.getLogin().equals(ConfigServer.getRootAdmin()))
                    &&(message.getMessage().startsWith("/set portconnection")
                    || message.getMessage().startsWith("/set portchatting")
                    || message.getMessage().startsWith("/set timeout")
                    && connections.size() == 1)) {
                String[] command = message.getMessage().split(" ");
                if (command.length == 3) {
                   ConfigServer.setConfig(command[1], command[2]);
                   logger.debug("Server Config field" + command[1] + " changed to: " + command[2]);
                }
            }

            else if ((message.getLogin().equals(ConfigServer.getRootAdmin()))
                    &&(message.getMessage().equals("/relaunchServer"))) {
                relaunchServer();
            }

            else if ((message.getLogin().equals(ConfigServer.getRootAdmin()))
                    && (message.getMessage().startsWith("/kick")
                    || message.getMessage().startsWith("/ban")
                    || message.getMessage().startsWith("/unban")
                    || message.getMessage().startsWith("/help"))) {

                String[] commands = message.getMessage().split(" ");

                if (commands.length > 1) {
                    for (Group g : groupsList) {
                        if (g.getGroupID().equals(message.getGroupID())) {
                            for (String c : g.getUsersList()) {
                                if (commands[1] != null && commands[1].equals(c)) {
                                    for (Connection findUser : connections) {
                                        if (findUser.getUser().getLogin().equals(c) && !c.equals(ConfigServer.getRootAdmin())) {
                                            Message msg = new Message(Message.CHATTING_TYPE, "");
                                            msg.setLogin(findUser.getUser().getLogin());
                                            msg.setGroupID(g.getGroupID());
                                            if ((message.getMessage().startsWith("/kick")) && (commands.length > 1)) {
                                                msg.setType(Message.KICK_TYPE);
                                                logger.debug("Server sends to Client into ServerConnection kick_type message with login and groupID: " + g.getGroupID());
                                                findUser.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                                            }
                                            else if ((message.getMessage().startsWith("/ban")) && (commands.length > 3)) {
                                                msg.setType(Message.BAN_TYPE);
                                                msg.setBanned(true);
                                                msg.setMessage("You've banned for " + commands[2] + " minutes. Reason: " + commands[commands.length - 1]);
                                                logger.debug("Cервер забанил юзера " + msg.getLogin() + " на " + commands[2] + " минут");
                                                findUser.getUser().setBanInterval(Integer.parseInt(commands[2]));
                                                findUser.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                                                message.setMessage(findUser.getUser().getLogin() + " was banned.");
                                                connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
                                            }
                                            else if ((message.getMessage().startsWith("/unban")) && (commands.length > 1)) {
                                                findUser.getUser().unban();
                                                msg.setType(Message.BAN_TYPE);
                                                msg.setBanned(false);
                                                findUser.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                                                message.setMessage(findUser.getUser().getLogin() + " was unbanned.");
                                                connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    message.setMessage("Kick format: /kick user. Ban format: /ban user time(in minutes) " +
                            "reason(only 1 word). Unban format: /unban user. Relaunch server: /relaunchServer");
                    connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
                }

            /* Chatting block user to user works here.*/
            } else {
                for (Group entry: groupsList) {
                    logger.debug("Groups in groupsList when received chatting_type message: " + groupsList.toString());
                    if (entry.getGroupID().equalsIgnoreCase(message.getGroupID())) {
                        HistoryMessage historyMessage = new HistoryMessage(message.getLogin(), message.getMessage());
                        entry.addToListMessage(historyMessage);
                        entry.saveGroup();
                        sendToAllGroup(entry.getGroupID(), HandleXml.marshallingWriter(Message.class, message));
                    }
                }
            }
            logger.debug("Who wrote from server side: " + connection.getUser().getLogin() + "\n");
            updateUserActivity(connection);
        }

        /* Updates list of online users in the chat. */
        else if (message.getType() == Message.UPDATE_USERS_TYPE) {
           message.setGroupList(getOnlineUsers());
           sendToAllGroup(message.getGroupID(), HandleXml.marshallingWriter(Message.class, message));
        }

        /* Makes a private chat with one user */
        else if (message.getType() == Message.PRIVATE_CHAT_TYPE) {
            Group group = new Group("group" + getUniqueID(), "Private");
            group.saveGroup();
            //Group group = new Group("group00" + groupsList.size());
            groupsList.add(group);
            message.setGroupID(group.getGroupID());
            ArrayList<String> usersInCurrentGroup = message.getGroupList();
            for (String s: usersInCurrentGroup) {
                for (Connection entry: connections) {
                    if (entry.getUser().getLogin().equals(s)) {
                        entry.getUser().addGroup(group.getGroupID());
                        group.addUser(entry.getUser().getLogin());
                        group.saveGroup();
                        entry.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
                        group.getUsersList().stream().forEach(x -> logger.debug("Who is in group list: " + x));
                    }
                }
            }
        }

        /* Adding user(s) to a private chat. A private group starts here. */
        else if (message.getType() == Message.PRIVATE_GROUP_TYPE) {
            logger.debug("Adding " + message.getLogin() + " to the group " + message.getGroupID());
            ArrayList<String> result = new ArrayList<>();

            for (Connection entry: connections) {
                if (entry.getUser().getLogin().equals(message.getLogin())) {
                    entry.getUser().addGroup(message.getGroupID());
                    for (Group g : groupsList) {
                        if (g.getGroupID().equals(message.getGroupID())) {
                            g.addUser(entry.getUser().getLogin());
                            g.setGroupName(message.getGroupTitle());
                            g.saveGroup();
                            result.addAll(g.getUsersList());
                        }
                    }
                    message.setGroupList(result);
                    logger.debug("Users list after adding a new member to the group: " + message.getGroupList());
                    entry.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
                }
            }

            Message msg = new Message();
            ArrayList<String> newUserList = new ArrayList<>();

            newUserList.addAll(result);

            msg.setGroupList(newUserList);
            msg.setType(Message.TAB_RENAME_TYPE);
            msg.setGroupID(message.getGroupID());
            msg.setGroupTitle(message.getGroupTitle());
            result.remove(message.getLogin());

            for (String users: result){
                for (Connection entry: connections){
                    if(entry.getUser().getLogin().equals(users)){
                        logger.debug("SERVER: Users list when started tab rename tab: " + msg.getGroupList());
                        entry.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                    }
                }
            }
        }

        /* Finds online users that are not members of a current group */
        else if (message.getType() == Message.UNIQUE_ONLINE_USERS_TYPE) {
            ArrayList<String> result = new ArrayList<>();
            for (Group g: groupsList) {
                if (message.getGroupID().equals(g.getGroupID())) {
                    Set<String> tmp = new LinkedHashSet<>(groupsList.iterator().next().getUsersList());
                    for (String c: g.getUsersList()) {
                        if (!tmp.add(c)) {
                            tmp.remove(c);
                        }
                    }
                    for (String c : tmp) {
                        result.add(c);
                        logger.debug("Online user, that is not presented in current group: " + c);
                    }
                }
            }
            message.setGroupList(result);
            connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        }

        /* User leaves the current group and stays online in a chat */
        else if (message.getType() == Message.LEAVE_GROUP_TYPE) {
            for (Group g: groupsList) {
                if (message.getGroupID().equals(g.getGroupID())) {
                    for (String c : g.getUsersList()) {
                        if (message.getLogin().equals(c)) {
                            for (Connection findUser: connections) {
                                if (findUser.getUser().getLogin().equals(c)) {
                                    findUser.getUser().removeGroup(g.getGroupID());
                                }
                            }
                            message.setMessage(c + " has left from the group.");
                            g.removeUser(c);
                            g.saveGroup();
                            if (g.getUsersList().size() == 0) {
                                removeGroup(g.getGroupID());
                                groupsList.remove(g);
                            }
                            //c.user.getGroups().remove(g.getGroupID());
                            break;
                        }
                    }
                    message.setType(Message.CHATTING_TYPE);
                    sendToAllGroup(g.getGroupID(), HandleXml.marshallingWriter(Message.class, message));
                    message.setGroupList(new ArrayList<>(g.getUsersList()));
                    message.setType(Message.UPDATE_USERS_TYPE);
                    sendToAllGroup(g.getGroupID(), HandleXml.marshallingWriter(Message.class, message));
                }
            }
        }

        /* Quit the client */
        else if (message.getType() == Message.EXIT_TYPE) {
            connectionDisconnect(connection);
        }
    }

    /**
     * Method generate an unique ID
     * @return String type of unique ID
     */
    private  String getUniqueID() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date now = new Date();
        String uniqueID = dateFormat.format(now);
        return uniqueID;
    }

    private void sendToAll(String text) {
        for (Connection entry: connections) {
            for (Group g : groupsList) {
                for (String s : g.getUsersList()) {
                    if (entry.getUser().getLogin().equals(s)) {
                        entry.sendToOutStream(text);
                    }
                }
            }
        }
    }

    private void sendToAllGroup(String groupID, String text) {
        for (Group g: groupsList) {
            if (groupID.equals(g.getGroupID())) {
                for (String line: g.getUsersList()) {
                    for (Connection entry: connections) {
                        if (entry.getUser().getLogin().equals(line)) {
                            logger.info("Method sendToAllGroup is working now. Who is in this group now: " + line + ", message: " + text);
                            entry.sendToOutStream(text);
                        }
                    }
                }
            }
        }
    }

    private boolean verificationName(String login) {
        File file = new File("User" + File.separator + login.hashCode() + ".xml");

        if (file.isFile()) {
            return false;
        }
        return true;
    }

    private boolean verificationSingIn(String login, String password) {
        File file = new File("User" + File.separator + login.hashCode() + ".xml");

        if (file.isFile()) {
            user = (User) HandleXml.unMarshalling(file, User.class);
            for (Connection connect : connections) {
                if (connect.getUser().getLogin().equals(login)) {
                    return false;
                }
            }
            if (password.equals(user.getPassword())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method moves connection to another socket where chatting starts.
     */
    private void moveToChattingSocket() {
        try {
            Socket clientChatSocket = serverChattingSocket.accept();
            logger.debug("Connection has moved to new socket for chatting: " +
                    clientChatSocket.getInetAddress() + " " + clientChatSocket.getLocalPort());
            connection.setSocket(clientChatSocket);
        } catch (IOException e) {
            logger.error("Error socket creation" + e);
        }
    }

    /**
     * Method returns an ArrayList of all online users
     */
    private ArrayList<String> getOnlineUsers() {
        ArrayList<String> result = new ArrayList<>();
        for (Connection entry : connections) {
            result.add(entry.getUser().getLogin());
        }
        return result;
    }

    /**
     * Method starts the timer
     */
    private void verifyUsersActivityTimer() {
        logger.debug("Timer started");
        //if (connections.size() > 0) {
            ses.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    logger.debug("SERVER: Checks activity and unban-status every minute.");
                    checkActivityAndUnbans();
                }
            }, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Method checks for user's activity and checks if it is needed to unban user.
     * User's inactive period defines in server.properties file.
     */
    private void checkActivityAndUnbans() {
        if (connections.size() > 0) {
            for (Connection entry : connections) {
                logger.debug("Searching inactive person");
                if ((!entry.getUser().getLogin().equals(ConfigServer.getRootAdmin())
                        && ((System.currentTimeMillis() - entry.getUser().getLastActivity()) >= Long.parseLong(ConfigServer.getTimeOut())))) {
                    logger.debug("Check activity: " + System.currentTimeMillis() + "user's lastActivity: " +
                            entry.getUser().getLastActivity());
                    Message msg = new Message(Message.QUIT_TYPE, "");
                    msg.setLogin(entry.getUser().getLogin());
                    entry.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                    logger.debug(entry.getUser().getLogin() + "\'s inactivity has reached " +
                            ConfigServer.getTimeOut() + " milliseconds. "
                            + entry.getUser().getLogin() + " has disconnected.");
                }
                if (entry.getUser().isBan() && entry.getUser().readyForUnban()) {
                    logger.debug("Unbanning user: " + entry.getUser().getLogin());
                    Message msg = new Message(Message.BAN_TYPE, entry.getUser().getLogin() + " was unbanned.");
                    msg.setLogin(entry.getUser().getLogin());
                    msg.setBanned(false);
                    entry.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                    connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
                }
            }
        }
    }

    /**
     * Method updates user's activity.
     * @param connect current connection
     */
    private void updateUserActivity(Connection connect){
        logger.debug("SERVER: Update user's activity: " + connect.getUser().getLogin());
        connect.getUser().setLastActivity(System.currentTimeMillis());
    }

    private void removeGroup(String group) {
        File file = new File(System.getProperty("user.dir") + File.separator + "Group" + File.separator + group + ".xml");
        logger.debug("Deleting " + System.getProperty("user.dir") + File.separator + "Group" + File.separator + group + ".xml");
        file.delete();
    }

    /**
     * Method just closes the program.
     */
    private void relaunchServer() {
        Message msg = new Message(Message.QUIT_TYPE, "");
        connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
        System.exit(0);
    }
}
