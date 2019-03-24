package ua.woochat.server.model.commands.admincmds;

import ua.woochat.app.Connection;
import ua.woochat.app.Message;
import ua.woochat.server.controller.Server;
import ua.woochat.server.model.commands.Commands;

/**
 * This class closes admin's connection and stops the server for manual start.
 */
public class RelaunchServer implements Commands {
    @Override
    public void execute(Connection curConnection, Message message) {
        Server.relaunchServer(curConnection);
    }
}
