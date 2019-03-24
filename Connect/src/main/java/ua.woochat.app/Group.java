package ua.woochat.app;

import org.apache.log4j.Logger;

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
    private final static Logger logger = Logger.getLogger(Group.class);
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
     * Method gets the list of users.
     * @return usersList list of users.
     */
    public Set<String> getUsersList() {
        return usersList;
    }

    /**
     * Method adds one user to the group.
     * @param login login of user
     */
    public void addUser (String login){
        usersList.add(login);
    }

    /**
     * Method removes one user from the group.
     * @param login login of user
     */
    public void removeUser (String login){
        usersList.remove(login);
    }

    /**
     * Method gets the list of online users in the group.
     * @return onlineUsersList list of online users in the group.
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
     * Method gets id of the group.
     * @return groupID id of the group.
     */
    public String getGroupID() {
        return groupID;
    }

    /**
     * Method gets title of the group.
     * @return groupName name of the group.
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Method sets group title.
     * @param groupName name of the group.
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
        this.queue = queue;
    }

    /**
     * Method adds one history message to the list of history messages
     */
    public void addToListMessage(HistoryMessage historyMessage) {
/*        if (queue == null) {
            queue = new ArrayBlockingQueue<HistoryMessage>(20);
        }*/
        if (!queue.offer(historyMessage)) {
            queue.poll();
            queue.offer(historyMessage);
        }
    }

    /**
     * Method saves group in XML file
     * ToDo delete groups without users
     */
    public void saveGroup() {
        File file = new File("Group" + File.separator + this.getGroupID() + ".xml");
        logger.debug("Saving a Group: " + this.getGroupID() + ".xml");
        File directory = new File("Group");
        if (!directory.exists()){
            directory.mkdir();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            logger.error("File has not created", e);
        }
        try {
            FileOutputStream stream = new FileOutputStream(file);
            HandleXml.marshalling(Group.class, this, stream);
        } catch (FileNotFoundException e) {
            logger.error("File not found exceptions ", e);
        }

    }

    /**
     * Method creates a list of user's groups from files in the list of strings
     * @return groupSet list of user groups
     */
    public static Set<Group> groupUser (Set<String> groups) {
        Set<Group> groupSet = new LinkedHashSet<>();
        File file;
        for (String entry : groups) {
            file = new File("Group" + File.separator + entry + ".xml");
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
        File file = new File("Group" + File.separator + group.getGroupID() + ".xml");
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
     * Method checks if the object equals to other object.
     * @return true if objects are equals.
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
     * Method returns hashCode of the group.
     * @return int hashCode of group.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        return result = prime * result + groupID.hashCode();
    }
}