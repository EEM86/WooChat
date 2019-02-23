package ua.woochat.app;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class User {
    @XmlElement
    private static int id = 0;
    @XmlElement
    private String login;
    @XmlElement
    private String password;

   /* @XmlAttribute
    private int type;
    public static int REGISTER_TYPE = 0;
    public static int SINGIN_TYPE = 1;*/

    private enum Gender {
        MALE, FEMALE
    }
    private Gender gender;
    private boolean admin;
    private boolean isBanned;
    private ArrayList group = null;


    public User() {

    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        id++;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
        admin = true;  //admin по умолчанию false, помоему здесь надо присвоить true
    }

    public boolean isBan() {
        return isBanned;
    }

    public void setBan(boolean ban) {
        this.isBanned = isBanned;
    }
}
