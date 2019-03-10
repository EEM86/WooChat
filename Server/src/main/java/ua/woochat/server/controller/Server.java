package ua.woochat.server.controller;

import org.apache.log4j.Logger;
import ua.woochat.app.*;
import ua.woochat.server.model.ConfigServer;
import ua.woochat.server.model.Group;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Server implements ConnectionAgent {

    final static Logger logger = Logger.getLogger(Server.class);
    private static Server server;
    private Set<Connection> connections = new LinkedHashSet<>();
    private Message message;
    private ArrayList<User> listRegisteredUsers = new ArrayList<>();
    private Set<File> listFilesUsers = new HashSet<>();
    private User user;
    private Connection connection;
    private Map<Integer, ArrayList<String>> onlineUsers = new HashMap<>();
    ServerSocket serverConnectSocket;
    ServerSocket serverChattingSocket;
    public LinkedHashSet<Group> groupsList = new LinkedHashSet<>();

    /**
     * Constructor creates Server socket which waits for connections.
     */
    private Server() {
        ConfigServer.getConfigServer();
        logger.debug("Server starting ....");
        //verifyUsersActivityTimer();

        try {
            serverConnectSocket = new ServerSocket(ConfigServer.getPort("portconnection"));
            serverChattingSocket = new ServerSocket(ConfigServer.getPort("portchatting"));
            final Group groupMain = new Group("group000");
            groupsList.add(groupMain);
            while (true) {
                try {
                    Socket clientConnectionSocket = serverConnectSocket.accept();
                    connection = new Connection(this, clientConnectionSocket);
                        logger.debug("Client's socket was accepted: [" + clientConnectionSocket.getInetAddress().getHostAddress()
                                + ":" + clientConnectionSocket.getPort() + "]. Connection success.");
                } catch (IOException e) {
                    logger.error("Connection exception " + e);
                }
            }
        } catch (IOException e) {
            //System.out.println("ConfigServer.setUserId(user.getId());" + user.getId());
            logger.error("Server socket exception " + e);
        }

    }

    final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

    public static Server startServer() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    public synchronized boolean userCreated(Connection data) {
        return true;
    }

    @Override
    public synchronized void connectionCreated(Connection data) {
        connections.add(data);
        Set<String> currentUserGroups = data.user.getGroups();
        for (String entry: currentUserGroups) {
            for (Group group : groupsList) {
                if (entry.equalsIgnoreCase(group.getGroupID())) {
                    //group.addUser(data);   -- меняем объект connection на стрингу с логином
                    group.addUser(data.user.getLogin());
                    logger.debug("Users in group \"" + group.getGroupID() + "\": ");
                    group.getUsersList().stream().forEach(x -> System.out.println(x)); //печатает в консоль список юзеров в группе
                }
            }
        }
    }

    @Override
    public synchronized void connectionDisconnect(Connection data) {
        connections.remove(data);
        String deletedUser = "";
        for (Group g: groupsList) {
            //for (Connection entry: g.getUsersList()) {     -- меняем объект connection на стрингу с логином
            for (String entry: g.getUsersList()) {
                //if (data.user.getLogin().equals(entry.user.getLogin())) { -- меняем объект connection на стрингу с логином
                if (data.user.getLogin().equals(entry)) {
                    deletedUser = entry;
                    g.removeUser(entry);
                    break;
                }
            }
        }
        logger.debug("Sending to all info about connection closed");
        Message msg = new Message(11, " has disconnected.");
        msg.setLogin(deletedUser);
        logger.debug("SERVER: user before ==11"  + msg.getLogin());
        //msg.setGroupID("group000");
        sendToAll(HandleXml.marshallingWriter(Message.class, msg));
        data.disconnect();
        //receivedMessage("Client disconnected " + data);
    }

    @Override
    public void receivedMessage(Connection data, String text) { //добавить синхронайзд
        connection = data;
        try {
            message = HandleXml.unMarshallingMessage(text);
        } catch (JAXBException e) {
            logger.error("unMarshallingMessage " + e);
        }
        // регистрация
        if (message.getType() == 0) {
            Message messageSend = new Message(0,"");
            //        messageSend.setType(0);
            if (verificationName(message.getLogin())) { // проверка существует ли имя
                connection.user = new User(message.getLogin(), message.getPassword());
                connection.user.addGroup("group000");
               // groupsList.iterator().next().addUser(connection);
                connectionCreated(connection);
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("true, port=" + ConfigServer.getPort("portchatting"));
                messageSend.setGroupList(getOnlineUsers());
                connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, messageSend));
                moveToChattingSocket();
            } else {
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("false");
                connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, messageSend));
            }
        }

        // вход
        else if (message.getType() == 1) {
            Message messageSend = new Message(1,"");
            //messageSend.setType(1);
            if (verificationSingIn(message.getLogin(), message.getPassword())) { // проверка существует ли имя
                connection.user = new User(message.getLogin(), message.getPassword());
               // connection.user.addGroup("group000");                 // сэтим юзеру главный чат как группу, переделать стрингу
                connection.user.setGroups(user.getGroups());
                connection.user.toString();
                connectionCreated(connection);
                messageSend.setLogin(message.getLogin());
                messageSend.setGroupID("group000");                          // переделать стрингу

                Queue<HistoryMessage> historyMessages =  groupSingIn(groupsList.iterator().next());
                for (HistoryMessage entry: historyMessages) {
                    System.out.println("history: " + entry.toString());
                }
                //messageSend.setGroupList(connection.user.getGroups());                          // переделать стрингу
                messageSend.setMessage("true, port=" + ConfigServer.getPort("portchatting"));
                messageSend.setGroupList(getOnlineUsers());
                connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, messageSend)); // format of message: <?xml version="1.0" encoding="UTF-8" standalone="yes"?><message><password>1qa</password><login>Zhe</login><type>1</type></message>
                moveToChattingSocket();
            } else {
                messageSend.setLogin(message.getLogin());
                messageSend.setMessage("false");
                connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, messageSend));
            }
        }

        // сообщение
        else if (message.getType() == 2)  {
            if (message.getMessage().startsWith("/")) {
                if (message.getLogin().equals(ConfigServer.getAdmin())) {

//                    Message msg = new Message(2, "");
//                    msg.setLogin(message.getLogin());
//                    msg.setGroupID(message.getGroupID());
//                    logger.debug("Message group ID: " + message.getGroupID());

                    if (message.getMessage().startsWith("/kick")) {
                        String[] whoKicked = message.getMessage().split(" ");



                        for (Group g : groupsList) {
                            if (g.getGroupID().equals(message.getGroupID())) {
                                for (String c : g.getUsersList()) {
                                    if (whoKicked[1].equals(c)) {
                                        for (Connection findUser : connections) {
                                            if (findUser.user.getLogin().equals(c)) {
                                                Message msg = new Message(13, "");
                                                msg.setLogin(findUser.user.getLogin());
                                                msg.setGroupID(g.getGroupID());
                                                logger.debug("Сервер отправляет в ServerConnection ==13 логин того кого надо кикнуть и группу откуда: " + g.getGroupID());
                                                findUser.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
//                                                findUser.user.removeGroup(g.getGroupID());
//                                                msg.setMessage("Admin kicked " + c);
//                                                msg.setMessage("Was kicked by admin");
//                                                findUser.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
//                                                g.removeUser(c);
//                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
//                            sendToAllGroup(g.getGroupID(), HandleXml.marshallingWriter(Message.class, msg));
//
//                            ArrayList<String> res = new ArrayList<>();
//                            for (String c2 : g.getUsersList()) {
//                                logger.debug("Список юзеров после админовского кика " + c2);
//                                res.add(c2);
//                            }
//
//                            msg.setGroupList(res);
//                            msg.setType(3);
//                            sendToAllGroup(g.getGroupID(), HandleXml.marshallingWriter(Message.class, msg));
//                        }

                    }
                //connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                } else {

                }
            } else {
    //            Message messageSend = new Message(2, message.getMessage());
    //            messageSend.setLogin(message.getLogin());
                Set<String> result = null;
                //Set<Connection> result = null;   -- меняем объект Коннекшн на стрингу логина
                //Set<String> result = null;
                for (Group entry: groupsList) {
                    if (entry.getGroupID().equalsIgnoreCase(message.getGroupID())) {
                        logger.debug("time now " + new Date());
                        HistoryMessage historyMessage = new HistoryMessage(message.getLogin(), message.getMessage());
                        entry.addToListMessage(historyMessage);
                        result = entry.getUsersList();
                        //for (Connection c: result) {                    -- меняем объект Коннекшн на стрингу логина
    //                        c.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
    //                        logger.debug("Server sent to: [" + c.user.getLogin() + "] message: \"" + message.getMessage() + "\"");
    //                    }
                        sendToAllGroup(entry.getGroupID(), HandleXml.marshallingWriter(Message.class, message)); // -- меняем объект Коннекшн на стрингу логина
                    }
                }
            }
            logger.debug("Who wrote from server side: " + connection.user.getLogin() + "\n");
            //sendToAll(HandleXml.marshalling1(Message.class, message));
            updateUserActivity(message.getLogin());
        }

        else if (message.getType() == 3) { //обновляет список всех пользователей онлайн в чате
           message.setGroupList(getOnlineUsers());
           sendToAllGroup(message.getGroupID(), HandleXml.marshallingWriter(Message.class, message));
        }

        else if (message.getType() == 4) { //обновляет список пользователей в текущей группе

        }

        else if (message.getType() == 6) {   //приватный чат  + сделать чтобы группы в файл User.xml записывались

            Group group = new Group("group" + getUniqueID());
            //Group group = new Group("group00" + groupsList.size());
            groupsList.add(group);
            message.setGroupID(group.getGroupID());
            ArrayList<String> usersInCurrentGroup = message.getGroupList();
            for (String s: usersInCurrentGroup) {
                for (Connection entry: connections) {
                    if (entry.user.getLogin().equals(s)) {
                        entry.user.addGroup(group.getGroupID());
                        //group.addUser(entry.user.getLogin());
                        //group.addUser(entry);  -- меняем объект Коннекшн на стрингу логина
                        group.addUser(entry.user.getLogin()); // -- меняем объект Коннекшн на стрингу логина
                        entry.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
                        logger.debug("Who is in group list: ");
                        group.getUsersList().stream().forEach(x -> System.out.println(x));
                    }
                }
            }
        }

        else if (message.getType() == 7) { //добавление юзера в приватный чат (где уже общаются как минимум двое)
            logger.debug("Сервер: я принял запрос на добавление: " + message.getLogin() + " в группу " + message.getGroupID());
            ArrayList<String> result = new ArrayList<>(); // заменить Аррей на Сет?

            for (Connection entry: connections) {
                if (entry.user.getLogin().equals(message.getLogin())) {
                    entry.user.addGroup(message.getGroupID());
                    for (Group g : groupsList) {
                        if (g.getGroupID().equals(message.getGroupID())) {
                            g.addUser(entry.user.getLogin());
                            result.addAll(g.getUsersList());
                        }
                    }
                    message.setGroupList(result);
                    logger.debug("SERVER: Спиcок пользователей: ==7:" + message.getGroupList());
                    entry.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
                }
            }

            Message msg = new Message();
            ArrayList<String> newUserList = new ArrayList<>();

            for (String userLogin: result){
                newUserList.add(userLogin);
            }

            msg.setGroupList(newUserList);

            msg.setType(12);

            msg.setGroupID(message.getGroupID());
            msg.setGroupTitle(message.getGroupTitle());

            result.remove(message.getLogin());
            for (String users: result){
                for (Connection entry: connections){
                    if(entry.user.getLogin().equals(users)){
                        logger.debug("SERVER: Спиcок пользователей: ==12:" + msg.getGroupList());
                        entry.sendToOutStream(HandleXml.marshallingWriter(Message.class, msg));
                    }
                }
            }
        }

        else if (message.getType() == 8) { // возвращает список онлайн юзеров, которые не состоят в текущей группе
            ArrayList<String> result = new ArrayList<>();
            for (Group g: groupsList) {  //groupsList - список всех групп, которые есть в WooChat
                if (message.getGroupID().equals(g.getGroupID())) {
                    //Set<Connection> usersInGroup = g.getUsersList();  -- меняем объект Коннекшн на стрингу логина
                    //Set<Connection> tmp = new LinkedHashSet<>(connections); //connections - это список всех соединений(по сути клиентов), которые подключены к серверу
                    //Group mainGroup = groupsList.stream().filter(x -> Objects.equals(x, "group000")).findFirst().get();

                    Set<String> tmp = new LinkedHashSet<>(groupsList.iterator().next().getUsersList());
                    for (String c: g.getUsersList()) {  //  -- меняем объект Коннекшн на стрингу логина
                        if (!tmp.add(c)) {
                            tmp.remove(c);
                        }
                    }
                    for (String c : tmp) {
                        result.add(c);
                        logger.debug("Юзер, не состоящий ни в одной из приватных групп: " + c);
                    }
                }
            }
            message.setGroupList(result); // сэтим эррей онлайн пользователей, которые не являются участниками приватной группы
            connection.sendToOutStream(HandleXml.marshallingWriter(Message.class, message));
        }

        else if (message.getType() == 9) { // отключение пользователя с группы
            // userLeaveGroup(String login, String groupID);  добавить метод для уменьшения кода и повторного использования
            for (Group g: groupsList) {
                if (message.getGroupID().equals(g.getGroupID())) {
                    for (String c : g.getUsersList()) {
                        if (message.getLogin().equals(c)) {
                            for (Connection findUser: connections) {
                                if (findUser.user.getLogin().equals(c)) {
                                    findUser.user.removeGroup(g.getGroupID());
                                }
                            }
                            message.setMessage(c + " has left the " + message.getGroupID());
                            g.removeUser(c);
                            //c.user.getGroups().remove(g.getGroupID());
                            break;
                        }
                    }
                    message.setType(2);
                    sendToAllGroup(g.getGroupID(), HandleXml.marshallingWriter(Message.class, message));


                    message.setGroupList(new ArrayList<>(g.getUsersList()));
                    message.setType(3);
                    sendToAllGroup(g.getGroupID(), HandleXml.marshallingWriter(Message.class, message));
                }
            }
        }

        else if (message.getType() == 11) { // выход из чата крестиком
            connectionDisconnect(connection);
        }
    }

    /**
     * Method generate an unique ID
     * @return String type of unique ID
     */
        private  String getUniqueID() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Date now = new Date();
            String uniqueID = dateFormat.format(now);
            return uniqueID;
        }

    public void sendToAll(String text) { //сделать отправку не стрингой, а месседжем
        for (Connection entry: connections) {
            for (Group g : groupsList) {
                for (String s : g.getUsersList()) {
                    if (entry.user.getLogin().equals(s)) {
                        entry.sendToOutStream(text);
                    }
                }
            }
        }
    }

    public void sendToAllGroup(String groupID, String text) { // отправляет сообщение всем юзерам в текущей группе
        for (Group g: groupsList) {
            if (groupID.equals(g.getGroupID())) {
                for (String line: g.getUsersList()) {
                    for (Connection entry: connections) {
                        if (entry.user.getLogin().equals(line)) {
                            logger.info("Method sendToAllGroup is working now. Who is in this group now: " + line + ", message: " + text);
                            entry.sendToOutStream(text);
                        }
                    }
                }
            }
        }
    }

    private boolean verificationName(String login) {
        String path = new File("").getAbsolutePath();
        File file = new File(path + "/Server/src/main/resources/User/" + login.hashCode() + ".xml");
        if (file.isFile()) {
            return false;
        }
        return true;
    }

    private boolean verificationSingIn(String login, String password) {  //переделать, чтобы выводило нужное сообщение, когда пользователь уже подключен к чату
        String path = new File("").getAbsolutePath();
        File file = new File(path + "/Server/src/main/resources/User/" + login.hashCode() + ".xml");

        if (file.isFile()) {
            user = (User) HandleXml.unMarshalling(file, User.class);
            for (Connection connect : connections) {
                if (connect.user.getLogin().equals(login)) {
                    return false;
                }
            }
            if (password.equals(user.getPassword())) {
                return true;
            }
        }
        return false;
    }

    private Queue<HistoryMessage> groupSingIn(Group group) {  //переделать, чтобы выводило нужное сообщение, когда пользователь уже подключен к чату
        String path = new File("").getAbsolutePath();
        File file = new File(path + "/Server/src/main/resources/Group/" + group.getGroupID() + ".xml");
        Queue<HistoryMessage> historyMessages = null;
        if (file.isFile()) {
            group = (Group) HandleXml.unMarshalling(file, Group.class);
            historyMessages = group.getQueue();
        }
        return historyMessages;
    }

    // files in folder
    public Set<File> filesFromFolder() {
        String path = new File("").getAbsolutePath();
        File folder = new File(path + "/Server/src/main/resources/User");
        File[] folderEntries = folder.listFiles();
        for (File entry : folderEntries) {
            listFilesUsers.add(entry);
            //listRegisteredUsers.add(handleXml.unMarshalling(entry));
        }
        return listFilesUsers;
    }

    private void moveToChattingSocket() {
        try {
            Socket clientChatSocket = serverChattingSocket.accept();
            logger.debug("Connection has moved to new socket for chatting: " + clientChatSocket.getInetAddress() + " " +clientChatSocket.getLocalPort());
            connection.setSocket(clientChatSocket);
        } catch (IOException e) {
            logger.error("Error socket creation" + e);
        }
    }
/*
Временно сделал вывод строки, в дальнейшем хмл файл со списком надо будет передавать.
 */
    public ArrayList<String> getOnlineUsers() { // возвращает список всех онлайн пользователей
        ArrayList<String> result = new ArrayList<>();
        for (Connection entry : connections) {
            result.add(entry.user.getLogin());
        }
        return result;
    }

    /**
     * Метод запускает таймер отслеживания активности пользователей
     */
    private void verifyUsersActivityTimer() {
        logger.debug("Таймер запустился");
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                logger.debug("SERVER: Один раз в минуту проверяю активность всех пользователей");
                for (Connection entry : connections) {
                    if (connection != null){
                        //тут реализация которая позволяет отключить пользователей неактивность которых
                        //превышает лимит
                        System.out.println("Пользователь: " + connection.user.getLogin() + " последняя активность: " +
                                connection.user.getLastActivity());
                    }
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * В этом методе обновляеться время последнего действия конкретного пользователя,
     * в данном случае по отправке сообщения
     * @param user имя пользователя или конкретный конекшн
     */
    private void updateUserActivity(String user){
        System.out.println("SERVER: Обновляю активность пользователю: " + user);

    }
}
