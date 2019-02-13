package ua.woochat.client.view;

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

    private DefaultListModel<String> model = new DefaultListModel();
    private JScrollPane scrollPane;
    private JList userList;

    private JLabel userOnlineLabel;

    private JTabbedPane conversationPanel;

    private String[] users = {"UserAnatoliy", "Bodik", "Shaurma", "Gnom", "Jon Snow (2)", "MARTIN", "Daywalker", "NEITRINO", "ЛЯПOTA", "-ZAUR", "DeHWeT", "NELLY", "Лacкoвaя_пaнтepa", "-CIQAN", "DeLi", "NELLY_FURTADO", "Лacкoвый_Бaкинeц", "-NeMo", "DeaD_GirL", "NEQATI", "Лacтoчкa", "-UREK", "Deart-Wolf", "NERGIZ_132", "Лaпyля"};

    public ChatForm(WindowProperties properties, WindowImages images){
        this.properties = properties;
        this.images = images;

        createWindow();
    }

    private void createWindow() {

        chatForm = new JFrame("Woo Chat");
        chatForm.getContentPane().setBackground(properties.getBgColor());
        chatForm.setBounds(700, 500, 700, 500);
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
        messageContainer = new JPanel();
        messageContainer.setBackground(properties.getChatBackColor());
        messageContainer.setPreferredSize(new Dimension(687,58));
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

        JPanel testPanel_1 = new JPanel();
        JPanel testPanel_2 = new JPanel();
        JPanel testPanel_3 = new JPanel();

        testPanel_1.setBackground(properties.getChatBackColor());
        testPanel_2.setBackground(properties.getChatBackColor());
        testPanel_3.setBackground(new Color(0, 31, 150));

        conversationPanel.addTab("Jon Snow", testPanel_1);
        conversationPanel.addTab("Shaurma", testPanel_2);
        conversationPanel.addTab("Daywalker, Roy Amber", testPanel_3);
        chatContainer.add(conversationPanel);
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

    private Border border() {
        return BorderFactory.createEmptyBorder(0, 0, 0, 0);
    }
}
