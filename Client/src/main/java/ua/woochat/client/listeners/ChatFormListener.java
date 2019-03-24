package ua.woochat.client.listeners;

import org.apache.log4j.Logger;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.client.view.ChatForm;
import ua.woochat.client.view.MessageView;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Class describes methods for handling chat form events
 */
public class ChatFormListener implements ActionListener {
    private ChatForm chatForm;
    private final static Logger logger = Logger.getLogger(ChatFormListener.class);

    public ChatFormListener(ChatForm chatForm) {
        this.chatForm = chatForm;
    }

    /**
     * Method handles the events that occurred when clicking on buttons and switching tabs
     * @param e event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("sendButton")) {
            String message = chatForm.getMessageField().getText();
            if (message.equals("")){}
            else {
                sendMessage(message);
            }
        }

        if (e.getActionCommand().equals("enterPressed")) {
            String message = chatForm.getMessageField().getText();
            if (!("").equals(message)){
                sendMessage(message);
            }
        }

        if (e.getActionCommand().equals("addUserBtn")) {
            String group = chatForm.getConversationPanel().getTitleAt(chatForm.getConversationPanel().getSelectedIndex());

            Message msg = new Message(Message.UNIQUE_ONLINE_USERS_TYPE, "");
            msg.setGroupID(group);

            chatForm.getServerConnection().connectionCheck();
            chatForm.getServerConnection().sendToServer(HandleXml.marshallingWriter(Message.class, msg));

            chatForm.getChatForm().setEnabled(false);
            chatForm.getAddUserListForm().setVisible(true);

            ArrayList<String> s1 = chatForm.getServerConnection().getOnlineState().get(group);

            chatForm.getGroupTextField().setText("");

            if (s1.size() > 2) {
                chatForm.getGroupTextField().setEnabled(false);
                chatForm.getGroupTextField().setText("Unavailable");
            }else {
                chatForm.getGroupTextField().setEnabled(true);
            }
        }

        if (e.getActionCommand().equals("addUser")) {
            int idx = chatForm.getAddUserList().getSelectedIndex();
            if (idx == -1){
                new MessageView("Select a user", chatForm.getAddUserListForm(),false);
            }else{
                if(chatForm.getGroupTextField().isEnabled()){
                    if(chatForm.getGroupTextField().getText().equals("")) {
                         new MessageView("Enter group name", chatForm.getAddUserListForm(),false);
                }else{
                        String user2 = chatForm.getAddUserModel().get(idx);
                        String groupID = chatForm.getConversationPanel().getTitleAt(chatForm.getConversationPanel().getSelectedIndex());
                        String groupName = chatForm.getGroupTextField().getText();
                        addUserToCurrentGroup(user2,groupID,groupName);

                        chatForm.getChatForm().setEnabled(true);
                        chatForm.getAddUserListForm().setVisible(false);
                }
                }else {
                    String user2 = chatForm.getAddUserModel().get(idx);
                    String groupID = chatForm.getConversationPanel().getTitleAt(chatForm.getConversationPanel().getSelectedIndex());

                    int index = chatForm.getConversationPanel().getSelectedIndex();
                    ChatForm.TabTitle ob = (ChatForm.TabTitle) chatForm.getConversationPanel().getTabComponentAt(index);
                    JLabel jLabel = ob.getLbl();
                    String groupName = jLabel.getText();

                    addUserToCurrentGroup(user2,groupID,groupName);

                    chatForm.getChatForm().setEnabled(true);
                    chatForm.getAddUserListForm().setVisible(false);
                }
            }
        }
    }

    /**
     * Method updates the list of users to add.
     * @param list list of users available to add
     */
    public void reNewAddList(ArrayList<String> list) {
        chatForm.getAddUserScrollPane().setVisible(false);
        chatForm.getAddUserModel().clear();

        int i=0;
        for (String entry: list) {
            chatForm.getAddUserModel().add(i, entry);
            i++;
        }
        chatForm.getAddUserScrollPane().setVisible(true);
    }

    /**
     * Method sends a request to create a private chat with another user.
     * @param user1 current user name
     * @param user2 username with which a private chat is being created
     */
    public void privateGroupCreate(String user1, String user2) {
        chatForm.getServerConnection().connectionCheck();
        Message message = new Message(Message.PRIVATE_CHAT_TYPE, "");
        ArrayList<String> listUsers = new ArrayList<>();
        listUsers.add(user1);
        listUsers.add(user2);
        message.setGroupList(listUsers);
        chatForm.getServerConnection().sendToServer(HandleXml.marshallingWriter(Message.class, message));
    }

    /**
     * Method adds a user to an existing group
     * @param name name of user to add
     * @param groupID group id
     * @param groupName group title
     */
    private void addUserToCurrentGroup(String name, String groupID, String groupName) {
        Message msg = new Message(Message.PRIVATE_GROUP_TYPE, "Connected" + name + " to " + groupID);
        msg.setLogin(name);
        msg.setGroupID(groupID);
        msg.setGroupTitle(groupName);
        chatForm.getServerConnection().sendToServer(HandleXml.marshallingWriter(Message.class, msg));
    }

    /**
     * Method sends a text message to the server.
     * @param text text message
     */
    private void sendMessage(String text) {

        String name = chatForm.getServerConnection().connection.getUser().getLogin();
        Message message = new Message(Message.CHATTING_TYPE, text);
        message.setLogin(name);
        message.setGroupID(chatForm.getConversationPanel().getTitleAt(chatForm.getConversationPanel().getSelectedIndex()));

        try {
            chatForm.getServerConnection().connectionCheck();
            chatForm.getServerConnection().sendToServer(HandleXml.marshallingWriter(Message.class, message));
        } catch (NullPointerException e){
            logger.error("getServerConnection ", e);
        } finally {
            chatForm.getMessageField().setText("");
        }
    }

    /**
     * Method is called when the user leaves the group.
     * @param groupID id of the group that the user leaves
     */
    public void pressedCloseGroup(String groupID){
        chatForm.getServerConnection().leaveGroup(groupID);
    }
}
