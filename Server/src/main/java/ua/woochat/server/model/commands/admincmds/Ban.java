package ua.woochat.server.model.commands.admincmds;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.server.model.Connections;
import ua.woochat.server.model.commands.Commands;

/**
 * This class executes ban command.
 */
public class Ban implements Commands {
    private final static Logger logger = Logger.getLogger(Ban.class);
    private int banPeriod;
    private String reason;

    @Override
    public void execute(Connection curConnection, Message message) {
        String[] banCommandToArr = message.getMessage().split(" ");
        if (banCommandToArr.length > 3) {
            String userLoginToBan = banCommandToArr[1];
            Connection connectionToBan = Connections.getConnectionByLogin(userLoginToBan);
            banPeriod = Integer.parseInt(banCommandToArr[2]);
            reason = banCommandToArr[banCommandToArr.length - 1];
            message.setType(Message.BAN_TYPE);
            message.setBanned(true);
            message.setMessage("You've banned for " + banPeriod + " minutes. Reason: " + reason);
            logger.debug("Cервер забанил юзера " + userLoginToBan + " на " + banPeriod + " минут");
            connectionToBan.getUser().setBanInterval(banPeriod);
            connectionToBan.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
            message.setType(Message.CHATTING_TYPE);
            message.setMessage(curConnection.getUser().getLogin() + " was banned.");
            curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        }
    }
}
