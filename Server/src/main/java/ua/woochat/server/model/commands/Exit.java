package ua.woochat.server.model.commands;

import ua.woochat.app.Connection;
import ua.woochat.app.Message;
import ua.woochat.server.controller.Server;

/**
 * This class sends to server disconnect request.
 */
public class Exit implements Commands {
    @Override
    public void execute(Connection curConnection, Message message) {
        Server.requestDisconnect(curConnection);
    }
}
