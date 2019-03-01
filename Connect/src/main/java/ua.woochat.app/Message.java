package ua.woochat.app;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class Message implements Serializable {
    private String login;
    @XmlElement
    private String password;
    private String onlineUsers;
    private int group;

    private int type;
    public static int REGISTER_TYPE = 0;  // сделать через enum?
    public static int SINGIN_TYPE = 1;
    public static int MESSAGE_TYPE = 2;

    private String message;

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

    public void setOnlineUsers(String onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
