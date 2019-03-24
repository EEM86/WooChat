package ua.woochat.server.model.commands.admincmds;

import ua.woochat.app.Connection;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.server.model.Connections;
import ua.woochat.server.model.commands.Commands;

/**
 * This class unbans user.
 */
public class Unban implements Commands {
    @Override
    public void execute(Connection curConnection, Message message) {
        String[] unbanCommandToArr = message.getMessage().split(" ");
        if (unbanCommandToArr.length > 1) {
            String userLoginToUnban = unbanCommandToArr[1];
            Connection connectionToUnban = Connections.getConnectionByLogin(userLoginToUnban);
            connectionToUnban.getUser().unban();
            message.setType(Message.BAN_TYPE);
            message.setBanned(false);
            connectionToUnban.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
            message.setType(Message.CHATTING_TYPE);
            message.setMessage(curConnection.getUser().getLogin() + " was unbanned.");
            curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        }
    }
}
