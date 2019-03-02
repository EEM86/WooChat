package ua.woochat.client.listeners;

import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.client.view.ChatForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ChatFormListener implements ActionListener {
    private ChatForm chatForm;

    public ChatFormListener(ChatForm chatForm) {
        this.chatForm = chatForm;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("sendButton")) {
            String message = chatForm.getMessageField().getText();
            if (message.equals("")){}
            else sendMessage(message);
        }

        if (e.getActionCommand().equals("enterPressed")) {
            String message = chatForm.getMessageField().getText();
            if (message.equals("")){}
            else sendMessage(message);
        }
    }

    /**
     * Метод отправляет запрос на создание приватного чата с другим пользователем
     * @param user1 имя текущего пользователя
     * @param user2 имя пользователя с которым создается приватный чат
     */
    public void requestGroup(String user1, String user2) {
        Message message = new Message(6, "");
        ArrayList<String> listUsers = new ArrayList<>();
        listUsers.add(user1);
        listUsers.add(user2);
        message.setGroupList(listUsers);
        chatForm.getServerConnection().sendToServer(HandleXml.marshalling1(Message.class, message));
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
        String str = HandleXml.marshalling1(Message.class, message);
        try {
            chatForm.getServerConnection().sendToServer(str);
        }catch (NullPointerException e){
            System.out.println("Сообщение не отправлено");
        }
    }
}
