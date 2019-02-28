package ua.woochat.client.listeners;

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
            sendMessage(chatForm.getMessageField().getText());
        }

        if (e.getActionCommand().equals("enterPressed")) {
            sendMessage(chatForm.getMessageField().getText());
        }
    }

    public void sendToChat(String message){
        JPanel temp;
        JScrollPane sp;
        JTextArea jta;
        JViewport jva;

        temp = (JPanel) chatForm.getConversationPanel().getSelectedComponent();
        sp = (JScrollPane) temp.getComponent(0);
        jva = (JViewport) sp.getComponent(0);
        jta = (JTextArea)jva.getComponent(0);

        jta.append( message + "\n");
        chatForm.getMessageField().setText("");
    }

    public void sendMessage(String message) {
        serverConnection.sendToServer(message);
    }
}
