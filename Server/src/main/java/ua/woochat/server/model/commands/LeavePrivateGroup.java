package ua.woochat.server.model.commands;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.Group;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.server.model.Connections;

import java.io.File;
import java.util.ArrayList;

/**
 * This class handles leaving the private group.
 */
public class LeavePrivateGroup implements Commands {
    private final static Logger logger = Logger.getLogger(LeavePrivateGroup.class);

    @Override
    public void execute(Connection curConnection, Message message) {
        for (Group g: Connections.getGroupsList()) {
            if (message.getGroupID().equals(g.getGroupID())) {
                for (String c : g.getUsersList()) {
                    if (message.getLogin().equals(c)) {
                        for (Connection findUser: Connections.getConnections()) {
                            if (findUser.getUser().getLogin().equals(c)) {
                                findUser.getUser().removeGroup(g.getGroupID());
                            }
                        }
                        message.setMessage(c + " has left from the group.");
                        g.removeUser(c);
                        g.saveGroup();
                        if (g.getUsersList().size() == 0) {
                            removeGroup(g.getGroupID());
                            Connections.getGroupsList().remove(g);
                        }
                        break;
                    }
                }
                message.setType(Message.CHATTING_TYPE);
                Connections.sendToAllGroup(g.getGroupID(), HandleXml.marshallingWriter(Message.class, message));
                message.setGroupList(new ArrayList<>(g.getUsersList()));
                message.setType(Message.UPDATE_USERS_TYPE);
                message.setAdminName(Message.administrator);
                Connections.sendToAllGroup(g.getGroupID(), HandleXml.marshallingWriter(Message.class, message));
            }
        }
    }
    //ToDo fix deleting
    private void removeGroup(String group) {
        File file = new File("Group" + File.separator + group + ".xml");
        logger.debug("Deleting " + System.getProperty("user.dir") + File.separator + "Group" + File.separator + group + ".xml");
        file.delete();
    }
}
