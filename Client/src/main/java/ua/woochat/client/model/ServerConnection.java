package ua.woochat.client.model;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.ConnectionAgent;
import ua.woochat.app.HandleXml;
import ua.woochat.app.Message;

import javax.xml.bind.JAXBException;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerConnection implements ConnectionAgent {

    private Socket socket;
    private BufferedReader reader;
    private Connection connection;

    private ActionListener chatFormListener;
    private Message message;
    final static Logger logger = Logger.getLogger(ServerConnection.class);
    private HandleXml handleXml = new HandleXml();

    public ServerConnection(ActionListener chatFormListener){

        this.chatFormListener = chatFormListener;

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
                System.out.println("Пользователь успешно создан! Получаем список пользователей. Вход.");
            } else {
                System.out.println("Пользователь с таким именем уже существует!");
            }
        }

        // вход
        if (message.getType() == 1) {
            if (message.getMessage().equals("true")) {
                System.out.println("Вход! Получаем список пользователей.");
            } else {
                System.out.println("Неверно введен логин или пароль!");
            }
        }

        // сообщение
        if (message.getType() == 2) {
            System.out.println("Получаем сообщению в соответствующую группу");
        }
        //chatFormListener.sendToChat(text);
    }

}
