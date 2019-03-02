package ua.woochat.client.listeners;

import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.client.model.ServerConnection;
import ua.woochat.client.view.ChatForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ChatFormListener implements ActionListener {
    private ChatForm chatForm;
    private ServerConnection serverConnection;


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

    public void sendMessage(String text) {
        String name = chatForm.getServerConnection().connection.user.getLogin();
        Message message = new Message(2,text);
        message.setLogin(name);
        String str = HandleXml.marshalling1(Message.class, message);
        try {
            chatForm.getServerConnection().sendToServer(str);
        }catch (NullPointerException e){
            System.out.println("Сообщение не отправлено");
        }
    }
}
