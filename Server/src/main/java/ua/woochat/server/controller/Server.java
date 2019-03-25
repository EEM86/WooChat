package ua.woochat.server.controller;

import org.apache.log4j.Logger;
import ua.woochat.app.*;
import ua.woochat.server.model.AdminCommands;
import ua.woochat.server.model.ConfigServer;
import ua.woochat.server.model.ServerCommands;
import ua.woochat.server.model.commands.Commands;
import ua.woochat.server.model.Connections;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class implements ConnectionAgent. Handles connections.
 * Creates server socket and waits for connection from a client. After connection and authorization throws client to the chatting socket.
 * Each connection creates in a new thread.
 * Server checks activity of connections and disconnects inactive users. Timeout is defined in server.properties file.
 */
public final class Server implements ConnectionAgent {

    private final static Logger logger = Logger.getLogger(Server.class);
    private static Server server;

    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private Message message;
    private Connection connection;
    private static ServerSocket serverConnectSocket;
    private ServerSocket serverChattingSocket;
    private static boolean socketListensForConnections = true;

    private Server() {
        ConfigServer.getConfigServer();
        logger.debug("Server started .... ");
        verifyUsersActivityTimer();

        try {
            serverConnectSocket = new ServerSocket(Integer.parseInt(ConfigServer.getPortConnection()));
            serverChattingSocket = new ServerSocket(Integer.parseInt(ConfigServer.getPortChatting()));
            final Group groupMain = new Group("group000", "Main chat");
            groupMain.saveGroup();
            Connections.getGroupsList().add(groupMain);
            while (socketListensForConnections) {
                try {
                    Socket clientConnectionSocket = serverConnectSocket.accept();
                    connection = new Connection(this, clientConnectionSocket);
                    logger.debug("Client's socket was accepted: [" + clientConnectionSocket.getInetAddress().getHostAddress()
                            + ":" + clientConnectionSocket.getPort() + "]. Connection success.");
                } catch (IOException e) {
                    logger.error("Connection exception ", e);
                }
            }
        } catch (IOException e) {
            logger.error("IOException error ", e);
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

    private static ServerSocket getServerConnectSocket() {
        return serverConnectSocket;
    }

    private static void setSocketListensForConnections(boolean value) {
        socketListensForConnections = value;
    }

    @Override
    public synchronized void connectionCreated(Connection data) {
        Connections.addConnection(data);
        updateUserActivity(data);
        Set<String> currentUserGroups = data.getUser().getGroups();
        for (String entry: currentUserGroups) {
            for (Group group: Connections.getGroupsList()) {
                if (entry.equalsIgnoreCase(group.getGroupID())) {
                    group.addUser(data.getUser().getLogin());
                    group.saveGroup();
                }
            }
        }
    }

    @Override
    public synchronized void connectionDisconnect(Connection data) {
//        Connections.removeConnection(data);
//        logger.debug("Sending to all info about connection closed from " + data.getUser().getLogin());
//        Message msg = new Message(Message.EXIT_TYPE, " has disconnected.");
//        msg.setLogin(data.getUser().getLogin());
//        sendToAll(HandleXml.marshallingWriter(Message.class, msg));
//        data.disconnect();
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

            Map<Integer, Commands> chatCommandsMap = ServerCommands.getCommandsMap();
            Commands currentCommand = chatCommandsMap.get(message.getType());
            if (currentCommand != null) {
                currentCommand.execute(connection, message);
                handleNewConnection();
            }
        } catch (JAXBException e) {
            logger.error("unMarshallingMessage ", e);
        }
    }

    /**
     * This method creates new connection and moves it to a chatting port.
     * Also checks admin and prints him help commands.
     */
    public void handleNewConnection() {
        if ((message.getType() == Message.REGISTER_TYPE)
                || (message.getType() == Message.SIGNIN_TYPE)
                && (connection.getUser() != null)) {
            connectionCreated(connection);
            if (AdminCommands.isConnectionAdmin(connection)) {
                Message.administrator = connection.getUser().getLogin();
            }
            moveToChattingSocket();
            Connections.updateListOfGroups(connection);
            if (AdminCommands.isConnectionAdmin(connection)) {
                message.setGroupID("group000");
                AdminCommands.printHelpAdmin(connection, message);
            }
        }
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
            logger.error("Error socket creation", e);
        }
    }

    /**
     * Method starts the timer
     */
    private void verifyUsersActivityTimer() {
        logger.debug("Timer started");
            ses.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    logger.debug("SERVER: Checks activity and unban-status every minute.");
                    checkActivityAndUnbans();
                }
            }, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Method checks for user's activity and checks unban-status.
     * User's inactive period defines in server.properties file.
     */
    private void checkActivityAndUnbans() {
        if (Connections.getCountConnections() > 0) {
            for (Connection entry : Connections.getConnections()) {
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
    public static void updateUserActivity(Connection connect){
        if (connect.getUser() != null) {
            logger.debug("SERVER: Update user's activity: " + connect.getUser().getLogin());
            connect.getUser().setLastActivity(System.currentTimeMillis());
        }
    }

    public static void requestDisconnect(Connection curConnection) {
        Connections.removeConnection(curConnection);
        if (curConnection.getUser().getLogin().equals(ConfigServer.getRootAdmin())) {
            Message.administrator = "";
        }
        logger.debug("Sending to all info about connection closed from " + curConnection.getUser().getLogin());
        Message msg = new Message(Message.EXIT_TYPE, " has disconnected.");
        msg.setAdminName(Message.administrator);
        msg.setLogin(curConnection.getUser().getLogin());
        Connections.sendToAll(HandleXml.marshallingWriter(Message.class, msg));
        curConnection.disconnect();
    }

    /**
     * Method just closes the program. Only admin can start this method.
     */
    public static void relaunchServer(Connection curConnection) {
        Message msg = new Message(Message.QUIT_TYPE, "");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
        System.exit(0);
    }

    /**
     * Stops the server. Only admin can start this method.
     */
    public static void stopServer() {
        for (Connection entry : Connections.getConnections()) {
            if (!entry.getUser().getLogin().equals(ConfigServer.getRootAdmin())) {
                Message msg = new Message(Message.QUIT_TYPE, "Server was stopped. Try to connect later");
                msg.setLogin(entry.getUser().getLogin());
                entry.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                requestDisconnect(entry);
                setSocketListensForConnections(false);
                try {
                    getServerConnectSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
