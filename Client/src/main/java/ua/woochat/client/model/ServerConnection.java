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

    final static Logger logger = Logger.getLogger(ServerConnection.class);

    private ArrayList<String> testOnlineList;

    public ServerConnection(LoginFormListener loginFormListener){

        this.loginFormListener = loginFormListener;

        try {
            socket = new Socket(ConfigClient.getServerIP(), ConfigClient.getPortConnection());
            this.connection = new Connection(this, socket);
        } catch (Exception e) {

        }
    }

    public void sendToServer(String text){
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

                chatForm.addNewTab(tabCount++, "WooChat", "group000");

                message.setType(3);
                sendToServer(HandleXml.marshalling1(Message.class, message));
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
            //testOnlineList = message.getGroupList();
            testOnlineList = message.getGroupList();
            reNewOnlineList(testOnlineList);
            //sendToChat("WooChat", message.getLogin() + " has joined to chat.", groupID);
        }

        // сообщение
        else if (message.getType() == 2) {
            logger.debug("Получили сообщение от сервера == 2 " + "from");
            logger.debug("tabCount " + tabCount);
            for (int i = 0; i < tabCount; i++){
                logger.debug("От кого пришла информация: " + message.getLogin());
                logger.debug("I= " + i);
                logger.debug("То что пришло: " + message.getGroupID());
                logger.debug("ID со вкладки: " + chatForm.getConversationPanel().getTitleAt(i));

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
                chatForm.addNewTab(tabCount++, currentGroupList.get(0), message.getGroupID());
            } else {
                chatForm.addNewTab(tabCount++, currentGroupList.get(1), message.getGroupID());
            }
            logger.debug("делаю setID для вкладки: " + message.getGroupID());
        }

        else if (message.getType() == 7) {
            logger.debug("делаю setID для вкладки: " + message.getGroupID());
            chatForm.addNewTab(tabCount++, message.getGroupID(), message.getGroupID());
        }

        else if (message.getType() == 8) {
            logger.debug("");
            ArrayList<String> onlineUsersWithoutPrivateGroups = message.getGroupList();

            for (String entry: onlineUsersWithoutPrivateGroups) {
                logger.debug("Спиcок пользователей: " + entry);
            }
            chatForm.getChatListener().reNewAddList(onlineUsersWithoutPrivateGroups);
        }

//        else if (message.getType() == 9) { //закрываем одну из вкладок, пользователь покидает группу
//            message.setType(3);
//            message.setMessage(message.getLogin() + " has left the group.");
//            sendToServer(HandleXml.marshalling1(Message.class, message));
//        }
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

    public void sendToChat(String login, String message, int tabNumber){
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

    public void leaveGroup(String groupID){
        Message msg = new Message(9, "");
        msg.setGroupID(groupID);
        msg.setLogin(connection.user.getLogin());
        tabCount--;
        sendToServer(HandleXml.marshalling1(Message.class, msg));
    }
}
