package ua.woochat.server.model.commands;

import ua.woochat.app.Connection;
import ua.woochat.app.Message;

/**
 * Implementing this interface allows an object to handle commands between client and server.
 */
public interface Commands {
    /**
     * Executes the current command.
     * @param curConnection current connection.
     * @param message an instance of Message object for transmitting information between client and server.
     */
    void execute(Connection curConnection, Message message);
}
