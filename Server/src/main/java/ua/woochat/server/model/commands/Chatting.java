package ua.woochat.server.model.commands;

import org.apache.log4j.Logger;
import ua.woochat.app.*;
import ua.woochat.server.controller.Server;
import ua.woochat.server.model.AdminCommands;
import ua.woochat.server.model.ConfigServer;
import ua.woochat.server.model.Connections;
import java.util.Map;

/**
 * This class handles chatting between users.
 * Also this class defines and executes admin commands.
*/
public class Chatting implements Commands{
    private final static Logger logger = Logger.getLogger(Chatting.class);

    @Override
    public void execute(Connection curConnection, Message message) {
        String context = message.getMessage();
        String[] commandToArr = context.split(" ");
        Map<String, Commands> adminCommandsMap = AdminCommands.getCommandsMap();
        Commands currentCommand = adminCommandsMap.get(commandToArr[0]);
        if (currentCommand != null && isAdmin(message)) {
            currentCommand.execute(curConnection, message);
        } else {
            for (Group entry : Connections.getGroupsList()) {
                logger.debug("Groups in groupsList when received chatting_type message: " + Connections.getGroupsList().toString());
                if (entry.getGroupID().equalsIgnoreCase(message.getGroupID())) {
                    HistoryMessage historyMessage = new HistoryMessage(message.getLogin(), message.getMessage());
                    entry.addToListMessage(historyMessage);
                    entry.saveGroup();
                    Connections.sendToAllGroup(entry.getGroupID(), HandleXml.marshallingWriter(Message.class, message));
                }
            }
            logger.debug("Who wrote from server side: " + curConnection.getUser().getLogin() + "\n");
            Server.updateUserActivity(curConnection);
        }
    }

    public boolean isAdmin(Message message) {
        return message.getLogin().equals(ConfigServer.getRootAdmin());
    }
}



