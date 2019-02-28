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
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.*;

public class ServerConnection implements ConnectionAgent {

    private Socket socket;
    private Socket socketChatting;
    public Connection connection; //change later

    private LoginFormListener loginFormListener;
    private ChatForm chatForm;
    private Message message;
    private WindowProperties windowProperties;
    private WindowImages windowImages;
    private ServerConnection serverConnection;

    final static Logger logger = Logger.getLogger(ServerConnection.class);

    private ArrayList<String> testOnlineList;

    public ServerConnection(LoginFormListener loginFormListener){

        this.loginFormListener = loginFormListener;

        try {
            socket = new Socket(ConfigClient.getServerIP(), ConfigClient.getPortConnection());
            this.connection = new Connection(this, socket);
            //connectionCreated(connection);

        } catch (Exception e) {

        }
    }

    public void sendToServer(String text){
        connection.sendToOutStream(text);
    }

    @Override
    public void connectionCreated(Connection data) {
        logger.debug("User connected " + data.user.getLogin());
        logger.debug("users in chat: " + testOnlineList.size());
        testOnlineList.add(connection.user.getLogin());
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

        // регистрация
        if (message.getType() == 0) {
            if (message.getMessage().startsWith("true")) {
                int chattingPort = Integer.parseInt(message.getMessage().substring(message.getMessage().indexOf('=')+1));
                moveToChattingSocket(chattingPort);
                loginFormListener.getLoginForm().getLoginWindow().setVisible(false);
                chatWindow(message.getLogin(), testOnlineList, serverConnection);
            } else {
                loginFormListener.getLoginForm().getLoginWindow().setEnabled(false);
                new MessageView("Пользователь с таким именем уже существует!",
                        loginFormListener.getLoginForm().getLoginWindow());
            }
        }

        // вход
        if (message.getType() == 1) {
            if (message.getMessage().startsWith("true")) {

                System.out.println("Постоянная авторизация");

                int chattingPort = Integer.parseInt(message.getMessage().substring(message.getMessage().indexOf('=')+1));
                moveToChattingSocket(chattingPort);
                connection.user =  new User(message.getLogin(), message.getPassword());
                connectionCreated(connection);
                connection.user =  new User(message.getLogin(), message.getPassword());
                testOnlineList = new ArrayList(Arrays.asList(message.getOnlineUsers().split("\\s")));
                //connectionCreated(connection);
                loginFormListener.getLoginForm().getLoginWindow().setVisible(false);
                chatWindow(message.getLogin(), testOnlineList, this);
            } else {
                loginFormListener.getLoginForm().getLoginWindow().setEnabled(false);
                new MessageView("Неверно введен логин или пароль!",
                        loginFormListener.getLoginForm().getLoginWindow());
            }
        }

        // сообщение
        if (message.getType() == 2) {
            System.out.println("Type:" + message.getType() + " Message:" + message.getMessage());
            sendToChat(message.getMessage());
        }
        //chatFormListener.sendToChat(text);

        if (message.getType() == 3) {                 //обновляет список юзеров онлайн
        }
    }

    //Окно чата после регистрации/логининга. Пока что сюда передается имя пользователя который вошел.
    // А будет передаваться и список онлайн с айдишниками

    /**
     * Метод создает новое окно чата для авторизированного/зарегистрированного пользователя
     * @param user пользователь который успешно авторизирован
     * @param testOnlineList список онлайн пользователей, которых вернул сервер в ответ на авторизацию
     */
    private void chatWindow(String user, Set<String> testOnlineList, ServerConnection serverConnection) {
        windowProperties = loginFormListener.getLoginForm().getProperties();
        windowImages = loginFormListener.getLoginForm().getImages();
        chatForm = new ChatForm(windowProperties, windowImages, user, testOnlineList, serverConnection);
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
}
