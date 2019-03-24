package ua.woochat.server.model.commands;

import org.apache.log4j.Logger;
import ua.woochat.app.*;
import ua.woochat.server.controller.Server;
import ua.woochat.server.model.ConfigServer;
import ua.woochat.server.model.Connections;

import java.util.Set;
/**
 * This class handles chatting between users.
 * Also this class defines and executes admin commands.
*/
public class Chatting implements Commands{
    private final static Logger logger = Logger.getLogger(Chatting.class);

    private final String stopServer = "/stopServer";
    private final String setPortConnect = "/set portConnection";
    private final String setPortChatting = "/set portChatting";
    private final String setTimeout = "/set timeout";
    private final String relaunchServer = "/relaunchServer";
    private final String kickCommand = "/kick";
    private final String banCommand = "/ban";
    private final String unbanCommand = "/unban";
    private final String helpCommand = "/help";
    private String admin = ConfigServer.getRootAdmin();

    public String getAdmin() {
        return admin;
    }

    @Override
    public void execute(Connection curConnection, Message message) {
        String context = message.getMessage();

            if (context.equals(stopServer) && message.getLogin().equals(ConfigServer.getRootAdmin())) {
                doStopCommand(curConnection, message);
            } else if (isSetCommand(message) && message.getLogin().equals(ConfigServer.getRootAdmin())) {
                doSetCommand(message);
            } else if (context.equals(relaunchServer) && message.getLogin().equals(ConfigServer.getRootAdmin())) {
                Server.relaunchServer(curConnection);
            } else if (isKickBanCommand(message) && message.getLogin().equals(ConfigServer.getRootAdmin())) {
                doKickBanCommand(curConnection, message);
            } else if (message.getMessage().equals(helpCommand) && message.getLogin().equals(ConfigServer.getRootAdmin())) {
                doHelpCommand(curConnection, message);
        } else {
            for (Group entry: Connections.getGroupsList()) {
                logger.debug("Groups in groupsList when received chatting_type message: " + Connections.getGroupsList().toString());
                if (entry.getGroupID().equalsIgnoreCase(message.getGroupID())) {
                    HistoryMessage historyMessage = new HistoryMessage(message.getLogin(), message.getMessage());
                    entry.addToListMessage(historyMessage);
                    entry.saveGroup();
                    Server.sendToAllGroup(entry.getGroupID(), HandleXml.marshallingWriter(Message.class, message));
                }
            }
            logger.debug("Who wrote from server side: " + curConnection.getUser().getLogin() + "\n");
            Server.updateUserActivity(curConnection);
        }
    }

    /**
     * Prints list of available commands to admin.
     * @param curConnection current connection.
     * @param message an instance of Message object for transmitting information between client and server.
     */
    private void doHelpCommand(Connection curConnection, Message message) {
        message.setLogin("WooChat");
        message.setMessage("****************** Admin Commands *******************");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("Kick format: /kick user");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("Ban format: /ban user time(in minutes) reason(only 1 word)");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("Unban format: /unban user");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("Stop server: /stopServer");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("To change server config, use: /set portConnection int, /set portChatting int, /set timeout int");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("Relaunch server: /relaunchServer");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("*********************** END ***************************");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
    }

    /**
     * Handles kick, ban, unban commands.
     * @param curConnection current connection.
     * @param message current message.
     */
    private void doKickBanCommand(Connection curConnection, Message message) {
        String[] kickBanCommands = message.getMessage().split(" ");

        if (kickBanCommands.length > 1) {
            String userToAct = kickBanCommands[1];
            Set<String> usersInGroup = Connections.getUsersListFromGroup(message.getGroupID());
            for (String entry : usersInGroup) {
                if ((!entry.equals(getAdmin()) && (userToAct != null) && (userToAct.equals(entry)))) {
                    Connection currentUser = Connections.getConnectionByLogin(entry);
                    Message msg = new Message(Message.CHATTING_TYPE, entry, message.getGroupID());
                    if (message.getMessage().startsWith(kickCommand) && !message.getGroupID().equals("group000")) {
                        msg.setType(Message.KICK_TYPE);
                        logger.debug("Server sends to Client into ServerConnection kick_type message with login and groupID: " + message.getGroupID());
                        currentUser.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                    } else if ((message.getMessage().startsWith(banCommand)) && (kickBanCommands.length > 3)) {
                        int banPeriod = Integer.parseInt(kickBanCommands[2]);
                        msg.setType(Message.BAN_TYPE);
                        msg.setBanned(true);
                        msg.setMessage("You've banned for " + banPeriod + " minutes. Reason: " + kickBanCommands[kickBanCommands.length - 1]);
                        logger.debug("Cервер забанил юзера " + msg.getLogin() + " на " + banPeriod + " минут");
                        currentUser.getUser().setBanInterval(banPeriod);
                        currentUser.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                        message.setMessage(currentUser.getUser().getLogin() + " was banned.");
                        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message)); // connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
                    } else if (message.getMessage().startsWith(unbanCommand)) {
                        currentUser.getUser().unban();
                        msg.setType(Message.BAN_TYPE);
                        msg.setBanned(false);
                        currentUser.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                        message.setMessage(currentUser.getUser().getLogin() + " was unbanned.");
                        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
                    }
                }

            }
        }
    }

    private boolean isKickBanCommand(Message message) {
        return (message.getMessage().startsWith(kickCommand)
                || message.getMessage().startsWith(banCommand)
                || message.getMessage().startsWith(unbanCommand));
    }

    private boolean isSetCommand(Message message) {
        return (message.getMessage().startsWith(setPortConnect)
                || message.getMessage().startsWith(setPortChatting)
                || message.getMessage().startsWith(setTimeout)
                && Connections.getCountConnections() == 1);
    }

    /**
     * Sets properties to server config file serverExtracted.properties.
     * @param message an instance of Message object for transmitting information between client and server.
     */
    private void doSetCommand(Message message) {
        String[] arrCommands = message.getMessage().split(" ");
        if (arrCommands.length == 3) {
            ConfigServer.setConfig(arrCommands[1], arrCommands[2]);
            logger.debug("Server Config field" + arrCommands[1] + " changed to: " + arrCommands[2]);
        }
    }

    /**
     * Stops the server. After that admin can set some properties.
     * @param curConnection current connection.
     * @param message an instance of Message object for transmitting information between client and server.
     */
    public void doStopCommand(Connection curConnection, Message message) {
        message.setMessage("Server was stopped. To change server config, use: /set portConnection int, /set portChatting int, /set timeout int");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        Server.stopServer();
    }
}


