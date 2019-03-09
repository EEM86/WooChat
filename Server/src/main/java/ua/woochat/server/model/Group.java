package ua.woochat.server.model;

import ua.woochat.app.Connection;
import ua.woochat.app.HandleXml;
import ua.woochat.app.HistoryMessage;
import ua.woochat.app.UsersAndGroups;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

//@XmlType(propOrder = {"groupID","usersList", "queue"})
@XmlRootElement
public class Group implements UsersAndGroups {
    @XmlElement
    private String groupID;
    @XmlElement
    private Set<String> usersList = new LinkedHashSet<>(); // tmp field
    //private Set<Connection> usersList = new LinkedHashSet<>(); // tmp field
    @XmlTransient
    HistoryMessage historyMessage;
    @XmlElement
    private static ArrayBlockingQueue<HistoryMessage> queue = null;
   // private String groupName;
   // private User adminGroup;


    public Group() {
    }

    public Group(String idGroup) {
        this.groupID = idGroup;
        queue = new ArrayBlockingQueue<HistoryMessage>(20);
        saveGroup();
    }


   /* public Group(String groupName, String idGroup) {
        this.groupName = groupName;
        this.groupID = idGroup;
    }*/

//public Set<Connection> getUsersList() {   --------- меняем на сэт стрингов
//        return usersList;
//    }
    public Set<String> getUsersList() {
        return usersList;
    }

//    public void addUser (Connection connection){ --------- меняем на сэт стрингов
//        usersList.add(connection);
//        saveGroup();
//    }

    public void addUser (String login){
        usersList.add(login);
        saveGroup();
    }

//    public void removeUser (Connection connection){ --------- меняем на сэт стрингов
//        usersList.remove(connection);
//        saveGroup();
//    }

    public void removeUser (String login){
        usersList.remove(login);
        saveGroup();
    }
/*    public Set<String> getUsersList() {
        return usersList;
    }

    public void addUser (String usersLogin){
        usersList.add(usersLogin);
        saveGroup();
    }

    public void removeUser (String usersLogin){
        usersList.remove(usersLogin);
        saveGroup();
    }*/

    public String getGroupID() {
        return groupID;
    }

    public static ArrayBlockingQueue<HistoryMessage> getQueue() {
        return queue;
    }

    public void addToListMessage(HistoryMessage historyMessage) {
        if (!queue.offer(historyMessage)) {
            queue.poll();
            queue.offer(historyMessage);
        }
        saveGroup();
    }

    public void saveGroup() {
        String path = new File("").getAbsolutePath();
        File file = new File(path + "/Server/src/main/resources/Group/" + this.getGroupID() + ".xml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream stream = new FileOutputStream(file);
            HandleXml.marshalling(Group.class, this, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
