package ua.woochat.server.model.commands;

import org.apache.log4j.Logger;
import ua.woochat.app.*;
import ua.woochat.server.model.Connections;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class creates a private conversation between two users.
 */
public class PrivateChat implements Commands {
    private final static Logger logger = Logger.getLogger(PrivateChat.class);

    @Override
    public void execute(Connection curConnection, Message message) {
        Group group = new Group("group" + getUniqueID(), "Private");
        group.saveGroup();
        Connections.getGroupsList().add(group);
        message.setGroupID(group.getGroupID());
        ArrayList<String> usersInCurrentGroup = message.getGroupList();
        for (String s: usersInCurrentGroup) {
            for (Connection entry: Connections.getConnections()) {
                if (entry.getUser().getLogin().equals(s)) {
                    entry.getUser().addGroup(group.getGroupID());
                    group.addUser(entry.getUser().getLogin());
                    group.saveGroup();
                    entry.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
                    group.getUsersList().stream().forEach(x -> logger.debug("Who is in group list: " + x));
                }
            }
        }
        Connections.addGroupToGroupsList(group);
        Connections.getGroupsList().stream().forEach(x -> logger.debug("Group in groups list: " + x.getGroupID()));
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
}
