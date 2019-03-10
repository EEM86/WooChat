package ua.woochat.client.model;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.ConnectionAgent;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.app.User;
import ua.woochat.client.listeners.ChatFormListener;
import ua.woochat.client.listeners.LoginFormListener;
import ua.woochat.client.view.ChatForm;
import ua.woochat.client.view.MessageView;
import ua.woochat.client.view.WindowImages;
import ua.woochat.client.view.WindowProperties;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerConnection implements ConnectionAgent {

    private Socket socket;
    private Socket socketChatting;
    public Connection connection;
    private LoginFormListener loginFormListener;
    private ChatForm chatForm;
    private Message message;
    private WindowProperties windowProperties;
    private WindowImages windowImages;
    private int tabCount;
    private HashMap<String, ArrayList<String>> onlineState = new HashMap<>();
    private boolean renderComplete;

    final static Logger logger = Logger.getLogger(ServerConnection.class);

    private ArrayList<String> testOnlineList;

    public ServerConnection(LoginFormListener loginFormListener){

        this.loginFormListener = loginFormListener;

        onlineState.put("group000", new ArrayList<>());

        try {
            socket = new Socket(ConfigClient.getServerIP(), ConfigClient.getPortConnection());
            this.connection = new Connection(this, socket);
        } catch (Exception e) {

        }

    }

    public void sendToServer(String text){ //убрать этот метод, вместо него использовать connection.sendToOutStream(text);
        connection.sendToOutStream(text);
    }

    @Override
    public void connectionCreated(Connection data) {

    }

    @Override
    public void connectionDisconnect(Connection data) {

    }

    @Override
    public void receivedMessage(Connection data, String text) {
        connection = data;
        try {
            message = HandleXml.unMarshallingMessage(text);
        } catch (JAXBException e) {
            logger.error("unMarshallingMessage " + e);
        }

        // регистрация либо вход
        if ((message.getType() == 0) || (message.getType() == 1)) {
            if (message.getMessage().startsWith("true")) {
                int chattingPort = Integer.parseInt(message.getMessage().substring(message.getMessage().indexOf('=') + 1));
                moveToChattingSocket(chattingPort);

                connection.user = new User(message.getLogin(), message.getPassword());
                testOnlineList = message.getGroupList();

                loginFormListener.getLoginForm().getLoginWindow().setVisible(false); //закрывается окошко логин формы
                chatWindow(connection.user.getLogin(), this);



                chatForm.addNewTab(tabCount++, "WooChat", "group000", false);

                message.setType(3);
                message.setGroupID("group000");
                message.setGroupTitle("WooChat");

                sendToServer(HandleXml.marshallingWriter(Message.class, message));

            } else {
                if (message.getType() == 0) {
                    loginFormListener.getLoginForm().getLoginWindow().setEnabled(false);
                    new MessageView("Пользователь с таким именем уже существует!",
                            loginFormListener.getLoginForm().getLoginWindow());
                } else {
                    loginFormListener.getLoginForm().getLoginWindow().setEnabled(false);
                    new MessageView("Неверно введен логин или пароль!",
                            loginFormListener.getLoginForm().getLoginWindow());
                }
            }
        }

        else if (message.getType() == 3) { //обновляет список юзеров онлайн
            logger.debug("Пришло название группы " + message.getGroupTitle());
            logger.debug("Список пользователей: " + message.getGroupList().toString());
            logger.debug("Пришел groupID: " + message.getGroupID());

            if (message.getGroupID().equals("group000")){
                onlineState.put("group000",message.getGroupList());
                logger.debug("Записываю в group000: " + message.getGroupID());
                logger.debug("Значение: " + message.getGroupList().toString());
            }else{
                logger.debug("ELSE сработал");
                onlineState.put(message.getGroupID(),message.getGroupList());
            }
            reNewAllTabs();
            renderComplete = true;
        }

        // сообщение
        else if (message.getType() == 2) {

            for (int i = 0; i < tabCount; i++){

                if (chatForm.getConversationPanel().getTitleAt(i).equals(message.getGroupID())) {
                    logger.debug("Нашли ID: " + message.getGroupID());
                    sendToChat(message.getLogin(), message.getMessage(), i);
                }
            }
        }

        else if (message.getType() == 6) {   // работает только для приватного чата, когда пользователей 2!!!
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
            logger.debug("делаю setID для вкладки: " + message.getGroupID());
        }

        else if (message.getType() == 7) {
            logger.debug("Спиcок пользователей: ==7:" + message.getGroupList());
            chatForm.addNewTab(tabCount++,message.getGroupTitle(), message.getGroupID(),true);
            logger.debug("Обновляю с ==7 " + message.getGroupList());
            onlineState.put(message.getGroupID(), message.getGroupList());
            reNewAllTabs();

        }

        else if (message.getType() == 8) {
            ArrayList<String> onlineUsersWithoutPrivateGroups = message.getGroupList();

            for (String entry: onlineUsersWithoutPrivateGroups) {
                logger.debug("Спиcок пользователей: " + entry);
            }
            chatForm.getChatListener().reNewAddList(onlineUsersWithoutPrivateGroups);
        }
        else if (message.getType() == 11) {
            logger.debug("SERVER: user at  ==11"  + message.getLogin());
            removeCurrentUserFromOnline(message.getLogin());
        }
        else if (message.getType() == 12) {
            logger.debug("Спиcок пользователей: ==12:" + message.getGroupList());
            tabRename(message.getGroupTitle(), message.getGroupID());
            logger.debug("Обновляю с ==12 " + message.getGroupList());
            onlineState.put(message.getGroupID(),message.getGroupList());
            reNewAllTabs();
        }
    }

    private void removeCurrentUserFromOnline(String login) {
        ArrayList<String> temp = null;
        for(Map.Entry<String, ArrayList<String>> entry: onlineState.entrySet()) {
            temp = entry.getValue();
            logger.debug("Пробегаю по группе:" + entry.getKey());

            for (String user: temp ){
                System.out.println("Before romoving:" + temp.toString());
                if(user.equals(login)){
                    System.out.println("GROUP:" + entry.getKey() + " USER:" + user);
                    offlineMessage(entry.getKey(), login);
                    logger.debug("Remove login: " + user);
                    temp.remove(login);
                    break;
                }
            }
            System.out.println("After romoving:" + temp.toString());
        }
        reNewAllTabs();
    }

    private void offlineMessage(String key, String login) {
         for (int i = 0; i < tabCount; i++){
             String tabTitle = chatForm.getConversationPanel().getTitleAt(i);
            if (tabTitle.equals(key)){
                sendToChat(login, "вышел из сети",i);
            }
        }
    }

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
     * Метод создает новое окно чата для авторизированного/зарегистрированного пользователя
     * @param user пользователь который успешно авторизирован
     */
    private void chatWindow(String user, ServerConnection serverConnection) {
        windowProperties = loginFormListener.getLoginForm().getProperties();
        windowImages = loginFormListener.getLoginForm().getImages();
        chatForm = new ChatForm(windowProperties, windowImages, user, serverConnection);
    }

    private void moveToChattingSocket(int chattingPort) {
        try {
            socketChatting = new Socket(ConfigClient.getServerIP(), chattingPort);
            connection.setSocket(socketChatting);
            logger.debug("Client has changed connection socket to chatting socket:" + socketChatting.getInetAddress() + ":" + socketChatting.getPort());
        } catch (IOException e) {
            logger.error("Error client socket creation" + e);
        }
    }

    private void sendToChat(String login, String message, int tabNumber){
        JPanel temp;
        JScrollPane sp;
        JTextArea jta;
        JViewport jva;
        logger.debug("Обновляю компонент по индексу: "+ tabNumber);
        temp = (JPanel) chatForm.getConversationPanel().getComponentAt(tabNumber);

        sp = (JScrollPane) temp.getComponent(0);
        jva = (JViewport) sp.getComponent(0);
        jta = (JTextArea)jva.getComponent(0);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:MM:ss");
        Date date = new Date();
        sdf.format(date);

        jta.append("[" + sdf.format(date) + "]" + "<" + login + ">: " + message + "\n");
        chatForm.getMessageField().setText("");
    }

    public void changeTabReNewOnlineList(int index){
        logger.debug("Обновляю список по вкладке с groupID: "  + chatForm.getConversationPanel().getTitleAt(index));
        if (renderComplete) {
            if (onlineState.get(chatForm.getConversationPanel().getTitleAt(index))!=null) {
                reNewOnlineList(onlineState.get(chatForm.getConversationPanel().getTitleAt(index)));
            }
        }
    }

    /**
     * Метод обновляет список онлайн пользователей
     * @param tOl список пользователей
     */
    private void reNewOnlineList(ArrayList<String> tOl) {
        chatForm.getScrollPane().setVisible(false);
        chatForm.getModel().clear();
        int i=0;

        for (String entry: tOl) {
            logger.debug("Inside: " + entry);
            chatForm.getModel().add(i, entry);
            i++;
        }

        chatForm.getScrollPane().setVisible(true);
        chatForm.getUserOnlineLabel().setText("Online users: (" + Integer.toString(tOl.size()) + ")");
    }

    /**
     * При нажатии на кнопку закрытия вкладки, метод отправляет серверу запрос на удаление пользователя с группы
     * @param groupID ID группы которую пользователь хочет покинуть
     */
    public void leaveGroup(String groupID){
        for(Map.Entry<String, ArrayList<String>> entry: onlineState.entrySet()){
            if (entry.getKey().equals(groupID)){
                logger.debug("Удаляю значение по getKey" + groupID);
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

    public boolean isRenderComplete() {
        return renderComplete;
    }

    public HashMap<String, ArrayList<String>> getOnlineState() {
        return onlineState;
    }

    public void disconnectRequest() {
        Message msg = new Message(11, "");
        msg.setLogin(connection.user.getLogin());
        sendToServer(HandleXml.marshallingWriter(Message.class, msg));
    }
    public void tabRename(String newTitle, String groupID){
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
    }

