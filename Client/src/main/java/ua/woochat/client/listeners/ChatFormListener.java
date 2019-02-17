package ua.woochat.client.listeners;

import ua.woochat.client.view.ChatForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatFormListener implements ActionListener {

    private ChatForm chatForm;

    public ChatFormListener(ChatForm chatForm){
        this.chatForm = chatForm;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("sendButton")) {
            sendMessage();
        }

        if (e.getActionCommand().equals("enterPressed")) {
            sendMessage();
        }
    }

    private void sendMessage() {
        JPanel temp;
        JScrollPane sp;
        JTextArea jta;
        JViewport jva;

        temp = (JPanel) chatForm.getConversationPanel().getSelectedComponent();
        sp = (JScrollPane) temp.getComponent(0);
        jva = (JViewport) sp.getComponent(0);
        jta = (JTextArea)jva.getComponent(0);

        jta.append("Jon: " + chatForm.getMessageField().getText() + "\n");
        chatForm.getMessageField().setText("");
        chatForm.getMessageField().setFocusable(true);
    }


}
