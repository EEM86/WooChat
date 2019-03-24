package ua.woochat.server.model.commands.admincmds;

import ua.woochat.app.Connection;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.server.controller.Server;
import ua.woochat.server.model.commands.Commands;


public class StopServer implements Commands {

    /**
     * Stops the server. After that admin can set some properties.
     * @param curConnection current connection.
     * @param message an instance of Message object for transmitting information between client and server.
     */
    @Override
    public void execute(Connection curConnection, Message message) {
        message.setMessage("Server was stopped. To change server config, use: /set portConnection int, /set portChatting int, /set timeout int");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        Server.stopServer();
    }
}
