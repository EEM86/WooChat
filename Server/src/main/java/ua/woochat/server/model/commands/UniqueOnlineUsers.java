package ua.woochat.server.model.commands;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.Group;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.server.model.Connections;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class sends to client information about online users that are not presented in current private group.
 */
public class UniqueOnlineUsers implements Commands {
    private final static Logger logger = Logger.getLogger(UniqueOnlineUsers.class);

    @Override
    public void execute(Connection curConnection, Message message) {
        ArrayList<String> result = new ArrayList<>();
        for (Group g: Connections.getGroupsList()) {
            if (message.getGroupID().equals(g.getGroupID())) {
                Set<String> tmp = new LinkedHashSet<>(Connections.getGroupsList().iterator().next().getUsersList());
                for (String c: g.getUsersList()) {
                    if (!tmp.add(c)) {
                        tmp.remove(c);
                    }
                }
                result.addAll(tmp);
            }
        }
        message.setGroupList(result);
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
    }
}
