package ua.woochat.server.model.commands.admincmds;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.Message;
import ua.woochat.server.model.ConfigServer;
import ua.woochat.server.model.commands.Commands;

/**
 * Sets properties to server config file serverExtracted.properties.
 */
public class SetCommand implements Commands {
    private final static Logger logger = Logger.getLogger(SetCommand.class);
    private String property;
    private String newParameter;

    @Override
    public void execute(Connection curConnection, Message message) {
        String[] arrCommands = message.getMessage().split(" ");
        if (arrCommands.length == 3) {
            property = arrCommands[1];
            newParameter = arrCommands[arrCommands.length - 1];
            ConfigServer.setConfig(property, newParameter);
            logger.debug("Server Config field" + property + " changed to: " + newParameter);
        }
    }
}
