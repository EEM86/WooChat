package ua.woochat.server.model.commands;

import org.apache.log4j.Logger;
import ua.woochat.app.*;
import ua.woochat.server.model.ConfigServer;
import ua.woochat.server.model.Connections;

import java.io.File;

/**
 * This class handles user signing in.
 */
public class SignInUser implements Commands {
    private User user = null;
    private final static Logger logger = Logger.getLogger(SignInUser.class);

    @Override
    public void execute(Connection curConnection, Message message) {
        Message messageSend = new Message(Message.SIGNIN_TYPE,"");
        if (verificationSingIn(message.getLogin(), message.getPassword())) {
            //verifyAdmin(user, message);
            if (user.getLogin().equals(ConfigServer.getRootAdmin())) {
                //message.setAdmin(user.getLogin());
                Message.administrator = user.getLogin();
            }
            //logger.info("admin1 " + message.getAdmin());
            logger.info("admin1 " + Message.administrator);
            //Message.admin = "FFFFFFFFFFFF";
            curConnection.setUser(user);
            curConnection.getUser().setGroups(curConnection.getUser().getGroups());

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

    private boolean verificationSingIn(String curLogin, String password) {
        File file = new File("User" + File.separator + curLogin.hashCode() + ".xml");

        if (file.isFile()) {
            user = (User) HandleXml.unMarshalling(file, User.class);
            user.setAdmin(user.getLogin().equals(ConfigServer.getRootAdmin()));

            if (Connections.getUserByLogin(curLogin) != null) {
                return false;
            }
            if (password.equals(user.getPassword())) {
                return true;
            }
        }
        return false;
    }

//    private boolean isAdmin(User user) {
//        return user.getLogin().equals(ConfigServer.getRootAdmin()) ? true : false;
//    }

//    private void verifyAdmin(User curUser, Message curMessage) {
//        if (isAdmin(curUser)) {
//            user.setLogin("[Admin]" + user.getLogin());
//            curMessage.setAdmin(true);
//        }
//    }
}
