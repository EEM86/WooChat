package ua.woochat.client.view;

import ua.woochat.client.listeners.ChatFormListener;
import ua.woochat.client.model.ServerConnection;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Class describes the main window of WooChat application
 * @autor Yevhen Yermolenko
 * @autor Maryia Romanovych
 * @autor Sergey Skidan
 */

public class ChatForm {

    private WindowProperties properties;
    private WindowImages images;
    private JFrame chatForm;

    private JPanel container;
    private JPanel chatContainer;
    private JPanel listContainer;
    private JPanel messageContainer;
    private JPanel functionalPanel;

    private DefaultListModel<String> model = new DefaultListModel();
    private JScrollPane scrollPane;
    private JList userList;

    private JLabel userOnlineLabel;
    private JButton sendButton;

    private JTabbedPane conversationPanel;

    private JTextField messageField;

    private ServerConnection serverConnection;

    private String[] users = {"UserAnatoliy", "Bodik", "Shaurma", "Gnom", "Jon Snow (2)", "MARTIN", "Daywalker", "NEITRINO", "ЛЯПOTA", "-ZAUR", "DeHWeT", "NELLY", "Лacкoвaя_пaнтepa", "-CIQAN", "DeLi", "NELLY_FURTADO", "Лacкoвый_Бaкинeц", "-NeMo", "DeaD_GirL", "NEQATI", "Лacтoчкa", "-UREK", "Deart-Wolf", "NERGIZ_132", "Лaпyля"};

    public ChatForm(WindowProperties properties, WindowImages images){
        this.serverConnection = serverConnection;
        this.properties = properties;
        this.images = images;

        createWindow();
    }

    private void createWindow() {

        chatForm = new JFrame("Woo Chat");
        chatForm.getContentPane().setBackground(properties.getBgColor());
        chatForm.setBounds(700, 500, 700, 482);
        chatForm.setLocationRelativeTo(null);
        chatForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatForm.setResizable(false);

        container = new JPanel();
        container.setBackground(properties.getBgColor());

        createChatContainer();
        createListContainer();
        createMessageContainer();

        container.add(chatContainer, BorderLayout.LINE_START);
        container.add(listContainer, BorderLayout.LINE_END);
        container.add(messageContainer, BorderLayout.PAGE_END);

        chatForm.add(container);

        chatForm.setVisible(true);
    }

    /**
     * method create a message container
     */
    private void createMessageContainer() {

        ChatFormListener chatListener = new ChatFormListener(this);

        messageContainer = new JPanel();
        messageContainer.setBackground(properties.getChatBackColor());
        messageContainer.setPreferredSize(new Dimension(687,40));

        messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(405,30));
        messageField.setActionCommand("enterPressed");
        messageField.addActionListener(chatListener);

        sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(80,30));
        sendButton.setActionCommand("sendButton");
        sendButton.addActionListener(chatListener);

        functionalPanel = new JPanel();
        functionalPanel.setLayout(new FlowLayout());

        functionalPanel.setBackground(new Color(97, 73, 150));
        functionalPanel.setPreferredSize(new Dimension(182,30));

        messageContainer.add(messageField);
        messageContainer.add(sendButton);
        messageContainer.add(functionalPanel);

    }

    /**
     * method create a chat container
     */
    private void createChatContainer() {

        chatContainer = new JPanel();

        chatContainer.setBackground(properties.getChatBackColor());
        chatContainer.setPreferredSize(new Dimension(500,400));

        conversationPanel = new JTabbedPane();
        conversationPanel.setPreferredSize(new Dimension(500,390));
        conversationPanel.setBackground(properties.getChatBackColor());
        conversationPanel.setForeground(properties.getTextColor());
        conversationPanel.setBorder(new EmptyBorder(3, 3, 3, 3));


        conversationPanel.addTab("Generated", createNewTab());
        conversationPanel.addTab("Jon Snow", createNewTab());
        conversationPanel.addTab("Shaurma", createNewTab());
        conversationPanel.addTab("Daywalker, Roy Amber", createNewTab());

        chatContainer.add(conversationPanel);
    }

    private JPanel createNewTab() {

        JPanel newTab = new JPanel();
        newTab.setBackground(properties.getChatBackColor());

        JTextArea chatArea = new JTextArea();
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
        chatArea.setBackground(properties.getChatBackColor());
        chatArea.setForeground(properties.getTextColor());
        chatArea.setTabSize(10);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);

        JScrollPane jsp = new JScrollPane(chatArea);
        jsp.setPreferredSize(new Dimension(475,342));
        jsp.setBorder(border());

        newTab.add(jsp);

        return newTab;
    }

    /**
     * method create a user list container
     */

    private void createListContainer() {

        listContainer = new JPanel();
        listContainer.setBackground(properties.getChatBackColor());
        listContainer.setPreferredSize(new Dimension(182,400));

        userOnlineLabel = new JLabel("Users online(25)");
        userOnlineLabel.setForeground(properties.getLabelTextColor());

        for (int i = 0; i < users.length; i++){
            model.add(i,users[i]);
        }

        userList = new JList(model);
        userList.setForeground(properties.getUserListColor());

        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setBackground(properties.getChatBackColor());

        scrollPane = new JScrollPane(userList);
        scrollPane.setPreferredSize(new Dimension(170, 370));
        scrollPane.setBorder(border());

        listContainer.add(userOnlineLabel);
        listContainer.add(scrollPane);
    }

    public JTabbedPane getConversationPanel() {
        return conversationPanel;
    }

    public JTextField getMessageField() {
        return messageField;
    }

    private Border border() {
        return BorderFactory.createEmptyBorder(0, 0, 0, 0);
    }
}
