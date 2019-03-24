package ua.woochat.server.model.commands;

import ua.woochat.app.Connection;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.server.model.Connections;

/**
 * This class updates list of online users in a group.
 */
public class UpdateUsersList implements Commands {
    @Override
    public void execute(Connection curConnection, Message message) {
        message.setGroupList(Connections.getOnlineUsersLogins());
        message.setAdminName(Message.administrator);
        Connections.sendToAllGroup(message.getGroupID(), HandleXml.marshallingWriter(Message.class, message));
    }

}
