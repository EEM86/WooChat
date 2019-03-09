package ua.woochat.server.model;

import ua.woochat.app.HandleXml;
import ua.woochat.app.HistoryMessage;
import ua.woochat.app.UsersAndGroups;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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
    @XmlElementWrapper(name="Users-List", nillable = true)
    @XmlElement(name="user")
    private Set<String> usersList = new LinkedHashSet<>();

    @XmlElementWrapper(name="Online-Users-List", nillable = true)
    @XmlElement(name="Online-user")
    private Set<String> onlineUsersList = new LinkedHashSet<>();

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

    public Set<String> getUsersList() {
        return usersList;
    }

    public void addUser (String login){
        usersList.add(login);
        saveGroup();
    }

    public void removeUser (String login){
        usersList.remove(login);
        saveGroup();
    }

    public Set<String> getOnlineUsersList () {
        return onlineUsersList;
    }

    public void addOnlineUser (String login){
        onlineUsersList.add(login);
        saveGroup();
    }

    public void removeOnlineUser (String login){
        onlineUsersList.remove(login);
        saveGroup();
    }

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
