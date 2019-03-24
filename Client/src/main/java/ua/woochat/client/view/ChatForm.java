package ua.woochat.client.view;

import org.apache.log4j.Logger;
import ua.woochat.client.listeners.ChatFormListener;
import ua.woochat.client.model.ServerConnection;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

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
    private JFrame addUserListForm;
    private JPanel chatContainer;
    private JPanel listContainer;
    private JPanel messageContainer;
    private DefaultListModel<String> model = new DefaultListModel();
    private DefaultListModel<String> addUserModel = new DefaultListModel();
    private JScrollPane scrollPane;
    private JScrollPane addUserScrollPane;
    private JList addUserList;
    private JLabel userOnlineLabel;
    private JLabel adminName;
    private JButton sendButton;
    private JButton addUserBtn;
    private JTabbedPane conversationPanel;
    private JTextField messageField;
    private JTextField groupTextField;
    private ServerConnection serverConnection;
    private String user;
    private ChatFormListener chatListener;

    private final static Logger logger = Logger.getLogger(ServerConnection.class);

    public ChatForm(WindowProperties properties, WindowImages images, String user, ServerConnection serverConnection){

        this.serverConnection = serverConnection;
        this.properties = properties;
        this.images = images;
        this.user = user;
        createWindow();
    }

    /**
     * Method create main window form
     */
    private void createWindow() {

        chatForm = new JFrame("Woo Chat | " + user);
        chatForm.getContentPane().setBackground(properties.getBgColor());
        chatForm.setBounds(700, 500, 700, 482);
        chatForm.setLocationRelativeTo(null);
        chatForm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        chatForm.setIconImage(images.getLogo().getImage());
        chatForm.setResizable(false);
        chatForm.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent e )
            {
                serverConnection.disconnectRequest();
            }
        } );

        JPanel container = new JPanel();
        container.setBackground(properties.getBgColor());

        createChatContainer();
        createListContainer();
        createMessageContainer();
        createAddUserListForm();

        container.add(chatContainer, BorderLayout.LINE_START);
        container.add(listContainer, BorderLayout.LINE_END);
        container.add(messageContainer, BorderLayout.PAGE_END);
        chatForm.add(container);
        chatForm.setVisible(true);
    }

    /**
     * Method create a form for user list
     */
    private void createAddUserListForm() {

        addUserListForm = new JFrame("Add user");
        addUserListForm.getContentPane().setBackground(properties.getBgColor());
        addUserListForm.setIconImage(images.getLogo().getImage());
        addUserListForm.setBounds(700, 500, 170, 370);
        addUserListForm.setLocationRelativeTo(null);
        addUserListForm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addUserListForm.setResizable(false);

        JLabel groupTextLabel = new JLabel("GROUP NAME:");
        groupTextLabel.setForeground(properties.getLabelTextColor());

        groupTextField = new JTextField();
        groupTextField.setPreferredSize(new Dimension(150,20));

        JPanel addUserListPanel = new JPanel();
        addUserListPanel.setLayout(new FlowLayout());
        addUserListPanel.setBackground(properties.getChatBackColor());
        addUserListPanel.setPreferredSize(new Dimension(182,400));

        JLabel addUserOnlineLabel = new JLabel("USERS:");
        addUserOnlineLabel.setForeground(properties.getLabelTextColor());

        addUserModel = new DefaultListModel<>();

        addUserList = new JList(addUserModel);
        addUserList.setForeground(properties.getUserListColor());
        addUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addUserList.setBackground(properties.getChatBackColor());

        addUserScrollPane = new JScrollPane(addUserList);

        addUserScrollPane.setPreferredSize(new Dimension(150, 225));
        addUserScrollPane.setBorder(border());

        JButton addUser = new JButton("Add");
        addUser.setActionCommand("addUser");

        btnConfig(addUser);

        addUser.setPreferredSize(new Dimension(150,30));

        addUserListPanel.add(groupTextLabel);
        addUserListPanel.add(groupTextField);
        addUserListPanel.add(addUserOnlineLabel);
        addUserListPanel.add(addUserScrollPane);
        addUserListPanel.add(addUser);

        addUserListForm.getContentPane().add(addUserListPanel);
        addUserListForm.setVisible(false);

        /**
         * Method adds a listener by clicking the close button in the user selection window.
         */
        addUserListForm.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent e )
            {
                chatForm.setEnabled(true);
                addUserListForm.setVisible(false);
                chatForm.toFront();
            }
        } );

        /**
         * Method adds a listener by pressing the ESC button in the user selection window
         */
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                chatForm.setEnabled(true);
                addUserListForm.setVisible(false);
                chatForm.toFront();
            }
        };
        addUserListForm.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                escapeKeyStroke, "ESCAPE");
        addUserListForm.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    /**
     * method create a message container
     */
    private void createMessageContainer() {
        chatListener = new ChatFormListener(this);
        messageContainer = new JPanel();
        messageContainer.setBackground(properties.getChatBackColor());
        messageContainer.setPreferredSize(new Dimension(687,40));
        messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(500,30));
        messageField.setActionCommand("enterPressed");
        messageField.addActionListener(chatListener);
        sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(80,30));
        sendButton.setActionCommand("sendButton");
        sendButton.addActionListener(chatListener);
        addUserBtn = new JButton("Add");
        addUserBtn.setEnabled(false);
        addUserBtn.setActionCommand("addUserBtn");
        btnConfig(addUserBtn);
        messageContainer.add(messageField);
        messageContainer.add(sendButton);
        messageContainer.add(addUserBtn);
    }

    /**
     * Method configure the appearance of buttons
     * @param btn JButton component
     */
    private void btnConfig(JButton btn) {
        btn.setForeground(properties.getLabelTextColor());
        btn.setPreferredSize(new Dimension(88,30));
        btn.setBackground(properties.getBgColor());
        btn.addActionListener(chatListener);
        btn.setBorderPainted(false);
    }

    /**
     * method create a chat container
     */
    private void createChatContainer() {
        chatContainer = new JPanel();
        chatContainer.setBackground(properties.getChatBackColor());
        chatContainer.setPreferredSize(new Dimension(500,400));
        conversationPanel = new JTabbedPane();
        conversationPanel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {

                if (serverConnection.isRenderComplete()){
                    ChatForm.TabTitle ob =  (ChatForm.TabTitle)conversationPanel.getTabComponentAt(conversationPanel.getSelectedIndex());
                    JLabel jLabel = ob.getEnvelope();
                    jLabel.setVisible(false);
                }

                serverConnection.changeTabReNewOnlineList(conversationPanel.getSelectedIndex());
                if (serverConnection.isRenderComplete()){
                if (conversationPanel.getTitleAt(conversationPanel.getSelectedIndex()).equals("group000")){
                    logger.debug("addUserBtn.setEnabled(false);");
                    addUserBtn.setEnabled(false);
                }else {
                    logger.debug("addUserBtn.setEnabled(true);");
                    addUserBtn.setEnabled(true);
                    }
                }
            }
        });

        conversationPanel.setUI(new MyTabbedPaneUI(properties));
        conversationPanel.setPreferredSize(new Dimension(500,390));
        chatContainer.add(conversationPanel);
    }

    /**
     * The method adds a new tab to the chat window. All tabs are indexed as in arrays.
     * @param index index of new tab
     * @param tabTitle chat title
     * @param tabID new tab id
     */
    public void addNewTab(int index, String tabTitle, String tabID, boolean closeable) {
        conversationPanel.addTab(null, createNewTab());
        conversationPanel.setTabComponentAt(index,new TabTitle(tabTitle,index, closeable));
        conversationPanel.setTitleAt(index,tabID);
        conversationPanel.setSelectedIndex(index);
    }

    /**
     * Class describe a new Tab object
     */
    public class TabTitle extends JPanel{
        private JLabel lbl;
        private JLabel envelope;

        private TabTitle(final String title, final int index, boolean closeable){

            setOpaque(false);
            lbl = new JLabel(title);
            envelope = new JLabel();
            envelope.setPreferredSize(new Dimension(14,9));
            envelope.setIcon(images.getEnvelope());
            envelope.setVisible(false);
            lbl.setForeground(properties.getTextColor());
            lbl.setPreferredSize(new Dimension(55,13));
            JButton button = new JButton();
            button.setBackground(properties.getBgColor());
            button.setBorderPainted(false);
            button.setPreferredSize(new Dimension(16, 16));
            button.setIcon(images.getCloseTabIcon());

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    chatListener.pressedCloseGroup(conversationPanel.getTitleAt(index));
                    conversationPanel.removeTabAt(index);
                }
            });

            if (closeable){
                add(envelope,BorderLayout.WEST);
                add(lbl, BorderLayout.CENTER);
                add(button, BorderLayout.EAST);
            }else {
                add(envelope,BorderLayout.WEST);
                add(lbl, BorderLayout.CENTER);
            }
        }
        public JLabel getLbl() {
            return lbl;
        }
        public JLabel getEnvelope() {
            return envelope;
        }
    }

    /**
     * Method returns a new tab of type JPanel
     * @return new JPanel object
     */
    private JPanel createNewTab() {

        JPanel newTab = new JPanel();
        newTab.setBorder(BorderFactory.createLineBorder(properties.getUserListColor()));
        newTab.setBackground(properties.getChatBackColor());
        JTextArea chatArea = new JTextArea();
        chatArea.setFont(new Font(null, Font.BOLD, 12));
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
     * Method create a user list container
     */

    private void createListContainer() {

        listContainer = new JPanel();
        listContainer.setBackground(properties.getChatBackColor());
        listContainer.setPreferredSize(new Dimension(182,400));
        userOnlineLabel = new JLabel();
        userOnlineLabel.setForeground(properties.getLabelTextColor());
        adminName = new JLabel("Admin: offline");
        adminName.setForeground(properties.getLabelTextColor());
        JLabel line = new JLabel();
        line.setPreferredSize(new Dimension(140,10));
        line.setIcon(images.getLine());
        JList userList = new JList(model);
        userList.setForeground(properties.getUserListColor());
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setBackground(properties.getChatBackColor());
        scrollPane = new JScrollPane(userList);
        scrollPane.setPreferredSize(new Dimension(170, 370));
        scrollPane.setBorder(border());
        listContainer.add(userOnlineLabel);
        listContainer.add(line);
        listContainer.add(adminName);
        listContainer.add(scrollPane);

        /**
         * Adding a listener for double clicking on the list of users
         */
        userList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    String user1 = serverConnection.connection.getUser().getLogin();
                    String user2 = model.get(index);

                    if (!serverConnection.isChatFounded(user2)) {
                        if (user1.equals(user2)) {
                            new MessageView("You can not create a dialogue with yourself", chatForm, false);
                        } else {
                            chatListener.privateGroupCreate(user1, user2);
                        }
                    }
                }
            }
        });
    }

    public ChatFormListener getChatListener() {
        return chatListener;
    }

    public JList getAddUserList() {
        return addUserList;
    }

    public DefaultListModel<String> getAddUserModel() {
        return addUserModel;
    }

    public JScrollPane getAddUserScrollPane() {
        return addUserScrollPane;
    }

    public JFrame getAddUserListForm() {
        return addUserListForm;
    }

    public JFrame getChatForm() {
        return chatForm;
    }

    public DefaultListModel<String> getModel() {
        return model;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    public JTabbedPane getConversationPanel() {
        return conversationPanel;
    }

    public JTextField getMessageField() {
        return messageField;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JLabel getUserOnlineLabel() {
        return userOnlineLabel;
    }

    public JTextField getGroupTextField() {
        return groupTextField;
    }

    public JButton getAddUserBtn() {
        return addUserBtn;
    }

    private Border border() {
        return BorderFactory.createEmptyBorder(0, 0, 0, 0);
    }

    public JLabel getAdminName() {
        return adminName;
    }
}

    /**
     * Redefining drawing methods for BasicTabbedPaneUI
     */
    class MyTabbedPaneUI extends javax.swing.plaf.basic.BasicTabbedPaneUI {

    private WindowProperties properties;
    MyTabbedPaneUI(WindowProperties properties){
        this.properties = properties;
    }

    @Override
    protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects,
                            int tabIndex, Rectangle iconRect, Rectangle textRect) {
        Color savedColor = g.getColor();

        g.setColor(properties.getBgColor());

        g.fillRect(rects[tabIndex].x, rects[tabIndex].y,
                rects[tabIndex].width, rects[tabIndex].height);

        g.setColor(properties.getUserListColor());
        g.drawRect(rects[tabIndex].x, rects[tabIndex].y,
                rects[tabIndex].width, rects[tabIndex].height);
        g.setColor(savedColor);
    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex){
        g.fillRect(0,0,0 ,0);
    }
}
