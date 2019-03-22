package ua.woochat.app;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement
public class Message implements Serializable {
    private String login;
    @XmlElement
    private String password;
    private String onlineUsers; //удалить?
    private String groupID;
    private String groupTitle;
    private boolean isBanned;

    private int type;
    public static int REGISTER_TYPE = 0;  // сделать через enum?
    public static int SINGIN_TYPE = 1;
    public static int CHATTING_TYPE = 2;
    public static int UPDATE_USERS_TYPE = 3;
    public static int PRIVATE_CHAT_TYPE = 6;
    public static int PRIVATE_GROUP_TYPE = 7;
    public static int UNIQUE_ONLINE_USERS_TYPE = 8;
    public static int LEAVE_GROUP_TYPE = 9;
    public static int EXIT_TYPE = 11;
    public static int TAB_RENAME_TYPE = 12;
    public static int KICK_TYPE = 13;
    public static int BAN_TYPE = 99;
    public static int QUIT_TYPE = 23;

    private String message;
    private ArrayList<String> groupList = new ArrayList<>();
    private Set<Group> groupListUser = new HashSet<>();

    public Message() {
    }

    public Message(String login, String password, int type) {
        this.login = login;
        this.password = password;
        this.type = type;
    }

    public Message(String login, String password, int type, String message) {
        this.login = login;
        this.password = password;
        this.type = type;
        this.message = message;
    }

    public Message(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public Message(String login, int type, String message) {
        this.login = login;
        this.type = type;
        this.message = message;
    }

    @XmlElement
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @XmlElement
    public int getType() {
        return type;
    }

    @XmlElement
    public String getMessage() {
        return message;
    }

    public void setType(int type) {
        this.type = type;
    }

    @XmlElement
    public String getOnlineUsers() {
        return onlineUsers;
    }

//    public void setOnlineUsers(String onlineUsers) {
//        this.onlineUsers = onlineUsers;
//    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @XmlElement
    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    @XmlElement
    public ArrayList<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<String> groupList) {
        this.groupList = groupList;
    }

    @XmlElement
    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    @XmlElement
    public Set<Group> getGroupListUser() {
        return groupListUser;
    }

    public void setGroupListUser(Set<Group> groupListUser) {
        this.groupListUser = groupListUser;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }
}
