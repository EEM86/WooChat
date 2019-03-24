package ua.woochat.server.model.commands;

import ua.woochat.app.Connection;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;

public class Ping implements Commands {
    @Override
    public void execute(Connection curConnection, Message message) {
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
    }
}
