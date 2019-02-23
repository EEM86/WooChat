package ua.woochat.server.controller;

import org.apache.log4j.Logger;
import ua.woochat.app.Connection;
import ua.woochat.app.ConnectionAgent;
import ua.woochat.app.Message;
import ua.woochat.app.User;
import ua.woochat.server.model.ConfigServer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Set;

public class Server implements ConnectionAgent {
    final static Logger logger = Logger.getLogger(Server.class);
    private Set<Connection> connections = new LinkedHashSet<>();
    private Message message;
    /**
     * Constructor creates Server socket which waits for connections.
     */
    public Server() {
        logger.debug("Server is running");
        try (ServerSocket serverSocket = new ServerSocket(ConfigServer.getPortConnection())) {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Connection connection = new Connection(this, clientSocket);
                    //где-то здесь надо проверить логин и пароль, после авторизации вызвать метод connectionCreated
                    // можно ли стартовать тред после проверки или всё же в конструкторе? connection.getThread().start();

                    //if (userCreated(connection)) {
                        connectionCreated(connection);
                        logger.debug("Client's socket was accepted: [" + clientSocket.getInetAddress().getHostAddress()
                                + ":" + clientSocket.getPort() + "]. Connection success.");
                    //}
                } catch (IOException e) {
                    logger.error("Connection exception " + e);
                }
            }
        } catch (IOException e) {
            logger.error("Server socket exception " + e);
        }
    }

    public synchronized boolean userCreated(Connection data) {
        return true;
    }

    @Override
    public synchronized void connectionCreated(Connection data) {
        System.out.println("data" + data);
        connections.add(data);
       // receivedMessage("Client connected " + data);
    }
    @Override
    public synchronized void connectionDisconnect(Connection data) {
        connections.remove(data);
        receivedMessage("Client disconnected " + data);
    }

    @Override
    public void receivedMessage(String text) {
        System.out.println("receivedMessage " + text);
        try {
            message = unMarshalling(text);
            if (message.getType() == 0) {
                if (verificationName(message.getLogin())) { // проверка не существует ли имя
                    User user = new User(message.getLogin(), message.getPassword());
                    saveUser(user);
                } else {

                    System.out.println("Пользователь с таким именем уже существует!");
                }
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (Connection entry : connections) {
            entry.sendToOutStream(text);
        }
    }

    private boolean verificationName(String login) {
        return true;

    }

    private void saveUser(User user) throws FileNotFoundException, JAXBException {
        String path = new File("").getAbsolutePath();
        File file = new File(path + "/Server/src/main/resources/User/" + user.getId() + ".xml");
        System.out.println("object file create");
        try {
            file.createNewFile();
            System.out.println("file create");
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream stream = new FileOutputStream(file);
        marshalling(User.class, user, stream);
    }

    private void marshalling (Class marshClass, Object message, FileOutputStream stream) throws JAXBException {
        //создание объекта Marshaller, который выполняет сериализацию
        JAXBContext context = JAXBContext.newInstance(marshClass);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // сама сериализация
        marshaller.marshal(message, stream);
    }

    private Message unMarshalling (String str) throws JAXBException {
        StringReader reader = new StringReader(str);
        JAXBContext context = JAXBContext.newInstance(Message.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        message = (Message) unmarshaller.unmarshal(reader);
        return message;
    }
}
