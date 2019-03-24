package ua.woochat.server.model.commands.admincmds;

import ua.woochat.app.Connection;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.server.model.commands.Commands;

/**
 * This class prints list of available commands to admin.
 */
public class Help implements Commands {

    @Override
    public void execute(Connection curConnection, Message message) {
        message.setType(Message.CHATTING_TYPE);
        message.setLogin("WooChat");
        message.setMessage("****************** Admin Commands *******************");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("Kick format: /kick user");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("Ban format: /ban user time(in minutes) reason(only 1 word)");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("Unban format: /unban user");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("Stop server: /stopServer");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("To change server config, use: /set portConnection int, /set portChatting int, /set timeout int");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("Relaunch server: /relaunchServer");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        message.setMessage("*********************** END ***************************");
        curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
    }
}

