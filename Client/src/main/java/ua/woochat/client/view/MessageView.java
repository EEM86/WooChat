package ua.woochat.client.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MessageView {

    private JFrame messageWindow;
    private WindowProperties properties;
    private JLabel messageText;
    private JPanel panel;

    public MessageView(String message, JFrame source) {

        properties = new WindowProperties();
        messageWindow = new JFrame("System message");
        messageWindow.getContentPane().setBackground(properties.getBgColor());
        messageWindow.setBounds(500, 200, 300, 60);
        messageWindow.setLocationRelativeTo(null);
        messageWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        messageWindow.setResizable(false);
        panel = new JPanel();
        messageText = new JLabel(message);
        messageText.setForeground(properties.getTextColor());
        panel.add(messageText);
        panel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        panel.setBackground(properties.getBgColor());
        messageWindow.getContentPane().add(panel);

        source.setEnabled(false);

        messageWindow.setVisible(true);


        messageWindow.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent e )
            {
                source.setEnabled(true);
                messageWindow.setVisible(false);
            }
        } );

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                source.setEnabled(true);
                messageWindow.setVisible(false);
            }
        };

        messageWindow.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                escapeKeyStroke, "ESCAPE");
        messageWindow.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    }








