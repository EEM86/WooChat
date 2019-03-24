package ua.woochat.server.model.commands.admincmds;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.server.model.Connections;
import ua.woochat.server.model.commands.Commands;

/**
 * This class executes kick command.
 */
public class Kick implements Commands {
    private final static Logger logger = Logger.getLogger(Kick.class);

    @Override
    public void execute(Connection curConnection, Message message) {
        String[] kickCommandToArr = message.getMessage().split(" ");
        if (kickCommandToArr.length > 1) {
            String userLoginToKick = kickCommandToArr[1];
            Connection connectionToKick = Connections.getConnectionByLogin(userLoginToKick);
            if (!message.getGroupID().equals("group000")) {
                message.setType(Message.KICK_TYPE);
                message.setLogin(userLoginToKick);
                logger.debug("Server sends to Client into ServerConnection kick_type message with login and groupID: " + userLoginToKick + " " + message.getGroupID());
                connectionToKick.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
            }
        }
    }
}
