package ua.woochat.client.listeners;

import org.apache.log4j.Logger;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.client.model.ServerConnection;
import ua.woochat.client.view.ChatForm;
import ua.woochat.client.view.MessageView;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ChatFormListener implements ActionListener {
    private ChatForm chatForm;
    final static Logger logger = Logger.getLogger(ServerConnection.class);

    public ChatFormListener(ChatForm chatForm) {
        this.chatForm = chatForm;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("sendButton")) {
            String message = chatForm.getMessageField().getText();
            if (message.equals("")){}
            else {
                logger.debug("Клиент:Сработала кнопка отправить: " + chatForm.getServerConnection().connection.user.getLogin());
                sendMessage(message);
            }
        }

        if (e.getActionCommand().equals("enterPressed")) {
            String message = chatForm.getMessageField().getText();
            if (message.equals("")){}
            else {
                sendMessage(message);
            }
        }

        if (e.getActionCommand().equals("addUserBtn")) {

            String group = chatForm.getConversationPanel().getTitleAt(chatForm.getConversationPanel().getSelectedIndex());

            Message msg = new Message(8, "");
            msg.setGroupID(group);

            chatForm.getServerConnection().sendToServer(HandleXml.marshallingWriter(Message.class, msg));

            chatForm.getChatForm().setEnabled(false);
            chatForm.getAddUserListForm().setVisible(true);

            ArrayList<String> s1 = chatForm.getServerConnection().getOnlineState().get(group);

            chatForm.getGroupTextField().setText("");

            if (s1.size() > 2) {
                chatForm.getGroupTextField().setEnabled(false);
                chatForm.getGroupTextField().setText("Недоступно");
            }else {
                chatForm.getGroupTextField().setEnabled(true);
            }
        }

        if (e.getActionCommand().equals("addUser")) {
            int idx = chatForm.getAddUserList().getSelectedIndex();
            if (idx == -1){
                new MessageView("Выберите пользователя", chatForm.getAddUserListForm());
            }else{
                if(chatForm.getGroupTextField().isEnabled()){
                    if(chatForm.getGroupTextField().getText().equals("")) {
                         new MessageView("Введите название группы", chatForm.getAddUserListForm());
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
     * Метод обновляет список доступных для добавления пользователей
     * @param list спикок пользователей доступных для добавления
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
     * Метод отправляет запрос на создание приватного чата с другим пользователем
     * @param user1 имя текущего пользователя
     * @param user2 имя пользователя с которым создается приватный чат
     */
    public void privateGroupCreate(String user1, String user2) {
        Message message = new Message(6, "");
        ArrayList<String> listUsers = new ArrayList<>();
        listUsers.add(user1);
        listUsers.add(user2);
        message.setGroupList(listUsers);
        chatForm.getServerConnection().sendToServer(HandleXml.marshallingWriter(Message.class, message));
    }

    public void addUserToCurrentGroup(String name, String groupID, String groupName) {
        Message msg = new Message(7, "Connected" + name + " to " + groupID);
        msg.setLogin(name);
        msg.setGroupID(groupID);
        msg.setGroupTitle(groupName);
        chatForm.getServerConnection().sendToServer(HandleXml.marshallingWriter(Message.class, msg));
    }

    /**
     * Метод отправляет текстовое сообщение на сервер
     * @param text текст сообщения
     */
    public void sendMessage(String text) {

        String name = chatForm.getServerConnection().connection.user.getLogin();
        Message message = new Message(2, text);
        message.setLogin(name);
        message.setGroupID(chatForm.getConversationPanel().getTitleAt(chatForm.getConversationPanel().getSelectedIndex()));
        logger.debug("Клиент:ID вкладки c которой отправляю: " +
                chatForm.getConversationPanel().getTitleAt(chatForm.getConversationPanel().getSelectedIndex()));
        try {
            chatForm.getServerConnection().sendToServer(HandleXml.marshallingWriter(Message.class, message));
        } catch (NullPointerException e){
            System.out.println("Сообщение не отправлено");
        } finally {
            chatForm.getMessageField().setText("");
        }
    }
    /**
     * Метод вызывается когда пользователь покидает группу
     * @param groupID имя группы которую покидает пользователь
     */
    public void pressedCloseGroup(String groupID){
        chatForm.getServerConnection().leaveGroup(groupID);
    }
}
