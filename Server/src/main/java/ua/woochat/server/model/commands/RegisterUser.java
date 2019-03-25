package ua.woochat.server.model.commands;

import ua.woochat.app.Connection;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.app.User;
import ua.woochat.server.model.ConfigServer;
import ua.woochat.server.model.Connections;

import java.io.File;

/**
 * This class handles user registration.
 */
public class RegisterUser implements Commands {

    @Override
    public void execute(Connection curConnection, Message message) {
        Message messageSend = new Message(Message.REGISTER_TYPE,"");
        if (verify(message.getLogin())) {
            curConnection.setUser(new User(message.getLogin(), message.getPassword()));
            curConnection.getUser().addGroup("group000");
            messageSend.setLogin(message.getLogin());
            messageSend.setMessage("true, port=" + ConfigServer.getPortChatting());
            messageSend.setGroupList(Connections.getOnlineUsersLogins());
            curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, messageSend));
        } else {
            messageSend.setLogin(message.getLogin());
            messageSend.setMessage("false");
            curConnection.sendToOutStream(HandleXml.marshallingWriter(Message.class, messageSend));
        }
    }

    private boolean verify(String login) {
        File file = new File("User" + File.separator + login.hashCode() + ".xml");
        return !file.isFile();
    }
}
