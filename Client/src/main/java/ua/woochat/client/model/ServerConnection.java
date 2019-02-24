package ua.woochat.client.model;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.ConnectionAgent;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;
import ua.woochat.client.listeners.LoginFormListener;
import ua.woochat.client.view.ChatForm;
import ua.woochat.client.view.MessageView;
import ua.woochat.client.view.WindowImages;
import ua.woochat.client.view.WindowProperties;

import javax.xml.bind.JAXBException;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerConnection implements ConnectionAgent {

    private Socket socket;
    private BufferedReader reader;
    private Connection connection;

    private LoginFormListener loginFormListener;
    private Message message;
    final static Logger logger = Logger.getLogger(ServerConnection.class);
    private HandleXml handleXml = new HandleXml();
    private WindowProperties windowProperties;
    private WindowImages windowImages;

    private String[] testOnlineList = {"UserAnatoliy", "Bodik", "Shaurma", "Gnom", "Jon Snow (2)", "MARTIN", "Daywalker", "NEITRINO", "ЛЯПOTA", "-ZAUR", "DeHWeT", "NELLY", "Лacкoвaя_пaнтepa", "-CIQAN", "DeLi", "NELLY_FURTADO", "Лacкoвый_Бaкинeц", "-NeMo", "DeaD_GirL", "NEQATI", "Лacтoчкa", "-UREK", "Deart-Wolf", "NERGIZ_132", "Лaпyля"};


    public ServerConnection(LoginFormListener loginFormListener){

        this.loginFormListener = loginFormListener;

        try {

            socket = new Socket(ConfigClient.getServerIP(), ConfigClient.getPortConnection());
            reader = new BufferedReader(new InputStreamReader(System.in));
            this.connection = new Connection(this, socket);
            connectionCreated(connection);

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
    public void receivedMessage(String text) {

        try {
            message = handleXml.unMarshallingMessage(text);
        } catch (JAXBException e) {
            logger.error("unMarshallingMessage " + e);
        }

        // регистрация
        if (message.getType() == 0) {
            if (message.getMessage().equals("true")) {
                loginFormListener.getLoginForm().getLoginWindow().setVisible(false);
                chatWindow(message.getLogin(), testOnlineList);
            } else {
                loginFormListener.getLoginForm().getLoginWindow().setEnabled(false);
                new MessageView("Пользователь с таким именем уже существует!",
                        loginFormListener.getLoginForm().getLoginWindow());
            }
        }

        // вход
        if (message.getType() == 1) {
            if (message.getMessage().equals("true")) {
                loginFormListener.getLoginForm().getLoginWindow().setVisible(false);
                chatWindow(message.getLogin(), testOnlineList);
            } else {
                loginFormListener.getLoginForm().getLoginWindow().setEnabled(false);
                new MessageView("Неверно введен логин или пароль!",
                        loginFormListener.getLoginForm().getLoginWindow());
            }
        }

        // сообщение
        if (message.getType() == 2) {
            System.out.println("Получаем сообщению в соответствующую группу");
        }
        //chatFormListener.sendToChat(text);
    }

    //Окно чата после регистрации/логининга. Пока что сюда передается имя пользователя который вошел.
    // А будет передаваться и список онлайн с айдишниками

    /**
     * Метод создает новое окно чата для авторизированного/зарегистрированного пользователя
     * @param user пользователь который успешно авторизирован
     * @param testOnlineList список онлайн пользователей, которых вернул сервер в ответ на авторизацию
     */
    private void chatWindow(String user, String[] testOnlineList) {
        windowProperties = loginFormListener.getLoginForm().getProperties();
        windowImages = loginFormListener.getLoginForm().getImages();
        new ChatForm(windowProperties, windowImages,user, testOnlineList);
    }

}
