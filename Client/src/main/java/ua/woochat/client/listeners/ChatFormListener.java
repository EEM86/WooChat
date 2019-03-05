package ua.woochat.client.listeners;

import org.apache.log4j.Logger;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.client.model.ServerConnection;
import ua.woochat.client.view.ChatForm;
import ua.woochat.client.view.MessageView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ChatFormListener implements ActionListener {
    private ChatForm chatForm;
    final static Logger logger = Logger.getLogger(ServerConnection.class);

    public ChatFormListener(ChatForm chatForm) {
        this.chatForm = chatForm;
    }

    private String[] virtualUserList = {"Zhe","Jon Snow", "Vasya", "Christopher"};

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("sendButton")) {
            String message = chatForm.getMessageField().getText();
            if (message.equals("")){}
            else {
                logger.debug("Сработала кнопка по нажатию мышкой");
                sendMessage(message);
            }
        }

        if (e.getActionCommand().equals("enterPressed")) {
            String message = chatForm.getMessageField().getText();
            if (message.equals("")){}
            else {
                if (message.equals("adduser")) {
                }
                logger.debug("Сработала кнопка по нажатию Enter");
                sendMessage(message);
            }
        }

        if (e.getActionCommand().equals("addUserBtn")) {

            String group = chatForm.getConversationPanel().getTitleAt(chatForm.getConversationPanel().getSelectedIndex());

            Message msg = new Message(8, "");
            msg.setGroupID(group);
            chatForm.getServerConnection().sendToServer(HandleXml.marshalling1(Message.class, msg));

            chatForm.getChatForm().setEnabled(false);
            chatForm.getAddUserListForm().setVisible(true);
        }

        if (e.getActionCommand().equals("leaveGroupBtn")) {
            logger.debug("Нажата кнопка leaveGroupBtn"); //и отсылаем на сервер сообщение о дисконнекте
        }

        if (e.getActionCommand().equals("addUser")) {
            int idx = chatForm.getAddUserList().getSelectedIndex();
            if (idx == -1){
                logger.info("task to edit not selected or missing");
                new MessageView("Выберите пользователя", chatForm.getAddUserListForm());
            }else{
                String user2 = chatForm.getAddUserModel().get(idx);
                if(user2.equals("Zhe")){
                    addUserToCurrentGroup(user2, "group001");
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
        chatForm.getServerConnection().sendToServer(HandleXml.marshalling1(Message.class, message));
    }

    public void addUserToCurrentGroup(String name, String groupID) {
        Message msg = new Message(7, "Connected" + name + " to " + groupID);
        msg.setLogin(name);
        msg.setGroupID(groupID);
        chatForm.getServerConnection().sendToServer(HandleXml.marshalling1(Message.class, msg));
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
        logger.debug("Забираю ID вкладки перед отправкой сообщения: " +
                chatForm.getConversationPanel().getTitleAt(chatForm.getConversationPanel().getSelectedIndex()));
        try {
            chatForm.getServerConnection().sendToServer(HandleXml.marshalling1(Message.class, message));
        }catch (NullPointerException e){
            System.out.println("Сообщение не отправлено");
        }
    }
}
