package ua.woochat.server.model.commands;

import org.apache.log4j.Logger;
import ua.woochat.app.*;
import ua.woochat.server.model.Connections;

import java.util.ArrayList;

/**
 * This class creates a private group for more than two users.
 */
public class PrivateGroup implements Commands {
    private final static Logger logger = Logger.getLogger(PrivateGroup.class);

    @Override
    public void execute(Connection curConnection, Message message) {
        logger.debug("Adding " + message.getLogin() + " to the group " + message.getGroupID());
        ArrayList<String> result = new ArrayList<>();
        Connection currentConnection = Connections.getConnectionByLogin(message.getLogin());
        currentConnection.getUser().addGroup(message.getGroupID());
        for (Group g : Connections.getGroupsList()) {
            if (g.getGroupID().equals(message.getGroupID())) {
                g.addUser(currentConnection.getUser().getLogin());
                g.setGroupName(message.getGroupTitle());
                g.saveGroup();
                result.addAll(g.getUsersList());
            }
        }
        message.setGroupList(result);
        logger.debug("Users list after adding a new member to the group: " + message.getGroupList());
        currentConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));

        Message msg = new Message();

        msg.setGroupList(result);
        msg.setType(Message.TAB_RENAME_TYPE);
        msg.setGroupID(message.getGroupID());
        msg.setGroupTitle(message.getGroupTitle());
        logger.debug("SERVER: Users list when started tab rename tab: " + msg.getGroupList());

        for (String user: result){
            currentConnection = Connections.getConnectionByLogin(user);
            currentConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg)); //msg login = null, adminName = null, message = null
        }
    }
}
