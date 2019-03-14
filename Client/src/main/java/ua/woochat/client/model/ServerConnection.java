package ua.woochat.client.model;

import org.apache.log4j.Logger;
import ua.woochat.app.*;
import ua.woochat.client.listeners.LoginFormListener;
import ua.woochat.client.view.ChatForm;
import ua.woochat.client.view.MessageView;
import ua.woochat.client.view.WindowImages;
import ua.woochat.client.view.WindowProperties;
import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * class describes methods for processing transfers and receiving requests to the server
 */
public class ServerConnection implements ConnectionAgent {

    public Connection connection;
    private LoginFormListener loginFormListener;
    private ChatForm chatForm;
    private Message message;
    private int tabCount;
    private HashMap<String, ArrayList<String>> onlineState = new HashMap<>();
    private boolean renderComplete;

    private final static Logger logger = Logger.getLogger(ServerConnection.class);

    public ServerConnection(LoginFormListener loginFormListener){

        this.loginFormListener = loginFormListener;
        onlineState.put("group000", new ArrayList<>());

        try {
            Socket socket = new Socket(ConfigClient.getServerIP(), ConfigClient.getPortConnection());
            this.connection = new Connection(this, socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method send message to server
     * @param text text message
     */
    public void sendToServer(String text){
        connection.sendToOutStream(text);
    }

    @Override
    public void connectionCreated(Connection data) {

    }

    /**
     * Method performs disconnect current connection
     * @param data current connection
     */
    @Override
    public void connectionDisconnect(Connection data) {
        data.disconnect();
        System.exit(0);
    }

    /**
     * Method receive a response from the server and process it.
     * @param data connection with server
     * @param text message response
     */
    @Override
    public void receivedMessage(Connection data, String text) {
        connection = data;
        try {
            message = HandleXml.unMarshallingMessage(text);
        } catch (JAXBException e) {
            logger.error("unMarshallingMessage " + e);
        }

        // Authorisation or create an account
        if ((message.getType() == 0) || (message.getType() == 1)) {
            if (message.getMessage().startsWith("true")) {
                int chattingPort = Integer.parseInt(message.getMessage().substring(message.getMessage().indexOf('=') + 1));
                moveToChattingSocket(chattingPort);

                connection.user = new User(message.getLogin(), message.getPassword());
                loginFormListener.getLoginForm().getLoginWindow().setVisible(false);
                chatWindow(connection.user.getLogin(), this);
                chatForm.addNewTab(tabCount++, "WooChat", "group000", false);

                message.setType(3);
                message.setGroupID("group000");
                message.setGroupTitle("WooChat");

                sendToServer(HandleXml.marshallingWriter(Message.class, message));

            } else if (message.getMessage().startsWith("update")) {
                logger.info("update");
                Set<Group> groupSet = message.getGroupListUser();

                Queue<HistoryMessage> historyMessages;
                int i;
                for (Group entry: groupSet) {
                    if (!entry.getGroupID().equals("group000")) {
                        chatForm.addNewTab(tabCount++, entry.getGroupName(), entry.getGroupID(), true);
                        i = chatForm.getConversationPanel().getSelectedIndex();
                        historyMessages =  entry.getQueue();
                        if (historyMessages != null) {
                            for (HistoryMessage entry1 : historyMessages) {
                                sendToChat(entry1.getLogin(), entry1.getMessage(), i, entry1.getTime());
                            }
                        }
                    }
                }

            } else {
                if (message.getType() == 0) {
                    loginFormListener.getLoginForm().getLoginWindow().setEnabled(false);
                    new MessageView("User with the same name already exists!",
                            loginFormListener.getLoginForm().getLoginWindow());
                } else {
                    loginFormListener.getLoginForm().getLoginWindow().setEnabled(false);
                    new MessageView("Invalid username or password!",
                            loginFormListener.getLoginForm().getLoginWindow());
                }
            }
        }

        //User list update
        else if (message.getType() == 3) {
            if (message.getGroupID().equals("group000")){
                onlineState.put("group000",message.getGroupList());
            }else{
                onlineState.put(message.getGroupID(),message.getGroupList());
            }
            reNewAllTabs();
            renderComplete = true; //custom form is completely drawn
        }

        //Text message received
        else if (message.getType() == 2) {
            for (int i = 0; i < tabCount; i++){
                if (chatForm.getConversationPanel().getTitleAt(i).equals(message.getGroupID())) {
                    logger.debug("Нашли ID: " + message.getGroupID());
                    sendToChat(message.getLogin(), message.getMessage(), i, null);
                }
            }
        }

        //Server response to creating a private chat
        else if (message.getType() == 6) {
            ArrayList<String> currentGroupList = message.getGroupList();
            String result = currentGroupList.get(currentGroupList.size() - 1);
            if (result.equals(connection.user.getLogin())) {
                chatForm.addNewTab(tabCount++, currentGroupList.get(0), message.getGroupID(),true);
                onlineState.put(message.getGroupID(), message.getGroupList());
                reNewAllTabs();
            } else {
                chatForm.addNewTab(tabCount++, currentGroupList.get(1), message.getGroupID(),true);
                onlineState.put(message.getGroupID(), message.getGroupList());
                reNewAllTabs();
            }
        }

        //Add user to private chat
        else if (message.getType() == 7) {
            logger.debug("List of users to ==7:" + message.getGroupList());
            chatForm.addNewTab(tabCount++,message.getGroupTitle(), message.getGroupID(),true);
            logger.debug("Update onlineState " + message.getGroupList());
            onlineState.put(message.getGroupID(), message.getGroupList());
            reNewAllTabs();
        }

        //Getting a list of users who are not in a current group
        else if (message.getType() == 8) {
            ArrayList<String> onlineUsersWithoutPrivateGroups = message.getGroupList();

            for (String entry: onlineUsersWithoutPrivateGroups) {
                logger.debug("list of users who are not in a current group: " + entry);
            }
            chatForm.getChatListener().reNewAddList(onlineUsersWithoutPrivateGroups);
        }

        //Chat disconnecting response
        else if (message.getType() == 11) {
            logger.debug("Login of user who disconnect: "  + message.getLogin());
            removeCurrentUserFromOnline(message.getLogin());
        }

        //Response of tab rename
        else if (message.getType() == 12) {
            tabRename(message.getGroupTitle(), message.getGroupID());
            onlineState.put(message.getGroupID(),message.getGroupList());
            reNewAllTabs();
        }

        //User kick response
        else if (message.getType() == 13) {
            for (int i = 0; i < tabCount; i++){
                String tabTitle = chatForm.getConversationPanel().getTitleAt(i);
                if (tabTitle.equals(message.getGroupID())){
                    chatForm.getConversationPanel().removeTabAt(i);
                    break;
                }
            }
            leaveGroup(message.getGroupID());
        }

        //User ban response
        else if (message.getType() == 99) {
            if (message.isBanned()) {
                new MessageView(message.getMessage(), chatForm.getChatForm());
                setButtonsActive(false);
            } else {
                setButtonsActive(true);
            }
        }

        //User disconnect response
        else if (message.getType() == 23) {           // В данном методе происходит дисконнект юзера
            disconnectRequest();
            }
        }

    /**
     * Method sets the buttons enable
      * @param value boolean value
     */
    private void setButtonsActive(boolean value) {
            chatForm.getSendButton().setEnabled(value);
            chatForm.getMessageField().setEnabled(value);
            chatForm.getAddUserBtn().setEnabled(value);
    }

    /**
     * Methods remove current user from online list
     * @param login login of removed user
     */
    private void removeCurrentUserFromOnline(String login) {
        ArrayList<String> temp;
        for(Map.Entry<String, ArrayList<String>> entry: onlineState.entrySet()) {
            temp = entry.getValue();
            logger.debug("Find user in group:" + entry.getKey());

            for (String user: temp ){
                System.out.println("Before remove:" + temp.toString());
                if(user.equals(login)){
                    offlineMessage(entry.getKey(), login);
                    logger.debug("Remove login: " + user);
                    temp.remove(login);
                    break;
                }
            }
            System.out.println("After remove:" + temp.toString());
        }
        reNewAllTabs();
    }

    /**
     * Method send a message to chat if user is offline
     * @param key value of tab title
     * @param login user login
     */
    private void offlineMessage(String key, String login) {
         for (int i = 0; i < tabCount; i++){
             String tabTitle = chatForm.getConversationPanel().getTitleAt(i);
            if (tabTitle.equals(key)){
                sendToChat(login, "has left WooChat",i, null);
            }
        }
    }

    /**
     * Method renew online list of all tabs
     */
    private void reNewAllTabs(){
        for (int i = 0; i < tabCount; i++) {
            String tabTitle = chatForm.getConversationPanel().getTitleAt(i);
            for(Map.Entry<String, ArrayList<String>> entry: onlineState.entrySet()){
                if(tabTitle.equals(entry.getKey())){
                    reNewOnlineList(entry.getValue());
                }
            }
        }
    }

    /**
     * The method creates a new chat window for an authorized / registered user.
     * @param user user who is logged/registered in successfully
     */
    private void chatWindow(String user, ServerConnection serverConnection) {
        WindowProperties windowProperties = loginFormListener.getLoginForm().getProperties();
        WindowImages windowImages = loginFormListener.getLoginForm().getImages();
        chatForm = new ChatForm(windowProperties, windowImages, user, serverConnection);
    }

    /**
     * Method move the client to new port
     * @param chattingPort new server port
     */
    private void moveToChattingSocket(int chattingPort) {
        try {
            Socket socketChatting = new Socket(ConfigClient.getServerIP(), chattingPort);
            connection.setSocket(socketChatting);
            logger.debug("Client has changed connection socket to chatting socket:" + socketChatting.getInetAddress() + ":" + socketChatting.getPort());
        } catch (IOException e) {
            logger.error("Error client socket creation" + e);
        }
    }

    /**
     * Method send text message to the current chat
     * @param login login of user who send a text message
     * @param message text message
     * @param tabNumber tab number
     * @param timeMessage time of message
     */
    private void sendToChat(String login, String message, int tabNumber, Date timeMessage){
        JPanel temp;
        JScrollPane sp;
        JTextArea jta;
        JViewport jva;
        temp = (JPanel) chatForm.getConversationPanel().getComponentAt(tabNumber);

        sp = (JScrollPane) temp.getComponent(0);
        jva = (JViewport) sp.getComponent(0);
        jta = (JTextArea)jva.getComponent(0);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date;
        if (timeMessage == null) {
            date = new Date();
        } else {
            date = timeMessage;
        }

        jta.append("[" + sdf.format(date) + "]" + "<" + login + ">: " + message + "\n");
        chatForm.getMessageField().setText("");

        if (chatForm.getConversationPanel().getSelectedIndex() != tabNumber){
            ChatForm.TabTitle ob =  (ChatForm.TabTitle)chatForm.getConversationPanel().getTabComponentAt(tabNumber);
            JLabel jLabel = ob.getEnvelope();
            jLabel.setVisible(true);
        }
    }

    /**
     * Method update online list for current tab
     * @param index tab index
     */
    public void changeTabReNewOnlineList(int index){
        logger.debug("Обновляю список по вкладке с groupID: "  + chatForm.getConversationPanel().getTitleAt(index));
        if (renderComplete) {
            if (onlineState.get(chatForm.getConversationPanel().getTitleAt(index))!=null) {
                reNewOnlineList(onlineState.get(chatForm.getConversationPanel().getTitleAt(index)));
            }
        }
    }

    /**
     * Method update online list
     * @param tOl new list of users
     */
    private void reNewOnlineList(ArrayList<String> tOl) {
        chatForm.getScrollPane().setVisible(false);
        chatForm.getModel().clear();
        int i=0;

        for (String entry: tOl) {
            chatForm.getModel().add(i, entry);
            i++;
        }
        chatForm.getScrollPane().setVisible(true);
        chatForm.getUserOnlineLabel().setText("Online users: (" + Integer.toString(tOl.size()) + ")");
    }

    /**
     * When clicking on the close button of the tab, the method sends the server a request to delete the user
     * from the group
     * @param groupID id of the group
     */
    public void leaveGroup(String groupID){
        for(Map.Entry<String, ArrayList<String>> entry: onlineState.entrySet()){
            if (entry.getKey().equals(groupID)){
                logger.debug("remove value of getKey" + groupID);
                onlineState.remove(entry.getKey());
                break;
            }
        }

        Message msg = new Message(9, "");
        msg.setGroupID(groupID);
        msg.setLogin(connection.user.getLogin());
        tabCount--;
        sendToServer(HandleXml.marshallingWriter(Message.class, msg));
    }

    /**
     * Method returns true if all of customer form is drawing
     * @return true or false value
     */
    public boolean isRenderComplete() {
        return renderComplete;
    }

    public HashMap<String, ArrayList<String>> getOnlineState() {
        return onlineState;
    }

    /**
     * Method send a disconnect request to server
     */
    public void disconnectRequest() {
        Message msg = new Message(11, "");
        msg.setLogin(connection.user.getLogin());
        sendToServer(HandleXml.marshallingWriter(Message.class, msg));
        connectionDisconnect(connection);
    }

    /**
     * Method rename tab of current group id
     * @param newTitle new tab name
     * @param groupID current tab id
     */
    private void tabRename(String newTitle, String groupID){
        for (int i = 0; i < tabCount; i++) {
            String tabTitle = chatForm.getConversationPanel().getTitleAt(i);
                if (tabTitle.equals(groupID)){
                    ChatForm.TabTitle ob =  (ChatForm.TabTitle)chatForm.getConversationPanel().getTabComponentAt(i);
                    JLabel jLabel = ob.getLbl();
                    jLabel.setText(newTitle);
                    break;
                }
            }
        }

    /**
     * Find and select tab if we try to create new private chat
      * @param user private chatting user name
     * @return true if tab is found and false if not
     */
    public boolean isChatFounded(String user) {
        for (int i = 0; i < tabCount; i++){
            ChatForm.TabTitle ob =  (ChatForm.TabTitle)chatForm.getConversationPanel().getTabComponentAt(i);
            JLabel jLabel = ob.getLbl();

            if (jLabel.getText().equals(user)){
                chatForm.getConversationPanel().setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }
}

