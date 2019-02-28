package ua.woochat.client.listeners;

import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.client.model.ServerConnection;
import ua.woochat.client.view.ChatForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ChatFormListener implements ActionListener {
    private HandleXml handleXml = new HandleXml();
    private ChatForm chatForm;
    private ServerConnection serverConnection;


    public ChatFormListener(ChatForm chatForm) {
        this.chatForm = chatForm;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("sendButton")) {
            sendMessage(chatForm.getMessageField().getText());
        }

        if (e.getActionCommand().equals("enterPressed")) {
            sendMessage(chatForm.getMessageField().getText());
        }
    }



    public void sendMessage(String text) {
        Message message = new Message(2,text);
        String str = handleXml.marshalling1(Message.class, message);
        try {
            chatForm.getServerConnection().sendToServer(str);
        }catch (NullPointerException e){
            System.out.println("Сообщение не отправлено");
        }
        //chatForm.getServerConnection().sendToServer(message);
    }
}
