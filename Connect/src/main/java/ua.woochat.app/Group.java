package ua.woochat.app;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

@XmlRootElement
public class Group implements UsersAndGroups {
    @XmlElement
    private String groupID;

    private String groupName;
    @XmlElementWrapper(name="Users-List", nillable = true)
    @XmlElement(name="user")
    private Set<String> usersList = new LinkedHashSet<>();

    private Set<String> onlineUsersList = new LinkedHashSet<>();

    private Queue<HistoryMessage> queue = null;

    public Group() {
    }

    public Group(String idGroup, String groupName) {
        this.groupID = idGroup;
        this.groupName = groupName;
        queue = new ArrayBlockingQueue<HistoryMessage>(20);
    }

    /**
     * Method gets the list of users
     * @return usersList list of users
     */
    public Set<String> getUsersList() {
        return usersList;
    }

    /**
     * Method add one user to the group
     * @param login login of user
     */
    public void addUser (String login){
        usersList.add(login);
    }

    /**
     * Method remove one user to the group
     * @param login login of user
     */
    public void removeUser (String login){
        usersList.remove(login);
    }

    /**
     * Method gets the list of online users in group
     * @return onlineUsersList list of online users in group
     */
    public Set<String> getOnlineUsersList () {
        return onlineUsersList;
    }

    /**
     * Method adds one user to the list of online users of the group
     * @param login login of user
     */
    public void addOnlineUser (String login){
        onlineUsersList.add(login);
    }

    /**
     * Method removes one user from the list of online users of the group
     * @param login login of user
     */
    public void removeOnlineUser (String login){
        onlineUsersList.remove(login);
    }

    /**
     * Method gets id of group
     * @return groupID id of group
     */
    public String getGroupID() {
        return groupID;
    }

    /**
     * Method gets title of group
     * @return groupName name of group
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Method sets title group
     * @param groupName name of group
     */
    @XmlElement(name="Title-group")
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Method gets the list of history messages
     * @return queue list of history messages
     */
    @XmlElement(name="History-Message")
    public Queue<HistoryMessage> getQueue() {
        return queue;
    }

    public void setQueue(Queue<HistoryMessage> queue) {
        this.queue = queue;;
    }

    /**
     * Method adds one history message to the list of history messages
     */
    public void addToListMessage(HistoryMessage historyMessage) {
        if (!queue.offer(historyMessage)) {
            queue.poll();
            queue.offer(historyMessage);
        }
    }

    /**
     * Method saves group in XML file
     */
    public void saveGroup() {
        String path = new File("").getAbsolutePath();
//        File tmp = new File("resources/Group/" + this.getGroupID() + ".xml");
//        String path = tmp.getAbsolutePath();
//        File file = new File(path + tmp);
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

    /**
     * Method creates a list of user groups from files in the list of strings
     * @return groupSet list of user groups
     */
    public static Set<Group> groupUser (Set<String> groups) {
        Set<Group> groupSet = new LinkedHashSet<>();
        String path = new File("").getAbsolutePath();
        File file;
        for (String entry : groups) {
            file = new File(path + "/Server/src/main/resources/Group/" + entry + ".xml");
            if (file.isFile()) {
                //newGroup = (Group) HandleXml.unMarshalling(file, Group.class);
                //newGroup = (Group) HandleXml.unMarshalling(file, Group.class);
                //groupSet.add(newGroup);
                Group group = (Group) HandleXml.unMarshalling(file, Group.class);
                groupSet.add(group);
            }
        }
        return groupSet;
    }

    /**
     * Method creates a list of historical messages for one group
     * @return historyMessages list of historical messages
     */
    public static Queue<HistoryMessage> groupSingIn(Group group) {  //переделать, чтобы выводило нужное сообщение, когда пользователь уже подключен к чату
        String path = new File("").getAbsolutePath();
        File file = new File(path + "/Server/src/main/resources/Group/" + group.getGroupID() + ".xml");
        Queue<HistoryMessage> historyMessages = null;
        if (file.isFile()) {
            group = (Group) HandleXml.unMarshalling(file, Group.class);
            historyMessages = group.getQueue();
        }
        return historyMessages;
    }

    /**
     * Method group to String
     * @return String for group
     */
    @Override
    public String toString() {

        return "Group{" +
                "groupID='" + groupID + '\'' +
                ", usersList=" + usersList +
                ", onlineUsersList=" + onlineUsersList +
                ", message=" + getQueue() +
                '}';
    }

    /**
     * Method object equals other object
     * @return true if objects are equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Group compare = (Group) obj;
        return ((groupID.equals(compare.groupID)) && (this.hashCode() == compare.hashCode()));
    }

    /**
     * Method hashCode of group
     * @return int hashCode of group
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        return result = prime * result + groupID.hashCode();
    }
}