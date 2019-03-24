package ua.woochat.app;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement
public class Message implements Serializable {
    public final static int REGISTER_TYPE = 0;  // сделать через enum?
    public final static int SIGNIN_TYPE = 1;
    public final static int CHATTING_TYPE = 2;
    public final static int UPDATE_USERS_TYPE = 3;
    public final static int PRIVATE_CHAT_TYPE = 6;
    public final static int PRIVATE_GROUP_TYPE = 7;
    public final static int UNIQUE_ONLINE_USERS_TYPE = 8;
    public final static int LEAVE_GROUP_TYPE = 9;
    public final static int PING_TYPE = 10;
    public final static int EXIT_TYPE = 11;
    public final static int TAB_RENAME_TYPE = 12;
    public final static int KICK_TYPE = 13;
    public final static int BAN_TYPE = 99;
    public final static int QUIT_TYPE = 23;
    private String login;
    public static String administrator;
    private String adminName;
    @XmlElement
    private String password;
    private String groupID;
    private String groupTitle;
    private boolean isBanned;

    private int type;
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

    public Message(int type, String login, String groupID) {
        this.login = login;
        this.type = type;
        this.groupID = groupID;
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

    public String getAdminName() {
        return adminName;
    }

    @XmlElement
    public void setAdminName(String admin) {
        this.adminName = admin;
    }
}
